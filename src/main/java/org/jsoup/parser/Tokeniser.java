package org.jsoup.parser;

import java.util.Arrays;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Entities;

final class Tokeniser {
    private static final char[] notCharRefCharsSorted = new char[]{'\t', '\n', '\r', '\f', ' ', '<', '&'};
    static final char replacementChar = '�';
    Character charPending = new Character();
    private StringBuilder charsBuilder = new StringBuilder(1024);
    private String charsString = null;
    private final int[] codepointHolder = new int[1];
    Comment commentPending = new Comment();
    StringBuilder dataBuffer = new StringBuilder(1024);
    Doctype doctypePending = new Doctype();
    private Token emitPending;
    EndTag endPending = new EndTag();
    private final ParseErrorList errors;
    private boolean isEmitPending = false;
    private String lastStartTag;
    private final int[] multipointHolder = new int[2];
    private final CharacterReader reader;
    private boolean selfClosingFlagAcknowledged = true;
    StartTag startPending = new StartTag();
    private TokeniserState state = TokeniserState.Data;
    Tag tagPending;

    static {
        Arrays.sort(notCharRefCharsSorted);
    }

    Tokeniser(CharacterReader reader, ParseErrorList errors) {
        this.reader = reader;
        this.errors = errors;
    }

    Token read() {
        if (!this.selfClosingFlagAcknowledged) {
            error("Self closing flag not acknowledged");
            this.selfClosingFlagAcknowledged = true;
        }
        while (!this.isEmitPending) {
            this.state.read(this, this.reader);
        }
        if (this.charsBuilder.length() > 0) {
            String str = this.charsBuilder.toString();
            this.charsBuilder.delete(0, this.charsBuilder.length());
            this.charsString = null;
            return this.charPending.data(str);
        } else if (this.charsString != null) {
            Token token = this.charPending.data(this.charsString);
            this.charsString = null;
            return token;
        } else {
            this.isEmitPending = false;
            return this.emitPending;
        }
    }

    void emit(Token token) {
        Validate.isFalse(this.isEmitPending, "There is an unread token pending!");
        this.emitPending = token;
        this.isEmitPending = true;
        if (token.type == TokenType.StartTag) {
            StartTag startTag = (StartTag) token;
            this.lastStartTag = startTag.tagName;
            if (startTag.selfClosing) {
                this.selfClosingFlagAcknowledged = false;
            }
        } else if (token.type == TokenType.EndTag && ((EndTag) token).attributes != null) {
            error("Attributes incorrectly present on end tag");
        }
    }

    void emit(String str) {
        if (this.charsString == null) {
            this.charsString = str;
            return;
        }
        if (this.charsBuilder.length() == 0) {
            this.charsBuilder.append(this.charsString);
        }
        this.charsBuilder.append(str);
    }

    void emit(char[] chars) {
        emit(String.valueOf(chars));
    }

    void emit(int[] codepoints) {
        emit(new String(codepoints, 0, codepoints.length));
    }

    void emit(char c) {
        emit(String.valueOf(c));
    }

    TokeniserState getState() {
        return this.state;
    }

    void transition(TokeniserState state) {
        this.state = state;
    }

    void advanceTransition(TokeniserState state) {
        this.reader.advance();
        this.state = state;
    }

    void acknowledgeSelfClosingFlag() {
        this.selfClosingFlagAcknowledged = true;
    }

    int[] consumeCharacterReference(Character additionalAllowedCharacter, boolean inAttribute) {
        if (this.reader.isEmpty()) {
            return null;
        }
        if (additionalAllowedCharacter != null && additionalAllowedCharacter.charValue() == this.reader.current()) {
            return null;
        }
        if (this.reader.matchesAnySorted(notCharRefCharsSorted)) {
            return null;
        }
        int[] codeRef = this.codepointHolder;
        this.reader.mark();
        if (this.reader.matchConsume("#")) {
            boolean isHexMode = this.reader.matchConsumeIgnoreCase("X");
            String numRef = isHexMode ? this.reader.consumeHexSequence() : this.reader.consumeDigitSequence();
            if (numRef.length() == 0) {
                characterReferenceError("numeric reference with no numerals");
                this.reader.rewindToMark();
                return null;
            }
            if (!this.reader.matchConsume(";")) {
                characterReferenceError("missing semicolon");
            }
            int charval = -1;
            try {
                charval = Integer.valueOf(numRef, isHexMode ? 16 : 10).intValue();
            } catch (NumberFormatException e) {
            }
            if (charval == -1 || ((charval >= 55296 && charval <= 57343) || charval > 1114111)) {
                characterReferenceError("character outside of valid range");
                codeRef[0] = 65533;
                return codeRef;
            }
            codeRef[0] = charval;
            return codeRef;
        }
        String nameRef = this.reader.consumeLetterThenDigitSequence();
        boolean looksLegit = this.reader.matches(';');
        boolean found = Entities.isBaseNamedEntity(nameRef) || (Entities.isNamedEntity(nameRef) && looksLegit);
        if (!found) {
            this.reader.rewindToMark();
            if (looksLegit) {
                characterReferenceError(String.format("invalid named referenece '%s'", new Object[]{nameRef}));
            }
            return null;
        } else if (inAttribute && (this.reader.matchesLetter() || this.reader.matchesDigit() || this.reader.matchesAny('=', '-', '_'))) {
            this.reader.rewindToMark();
            return null;
        } else {
            if (!this.reader.matchConsume(";")) {
                characterReferenceError("missing semicolon");
            }
            int numChars = Entities.codepointsForName(nameRef, this.multipointHolder);
            if (numChars == 1) {
                codeRef[0] = this.multipointHolder[0];
                return codeRef;
            } else if (numChars == 2) {
                return this.multipointHolder;
            } else {
                Validate.fail("Unexpected characters returned for " + nameRef);
                return this.multipointHolder;
            }
        }
    }

    Tag createTagPending(boolean start) {
        this.tagPending = start ? this.startPending.reset() : this.endPending.reset();
        return this.tagPending;
    }

    void emitTagPending() {
        this.tagPending.finaliseTag();
        emit(this.tagPending);
    }

    void createCommentPending() {
        this.commentPending.reset();
    }

    void emitCommentPending() {
        emit(this.commentPending);
    }

    void createDoctypePending() {
        this.doctypePending.reset();
    }

    void emitDoctypePending() {
        emit(this.doctypePending);
    }

    void createTempBuffer() {
        Token.reset(this.dataBuffer);
    }

    boolean isAppropriateEndTagToken() {
        return this.lastStartTag != null && this.tagPending.name().equalsIgnoreCase(this.lastStartTag);
    }

    String appropriateEndTagName() {
        if (this.lastStartTag == null) {
            return null;
        }
        return this.lastStartTag;
    }

    void error(TokeniserState state) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpected character '%s' in input state [%s]", Character.valueOf(this.reader.current()), state));
        }
    }

    void eofError(TokeniserState state) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Unexpectedly reached end of file (EOF) in input state [%s]", state));
        }
    }

    private void characterReferenceError(String message) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), "Invalid character reference: %s", message));
        }
    }

    private void error(String errorMsg) {
        if (this.errors.canAddError()) {
            this.errors.add(new ParseError(this.reader.pos(), errorMsg));
        }
    }

    boolean currentNodeInHtmlNS() {
        return true;
    }

    String unescapeEntities(boolean inAttribute) {
        StringBuilder builder = new StringBuilder();
        while (!this.reader.isEmpty()) {
            builder.append(this.reader.consumeTo('&'));
            if (this.reader.matches('&')) {
                this.reader.consume();
                int[] c = consumeCharacterReference(null, inAttribute);
                if (c == null || c.length == 0) {
                    builder.append('&');
                } else {
                    builder.appendCodePoint(c[0]);
                    if (c.length == 2) {
                        builder.appendCodePoint(c[1]);
                    }
                }
            }
        }
        return builder.toString();
    }
}
