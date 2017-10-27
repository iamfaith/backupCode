package org.jsoup.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.protocol.HTTP;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.parser.Parser;

public final class DataUtil {
    static final int boundaryLength = 32;
    private static final int bufferSize = 60000;
    private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:\"|')?([^\\s,;\"']*)");
    static final String defaultCharset = "UTF-8";
    private static final char[] mimeBoundaryChars = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private DataUtil() {
    }

    public static Document load(File in, String charsetName, String baseUri) throws IOException {
        return parseByteData(readFileToByteBuffer(in), charsetName, baseUri, Parser.htmlParser());
    }

    public static Document load(InputStream in, String charsetName, String baseUri) throws IOException {
        return parseByteData(readToByteBuffer(in), charsetName, baseUri, Parser.htmlParser());
    }

    public static Document load(InputStream in, String charsetName, String baseUri, Parser parser) throws IOException {
        return parseByteData(readToByteBuffer(in), charsetName, baseUri, parser);
    }

    static void crossStreams(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[bufferSize];
        while (true) {
            int len = in.read(buffer);
            if (len != -1) {
                out.write(buffer, 0, len);
            } else {
                return;
            }
        }
    }

    static Document parseByteData(ByteBuffer byteData, String charsetName, String baseUri, Parser parser) {
        String docData;
        Document doc = null;
        charsetName = detectCharsetFromBom(byteData, charsetName);
        if (charsetName == null) {
            docData = Charset.forName("UTF-8").decode(byteData).toString();
            doc = parser.parseInput(docData, baseUri);
            Element meta = doc.select("meta[http-equiv=content-type], meta[charset]").first();
            String foundCharset = null;
            if (meta != null) {
                if (meta.hasAttr("http-equiv")) {
                    foundCharset = getCharsetFromContentType(meta.attr("content"));
                }
                if (foundCharset == null && meta.hasAttr("charset")) {
                    foundCharset = meta.attr("charset");
                }
            }
            if (foundCharset == null && doc.childNodeSize() > 0 && (doc.childNode(0) instanceof XmlDeclaration)) {
                XmlDeclaration prolog = (XmlDeclaration) doc.childNode(0);
                if (prolog.name().equals("xml")) {
                    foundCharset = prolog.attr("encoding");
                }
            }
            foundCharset = validateCharset(foundCharset);
            if (!(foundCharset == null || foundCharset.equals("UTF-8"))) {
                foundCharset = foundCharset.trim().replaceAll("[\"']", "");
                charsetName = foundCharset;
                byteData.rewind();
                docData = Charset.forName(foundCharset).decode(byteData).toString();
                doc = null;
            }
        } else {
            Validate.notEmpty(charsetName, "Must set charset arg to character set of file to parse. Set to null to attempt to detect from HTML");
            docData = Charset.forName(charsetName).decode(byteData).toString();
        }
        if (doc != null) {
            return doc;
        }
        doc = parser.parseInput(docData, baseUri);
        doc.outputSettings().charset(charsetName);
        return doc;
    }

    public static ByteBuffer readToByteBuffer(InputStream inStream, int maxSize) throws IOException {
        boolean z;
        boolean capped;
        int i = bufferSize;
        if (maxSize >= 0) {
            z = true;
        } else {
            z = false;
        }
        Validate.isTrue(z, "maxSize must be 0 (unlimited) or larger");
        if (maxSize > 0) {
            capped = true;
        } else {
            capped = false;
        }
        int i2 = (!capped || maxSize >= bufferSize) ? bufferSize : maxSize;
        byte[] buffer = new byte[i2];
        if (capped) {
            i = maxSize;
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(i);
        int remaining = maxSize;
        while (!Thread.interrupted()) {
            int read = inStream.read(buffer);
            if (read == -1) {
                break;
            }
            if (capped) {
                if (read > remaining) {
                    outStream.write(buffer, 0, remaining);
                    break;
                }
                remaining -= read;
            }
            outStream.write(buffer, 0, read);
        }
        return ByteBuffer.wrap(outStream.toByteArray());
    }

    static ByteBuffer readToByteBuffer(InputStream inStream) throws IOException {
        return readToByteBuffer(inStream, 0);
    }

    static ByteBuffer readFileToByteBuffer(File file) throws IOException {
        Throwable th;
        RandomAccessFile randomAccessFile = null;
        try {
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "r");
            try {
                byte[] bytes = new byte[((int) randomAccessFile2.length())];
                randomAccessFile2.readFully(bytes);
                ByteBuffer wrap = ByteBuffer.wrap(bytes);
                if (randomAccessFile2 != null) {
                    randomAccessFile2.close();
                }
                return wrap;
            } catch (Throwable th2) {
                th = th2;
                randomAccessFile = randomAccessFile2;
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            throw th;
        }
    }

    static ByteBuffer emptyByteBuffer() {
        return ByteBuffer.allocate(0);
    }

    static String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        Matcher m = charsetPattern.matcher(contentType);
        if (m.find()) {
            return validateCharset(m.group(1).trim().replace("charset=", ""));
        }
        return null;
    }

    private static String validateCharset(String cs) {
        if (cs == null || cs.length() == 0) {
            return null;
        }
        cs = cs.trim().replaceAll("[\"']", "");
        try {
            if (Charset.isSupported(cs)) {
                return cs;
            }
            cs = cs.toUpperCase(Locale.ENGLISH);
            if (Charset.isSupported(cs)) {
                return cs;
            }
            return null;
        } catch (IllegalCharsetNameException e) {
        }
    }

    static String mimeBoundary() {
        StringBuilder mime = new StringBuilder(32);
        Random rand = new Random();
        for (int i = 0; i < 32; i++) {
            mime.append(mimeBoundaryChars[rand.nextInt(mimeBoundaryChars.length)]);
        }
        return mime.toString();
    }

    private static String detectCharsetFromBom(ByteBuffer byteData, String charsetName) {
        byteData.mark();
        byte[] bom = new byte[4];
        if (byteData.remaining() >= bom.length) {
            byteData.get(bom);
            byteData.rewind();
        }
        if ((bom[0] == (byte) 0 && bom[1] == (byte) 0 && bom[2] == (byte) -2 && bom[3] == (byte) -1) || (bom[0] == (byte) -1 && bom[1] == (byte) -2 && bom[2] == (byte) 0 && bom[3] == (byte) 0)) {
            return "UTF-32";
        }
        if ((bom[0] == (byte) -2 && bom[1] == (byte) -1) || (bom[0] == (byte) -1 && bom[1] == (byte) -2)) {
            return HTTP.UTF_16;
        }
        if (bom[0] != (byte) -17 || bom[1] != (byte) -69 || bom[2] != (byte) -65) {
            return charsetName;
        }
        charsetName = "UTF-8";
        byteData.position(3);
        return charsetName;
    }
}
