package cn.bmob.v3.a.a;

/* compiled from: Hex */
public final class This {
    private static final char[] Code = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] V = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private final String I;

    private static char[] V(byte[] bArr) {
        int i = 0;
        char[] cArr = Code;
        int length = bArr.length;
        char[] cArr2 = new char[(length << 1)];
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i + 1;
            cArr2[i] = cArr[(bArr[i2] & 240) >>> 4];
            i = i3 + 1;
            cArr2[i3] = cArr[bArr[i2] & 15];
        }
        return cArr2;
    }

    public static String Code(byte[] bArr) {
        return new String(V(bArr));
    }

    public final String toString() {
        return super.toString() + "[charsetName=" + this.I + "]";
    }
}
