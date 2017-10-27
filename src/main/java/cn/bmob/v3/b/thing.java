package cn.bmob.v3.b;

/* compiled from: Base64Coder */
public final class thing {
    private static final char[] Code = new char[64];
    private static final byte[] V = new byte[128];

    static {
        int i;
        int i2 = 0;
        System.getProperty("line.separator");
        char c = 'A';
        int i3 = 0;
        while (c <= 'Z') {
            i = i3 + 1;
            Code[i3] = c;
            c = (char) (c + 1);
            i3 = i;
        }
        c = 'a';
        while (c <= 'z') {
            i = i3 + 1;
            Code[i3] = c;
            c = (char) (c + 1);
            i3 = i;
        }
        c = '0';
        while (c <= '9') {
            i = i3 + 1;
            Code[i3] = c;
            c = (char) (c + 1);
            i3 = i;
        }
        i = i3 + 1;
        Code[i3] = '+';
        Code[i] = '/';
        for (int i4 = 0; i4 < 128; i4++) {
            V[i4] = (byte) -1;
        }
        while (i2 < 64) {
            V[Code[i2]] = (byte) i2;
            i2++;
        }
    }

    public static String Code(String str) {
        byte[] bytes = str.getBytes();
        return new String(Code(bytes, 0, bytes.length));
    }

    private static char[] Code(byte[] bArr, int i, int i2) {
        int i3 = ((i2 << 2) + 2) / 3;
        char[] cArr = new char[(((i2 + 2) / 3) << 2)];
        int i4 = i2 + 0;
        int i5 = 0;
        int i6 = 0;
        while (i6 < i4) {
            int i7;
            char c;
            int i8 = i6 + 1;
            int i9 = bArr[i6] & 255;
            if (i8 < i4) {
                i7 = bArr[i8] & 255;
                i8++;
            } else {
                i7 = 0;
            }
            if (i8 < i4) {
                i6 = i8 + 1;
                i8 = bArr[i8] & 255;
            } else {
                i6 = i8;
                i8 = 0;
            }
            int i10 = i9 >>> 2;
            i9 = ((i9 & 3) << 4) | (i7 >>> 4);
            i7 = ((i7 & 15) << 2) | (i8 >>> 6);
            int i11 = i8 & 63;
            i8 = i5 + 1;
            cArr[i5] = Code[i10];
            i5 = i8 + 1;
            cArr[i8] = Code[i9];
            if (i5 < i3) {
                c = Code[i7];
            } else {
                c = '=';
            }
            cArr[i5] = c;
            i7 = i5 + 1;
            if (i7 < i3) {
                c = Code[i11];
            } else {
                c = '=';
            }
            cArr[i7] = c;
            i5 = i7 + 1;
        }
        return cArr;
    }
}
