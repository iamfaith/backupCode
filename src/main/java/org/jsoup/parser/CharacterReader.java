package org.jsoup.parser;

import java.util.Arrays;
import java.util.Locale;
import org.jsoup.helper.Validate;

public final class CharacterReader {
    static final char EOF = '￿';
    private static final int maxCacheLen = 12;
    private final char[] input;
    private final int length;
    private int mark = 0;
    private int pos = 0;
    private final String[] stringCache = new String[512];

    public CharacterReader(String input) {
        Validate.notNull(input);
        this.input = input.toCharArray();
        this.length = this.input.length;
    }

    public int pos() {
        return this.pos;
    }

    public boolean isEmpty() {
        return this.pos >= this.length;
    }

    public char current() {
        return this.pos >= this.length ? EOF : this.input[this.pos];
    }

    char consume() {
        char val = this.pos >= this.length ? EOF : this.input[this.pos];
        this.pos++;
        return val;
    }

    void unconsume() {
        this.pos--;
    }

    public void advance() {
        this.pos++;
    }

    void mark() {
        this.mark = this.pos;
    }

    void rewindToMark() {
        this.pos = this.mark;
    }

    String consumeAsString() {
        char[] cArr = this.input;
        int i = this.pos;
        this.pos = i + 1;
        return new String(cArr, i, 1);
    }

    int nextIndexOf(char c) {
        for (int i = this.pos; i < this.length; i++) {
            if (c == this.input[i]) {
                return i - this.pos;
            }
        }
        return -1;
    }

    int nextIndexOf(CharSequence seq) {
        char startChar = seq.charAt(0);
        int offset = this.pos;
        while (offset < this.length) {
            if (startChar != this.input[offset]) {
                do {
                    offset++;
                    if (offset >= this.length) {
                        break;
                    }
                } while (startChar != this.input[offset]);
            }
            int i = offset + 1;
            int last = (seq.length() + i) - 1;
            if (offset < this.length && last <= this.length) {
                int j = 1;
                while (i < last && seq.charAt(j) == this.input[i]) {
                    i++;
                    j++;
                }
                if (i == last) {
                    return offset - this.pos;
                }
            }
            offset++;
        }
        return -1;
    }

    public String consumeTo(char c) {
        int offset = nextIndexOf(c);
        if (offset == -1) {
            return consumeToEnd();
        }
        String consumed = cacheString(this.pos, offset);
        this.pos += offset;
        return consumed;
    }

    String consumeTo(String seq) {
        int offset = nextIndexOf((CharSequence) seq);
        if (offset == -1) {
            return consumeToEnd();
        }
        String consumed = cacheString(this.pos, offset);
        this.pos += offset;
        return consumed;
    }

    public java.lang.String consumeToAny(char... r8) {
        /* JADX: method processing error */
/*
Error: java.lang.IndexOutOfBoundsException: bitIndex < 0: -1
	at java.util.BitSet.get(BitSet.java:623)
	at jadx.core.dex.visitors.CodeShrinker$ArgsInfo.usedArgAssign(CodeShrinker.java:138)
	at jadx.core.dex.visitors.CodeShrinker$ArgsInfo.access$300(CodeShrinker.java:43)
	at jadx.core.dex.visitors.CodeShrinker.canMoveBetweenBlocks(CodeShrinker.java:282)
	at jadx.core.dex.visitors.CodeShrinker.shrinkBlock(CodeShrinker.java:230)
	at jadx.core.dex.visitors.CodeShrinker.shrinkMethod(CodeShrinker.java:38)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.checkArrayForEach(LoopRegionVisitor.java:196)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.checkForIndexedLoop(LoopRegionVisitor.java:119)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.processLoopRegion(LoopRegionVisitor.java:65)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.enterRegion(LoopRegionVisitor.java:52)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:56)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:58)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
	at jadx.core.dex.visitors.regions.LoopRegionVisitor.visit(LoopRegionVisitor.java:46)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
*/
        /*
        r7 = this;
        r2 = r7.pos;
        r1 = r7.length;
        r3 = r7.input;
    L_0x0006:
        r4 = r7.pos;
        if (r4 >= r1) goto L_0x0016;
    L_0x000a:
        r5 = r8.length;
        r4 = 0;
    L_0x000c:
        if (r4 >= r5) goto L_0x0025;
    L_0x000e:
        r0 = r8[r4];
        r6 = r7.pos;
        r6 = r3[r6];
        if (r6 != r0) goto L_0x0022;
    L_0x0016:
        r4 = r7.pos;
        if (r4 <= r2) goto L_0x002c;
    L_0x001a:
        r4 = r7.pos;
        r4 = r4 - r2;
        r4 = r7.cacheString(r2, r4);
    L_0x0021:
        return r4;
    L_0x0022:
        r4 = r4 + 1;
        goto L_0x000c;
    L_0x0025:
        r4 = r7.pos;
        r4 = r4 + 1;
        r7.pos = r4;
        goto L_0x0006;
    L_0x002c:
        r4 = "";
        goto L_0x0021;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jsoup.parser.CharacterReader.consumeToAny(char[]):java.lang.String");
    }

    String consumeToAnySorted(char... chars) {
        int start = this.pos;
        int remaining = this.length;
        char[] val = this.input;
        while (this.pos < remaining && Arrays.binarySearch(chars, val[this.pos]) < 0) {
            this.pos++;
        }
        return this.pos > start ? cacheString(start, this.pos - start) : "";
    }

    String consumeData() {
        int start = this.pos;
        int remaining = this.length;
        char[] val = this.input;
        while (this.pos < remaining) {
            char c = val[this.pos];
            if (c == '&' || c == '<' || c == '\u0000') {
                break;
            }
            this.pos++;
        }
        return this.pos > start ? cacheString(start, this.pos - start) : "";
    }

    String consumeTagName() {
        int start = this.pos;
        int remaining = this.length;
        char[] val = this.input;
        while (this.pos < remaining) {
            char c = val[this.pos];
            if (c == '\t' || c == '\n' || c == '\r' || c == '\f' || c == ' ' || c == '/' || c == '>' || c == '\u0000') {
                break;
            }
            this.pos++;
        }
        return this.pos > start ? cacheString(start, this.pos - start) : "";
    }

    String consumeToEnd() {
        String data = cacheString(this.pos, this.length - this.pos);
        this.pos = this.length;
        return data;
    }

    String consumeLetterSequence() {
        int start = this.pos;
        while (this.pos < this.length) {
            char c = this.input[this.pos];
            if ((c < 'A' || c > 'Z') && ((c < 'a' || c > 'z') && !Character.isLetter(c))) {
                break;
            }
            this.pos++;
        }
        return cacheString(start, this.pos - start);
    }

    String consumeLetterThenDigitSequence() {
        int start = this.pos;
        while (this.pos < this.length) {
            char c = this.input[this.pos];
            if ((c < 'A' || c > 'Z') && ((c < 'a' || c > 'z') && !Character.isLetter(c))) {
                break;
            }
            this.pos++;
        }
        while (!isEmpty()) {
            c = this.input[this.pos];
            if (c < '0' || c > '9') {
                break;
            }
            this.pos++;
        }
        return cacheString(start, this.pos - start);
    }

    String consumeHexSequence() {
        int start = this.pos;
        while (this.pos < this.length) {
            char c = this.input[this.pos];
            if ((c < '0' || c > '9') && ((c < 'A' || c > 'F') && (c < 'a' || c > 'f'))) {
                break;
            }
            this.pos++;
        }
        return cacheString(start, this.pos - start);
    }

    String consumeDigitSequence() {
        int start = this.pos;
        while (this.pos < this.length) {
            char c = this.input[this.pos];
            if (c < '0' || c > '9') {
                break;
            }
            this.pos++;
        }
        return cacheString(start, this.pos - start);
    }

    boolean matches(char c) {
        return !isEmpty() && this.input[this.pos] == c;
    }

    boolean matches(String seq) {
        int scanLength = seq.length();
        if (scanLength > this.length - this.pos) {
            return false;
        }
        for (int offset = 0; offset < scanLength; offset++) {
            if (seq.charAt(offset) != this.input[this.pos + offset]) {
                return false;
            }
        }
        return true;
    }

    boolean matchesIgnoreCase(String seq) {
        int scanLength = seq.length();
        if (scanLength > this.length - this.pos) {
            return false;
        }
        for (int offset = 0; offset < scanLength; offset++) {
            if (Character.toUpperCase(seq.charAt(offset)) != Character.toUpperCase(this.input[this.pos + offset])) {
                return false;
            }
        }
        return true;
    }

    boolean matchesAny(char... seq) {
        if (isEmpty()) {
            return false;
        }
        char c = this.input[this.pos];
        for (char seek : seq) {
            if (seek == c) {
                return true;
            }
        }
        return false;
    }

    boolean matchesAnySorted(char[] seq) {
        return !isEmpty() && Arrays.binarySearch(seq, this.input[this.pos]) >= 0;
    }

    boolean matchesLetter() {
        if (isEmpty()) {
            return false;
        }
        char c = this.input[this.pos];
        if ((c < 'A' || c > 'Z') && ((c < 'a' || c > 'z') && !Character.isLetter(c))) {
            return false;
        }
        return true;
    }

    boolean matchesDigit() {
        if (isEmpty()) {
            return false;
        }
        char c = this.input[this.pos];
        if (c < '0' || c > '9') {
            return false;
        }
        return true;
    }

    boolean matchConsume(String seq) {
        if (!matches(seq)) {
            return false;
        }
        this.pos += seq.length();
        return true;
    }

    boolean matchConsumeIgnoreCase(String seq) {
        if (!matchesIgnoreCase(seq)) {
            return false;
        }
        this.pos += seq.length();
        return true;
    }

    boolean containsIgnoreCase(String seq) {
        return nextIndexOf(seq.toLowerCase(Locale.ENGLISH)) > -1 || nextIndexOf(seq.toUpperCase(Locale.ENGLISH)) > -1;
    }

    public String toString() {
        return new String(this.input, this.pos, this.length - this.pos);
    }

    private String cacheString(int start, int count) {
        char[] val = this.input;
        String[] cache = this.stringCache;
        if (count > 12) {
            return new String(val, start, count);
        }
        int hash = 0;
        int i = 0;
        int offset = start;
        while (i < count) {
            hash = (hash * 31) + val[offset];
            i++;
            offset++;
        }
        int index = hash & (cache.length - 1);
        String cached = cache[index];
        if (cached == null) {
            cached = new String(val, start, count);
            cache[index] = cached;
            return cached;
        } else if (rangeEquals(start, count, cached)) {
            return cached;
        } else {
            cached = new String(val, start, count);
            cache[index] = cached;
            return cached;
        }
    }

    boolean rangeEquals(int start, int count, String cached) {
        if (count != cached.length()) {
            return false;
        }
        char[] one = this.input;
        int j = 0;
        int i = start;
        int count2 = count;
        while (true) {
            count = count2 - 1;
            if (count2 == 0) {
                return true;
            }
            int i2 = i + 1;
            int j2 = j + 1;
            if (one[i] != cached.charAt(j)) {
                return false;
            }
            j = j2;
            i = i2;
            count2 = count;
        }
    }
}
