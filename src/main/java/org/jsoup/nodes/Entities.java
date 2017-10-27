package org.jsoup.nodes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.HashMap;
import org.jsoup.SerializationException;
import org.jsoup.helper.DataUtil;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.parser.CharacterReader;
import org.jsoup.parser.Parser;

public class Entities {
    private static final char[] codeDelims = new char[]{',', ';'};
    static final int codepointRadix = 36;
    private static final int empty = -1;
    private static final String emptyName = "";
    private static final HashMap<String, String> multipoints = new HashMap();

    private enum CoreCharset {
        ascii,
        utf,
        fallback;

        private static CoreCharset byName(String name) {
            if (name.equals("US-ASCII")) {
                return ascii;
            }
            if (name.startsWith("UTF-")) {
                return utf;
            }
            return fallback;
        }
    }

    public enum EscapeMode {
        xhtml("entities-xhtml.properties", 4),
        base("entities-base.properties", 106),
        extended("entities-full.properties", 2125);
        
        private int[] codeKeys;
        private int[] codeVals;
        private String[] nameKeys;
        private String[] nameVals;

        private EscapeMode(String file, int size) {
            Entities.load(this, file, size);
        }

        int codepointForName(String name) {
            int index = Arrays.binarySearch(this.nameKeys, name);
            return index >= 0 ? this.codeVals[index] : -1;
        }

        String nameForCodepoint(int codepoint) {
            int index = Arrays.binarySearch(this.codeKeys, codepoint);
            if (index >= 0) {
                return (index >= this.nameVals.length + -1 || this.codeKeys[index + 1] != codepoint) ? this.nameVals[index] : this.nameVals[index + 1];
            } else {
                return "";
            }
        }

        private int size() {
            return this.nameKeys.length;
        }
    }

    private Entities() {
    }

    public static boolean isNamedEntity(String name) {
        return EscapeMode.extended.codepointForName(name) != -1;
    }

    public static boolean isBaseNamedEntity(String name) {
        return EscapeMode.base.codepointForName(name) != -1;
    }

    public static Character getCharacterByName(String name) {
        return Character.valueOf((char) EscapeMode.extended.codepointForName(name));
    }

    public static String getByName(String name) {
        String val = (String) multipoints.get(name);
        if (val != null) {
            return val;
        }
        if (EscapeMode.extended.codepointForName(name) == -1) {
            return "";
        }
        return new String(new int[]{EscapeMode.extended.codepointForName(name)}, 0, 1);
    }

    public static int codepointsForName(String name, int[] codepoints) {
        String val = (String) multipoints.get(name);
        if (val != null) {
            codepoints[0] = val.codePointAt(0);
            codepoints[1] = val.codePointAt(1);
            return 2;
        }
        int codepoint = EscapeMode.extended.codepointForName(name);
        if (codepoint == -1) {
            return 0;
        }
        codepoints[0] = codepoint;
        return 1;
    }

    static String escape(String string, OutputSettings out) {
        StringBuilder accum = new StringBuilder(string.length() * 2);
        try {
            escape(accum, string, out, false, false, false);
            return accum.toString();
        } catch (Throwable e) {
            throw new SerializationException(e);
        }
    }

    static void escape(Appendable accum, String string, OutputSettings out, boolean inAttribute, boolean normaliseWhite, boolean stripLeadingWhite) throws IOException {
        boolean lastWasWhite = false;
        boolean reachedNonWhite = false;
        EscapeMode escapeMode = out.escapeMode();
        CharsetEncoder encoder = out.encoder();
        CoreCharset coreCharset = CoreCharset.byName(encoder.charset().name());
        int length = string.length();
        int offset = 0;
        while (offset < length) {
            int codePoint = string.codePointAt(offset);
            if (normaliseWhite) {
                if (StringUtil.isWhitespace(codePoint)) {
                    if ((!stripLeadingWhite || reachedNonWhite) && !lastWasWhite) {
                        accum.append(' ');
                        lastWasWhite = true;
                    }
                    offset += Character.charCount(codePoint);
                } else {
                    lastWasWhite = false;
                    reachedNonWhite = true;
                }
            }
            if (codePoint < 65536) {
                char c = (char) codePoint;
                switch (c) {
                    case '\"':
                        if (!inAttribute) {
                            accum.append(c);
                            break;
                        } else {
                            accum.append("&quot;");
                            break;
                        }
                    case '&':
                        accum.append("&amp;");
                        break;
                    case '<':
                        if (inAttribute && escapeMode != EscapeMode.xhtml) {
                            accum.append(c);
                            break;
                        } else {
                            accum.append("&lt;");
                            break;
                        }
                        break;
                    case '>':
                        if (!inAttribute) {
                            accum.append("&gt;");
                            break;
                        } else {
                            accum.append(c);
                            break;
                        }
                    case ' ':
                        if (escapeMode == EscapeMode.xhtml) {
                            accum.append("&#xa0;");
                            break;
                        } else {
                            accum.append("&nbsp;");
                            break;
                        }
                    default:
                        if (!canEncode(coreCharset, c, encoder)) {
                            appendEncoded(accum, escapeMode, codePoint);
                            break;
                        } else {
                            accum.append(c);
                            break;
                        }
                }
            }
            String c2 = new String(Character.toChars(codePoint));
            if (encoder.canEncode(c2)) {
                accum.append(c2);
            } else {
                appendEncoded(accum, escapeMode, codePoint);
            }
            offset += Character.charCount(codePoint);
        }
    }

    private static void appendEncoded(Appendable accum, EscapeMode escapeMode, int codePoint) throws IOException {
        String name = escapeMode.nameForCodepoint(codePoint);
        if (name != "") {
            accum.append('&').append(name).append(';');
        } else {
            accum.append("&#x").append(Integer.toHexString(codePoint)).append(';');
        }
    }

    static String unescape(String string) {
        return unescape(string, false);
    }

    static String unescape(String string, boolean strict) {
        return Parser.unescapeEntities(string, strict);
    }

    private static boolean canEncode(CoreCharset charset, char c, CharsetEncoder fallback) {
        switch (charset) {
            case ascii:
                if (c >= '') {
                    return false;
                }
                return true;
            case utf:
                return true;
            default:
                return fallback.canEncode(c);
        }
    }

    private static void load(EscapeMode e, String file, int size) {
        e.nameKeys = new String[size];
        e.codeVals = new int[size];
        e.codeKeys = new int[size];
        e.nameVals = new String[size];
        InputStream stream = Entities.class.getResourceAsStream(file);
        if (stream == null) {
            throw new IllegalStateException("Could not read resource " + file + ". Make sure you copy resources for " + Entities.class.getCanonicalName());
        }
        int i = 0;
        try {
            CharacterReader reader = new CharacterReader(Charset.forName("ascii").decode(DataUtil.readToByteBuffer(stream, 0)).toString());
            while (!reader.isEmpty()) {
                int cp2;
                String name = reader.consumeTo('=');
                reader.advance();
                int cp1 = Integer.parseInt(reader.consumeToAny(codeDelims), 36);
                char codeDelim = reader.current();
                reader.advance();
                if (codeDelim == ',') {
                    cp2 = Integer.parseInt(reader.consumeTo(';'), 36);
                    reader.advance();
                } else {
                    cp2 = -1;
                }
                String indexS = reader.consumeTo('\n');
                if (indexS.charAt(indexS.length() - 1) == '\r') {
                    indexS = indexS.substring(0, indexS.length() - 1);
                }
                int index = Integer.parseInt(indexS, 36);
                reader.advance();
                e.nameKeys[i] = name;
                e.codeVals[i] = cp1;
                e.codeKeys[index] = cp1;
                e.nameVals[index] = name;
                if (cp2 != -1) {
                    multipoints.put(name, new String(new int[]{cp1, cp2}, 0, 2));
                }
                i++;
            }
        } catch (IOException e2) {
            throw new IllegalStateException("Error reading resource " + file);
        }
    }
}
