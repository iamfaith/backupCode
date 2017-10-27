package cn.bmob.v3.socketio;

import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.TransportMediator;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.apache.http.protocol.HTTP;

/* compiled from: HybiParser */
public final class I {
    private static final List<Integer> d = Arrays.asList(new Integer[]{Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(8), Integer.valueOf(9), Integer.valueOf(10)});
    private static final List<Integer> e = Arrays.asList(new Integer[]{Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2)});
    private boolean B;
    private int C;
    private From Code;
    private int D;
    private int F;
    private int I;
    private byte[] L = new byte[0];
    private int S;
    private boolean V = true;
    private boolean Z;
    private byte[] a = new byte[0];
    private boolean b = false;
    private ByteArrayOutputStream c = new ByteArrayOutputStream();

    /* compiled from: HybiParser */
    public static class This extends DataInputStream {
        public This(InputStream inputStream) {
            super(inputStream);
        }

        public final byte[] Code(int i) throws IOException {
            byte[] bArr = new byte[i];
            int i2 = 0;
            while (i2 < i) {
                int read = read(bArr, i2, i - i2);
                if (read == -1) {
                    break;
                }
                i2 += read;
            }
            if (i2 == i) {
                return bArr;
            }
            throw new IOException(String.format("Read wrong number of bytes. Got: %s, Expected: %s.", new Object[]{Integer.valueOf(i2), Integer.valueOf(i)}));
        }
    }

    /* compiled from: HybiParser */
    public static class thing extends IOException {
        public thing(String str) {
            super(str);
        }
    }

    public I(From from) {
        this.Code = from;
    }

    private static byte[] Code(byte[] bArr, byte[] bArr2, int i) {
        if (bArr2.length != 0) {
            for (int i2 = 0; i2 < bArr.length - i; i2++) {
                bArr[i + i2] = (byte) (bArr[i + i2] ^ bArr2[i2 % 4]);
            }
        }
        return bArr;
    }

    public final void Code(This thisR) throws IOException {
        while (thisR.available() != -1) {
            int i;
            int i2;
            byte[] Code;
            switch (this.I) {
                case 0:
                    byte readByte = thisR.readByte();
                    i = (readByte & 64) == 64 ? 1 : 0;
                    int i3;
                    if ((readByte & 32) == 32) {
                        i3 = 1;
                    } else {
                        i3 = 0;
                    }
                    if ((readByte & 16) == 16) {
                        i2 = 1;
                    } else {
                        i2 = 0;
                    }
                    if (i == 0 && r5 == 0 && r0 == 0) {
                        this.Z = (readByte & 128) == 128;
                        this.C = readByte & 15;
                        this.L = new byte[0];
                        this.a = new byte[0];
                        if (!d.contains(Integer.valueOf(this.C))) {
                            throw new thing("Bad opcode");
                        } else if (e.contains(Integer.valueOf(this.C)) || this.Z) {
                            this.I = 1;
                            break;
                        } else {
                            throw new thing("Expected non-final packet");
                        }
                    }
                    throw new thing("RSV not zero");
                case 1:
                    boolean z;
                    byte readByte2 = thisR.readByte();
                    if ((readByte2 & 128) == 128) {
                        z = true;
                    } else {
                        z = false;
                    }
                    this.B = z;
                    this.F = readByte2 & TransportMediator.KEYCODE_MEDIA_PAUSE;
                    if (this.F >= 0 && this.F <= 125) {
                        if (this.B) {
                            i2 = 3;
                        } else {
                            i2 = 4;
                        }
                        this.I = i2;
                        break;
                    }
                    this.S = this.F == TransportMediator.KEYCODE_MEDIA_PLAY ? 2 : 8;
                    this.I = 2;
                    break;
                case 2:
                    Code = thisR.Code(this.S);
                    int length = Code.length;
                    if (Code.length >= length) {
                        long j = 0;
                        for (i2 = 0; i2 < length; i2++) {
                            j += (long) ((Code[i2] & 255) << (((length - 1) - i2) << 3));
                        }
                        if (j >= 0 && j <= 2147483647L) {
                            this.F = (int) j;
                            this.I = this.B ? 3 : 4;
                            break;
                        }
                        throw new thing("Bad integer: " + j);
                    }
                    throw new IllegalArgumentException("length must be less than or equal to b.length");
                case 3:
                    this.L = thisR.Code(4);
                    this.I = 4;
                    break;
                case 4:
                    this.a = thisR.Code(this.F);
                    Code = Code(this.a, this.L, 0);
                    i2 = this.C;
                    if (i2 == 0) {
                        if (this.D == 0) {
                            throw new thing("Mode was not set.");
                        }
                        this.c.write(Code);
                        if (this.Z) {
                            byte[] toByteArray = this.c.toByteArray();
                            if (this.D == 1) {
                                this.Code.Code().Code(Code(toByteArray));
                            }
                            this.D = 0;
                            this.c.reset();
                        }
                    } else if (i2 == 1) {
                        if (this.Z) {
                            this.Code.Code().Code(Code(Code));
                        } else {
                            this.D = 1;
                            this.c.write(Code);
                        }
                    } else if (i2 == 2) {
                        if (!this.Z) {
                            this.D = 2;
                            this.c.write(Code);
                        }
                    } else if (i2 == 8) {
                        String Code2;
                        i2 = Code.length >= 2 ? (Code[0] * 256) + Code[1] : 0;
                        if (Code.length > 2) {
                            i = Code.length;
                            if (2 > i) {
                                throw new IllegalArgumentException();
                            }
                            int length2 = Code.length;
                            if (2 > length2) {
                                throw new ArrayIndexOutOfBoundsException();
                            }
                            i -= 2;
                            length2 = Math.min(i, length2 - 2);
                            byte[] bArr = new byte[i];
                            System.arraycopy(Code, 2, bArr, 0, length2);
                            Code2 = Code(bArr);
                        } else {
                            Code2 = null;
                        }
                        Log.d("HybiParser", "Got close op! " + i2 + " " + Code2);
                        this.Code.Code().Code(i2, Code2);
                    } else if (i2 == 9) {
                        if (Code.length > 125) {
                            throw new thing("Ping payload too large");
                        }
                        Log.d("HybiParser", "Sending pong!!");
                        this.Code.Code(Code((Object) Code, 10, -1));
                    } else if (i2 == 10) {
                        Log.d("HybiParser", "Got pong! " + Code(Code));
                    }
                    this.I = 0;
                    break;
                default:
                    break;
            }
        }
        this.Code.Code().Code(0, "EOF");
    }

    private byte[] Code(Object obj, int i, int i2) {
        Log.d("HybiParser", "Creating frame for: " + obj + " op: " + i + " err: " + i2);
        if (obj instanceof String) {
            obj = V((String) obj);
        } else {
            byte[] bArr = (byte[]) obj;
        }
        int i3 = i2 > 0 ? 2 : 0;
        int length = obj.length + i3;
        int i4 = length <= 125 ? 2 : length <= SupportMenu.USER_MASK ? 4 : 10;
        int i5 = i4 + (this.V ? 4 : 0);
        int i6 = this.V ? 128 : 0;
        byte[] bArr2 = new byte[(length + i5)];
        bArr2[0] = (byte) (((byte) i) | -128);
        if (length <= 125) {
            bArr2[1] = (byte) (i6 | length);
        } else if (length <= SupportMenu.USER_MASK) {
            bArr2[1] = (byte) (i6 | TransportMediator.KEYCODE_MEDIA_PLAY);
            bArr2[2] = (byte) ((int) Math.floor((double) (length / 256)));
            bArr2[3] = (byte) length;
        } else {
            bArr2[1] = (byte) (i6 | TransportMediator.KEYCODE_MEDIA_PAUSE);
            bArr2[2] = (byte) ((int) Math.floor(((double) length) / Math.pow(2.0d, 56.0d)));
            bArr2[3] = (byte) ((int) Math.floor(((double) length) / Math.pow(2.0d, 48.0d)));
            bArr2[4] = (byte) ((int) Math.floor(((double) length) / Math.pow(2.0d, 40.0d)));
            bArr2[5] = (byte) ((int) Math.floor(((double) length) / Math.pow(2.0d, 32.0d)));
            bArr2[6] = (byte) ((int) Math.floor(((double) length) / Math.pow(2.0d, 24.0d)));
            bArr2[7] = (byte) ((int) Math.floor(((double) length) / Math.pow(2.0d, 16.0d)));
            bArr2[8] = (byte) ((int) Math.floor(((double) length) / Math.pow(2.0d, 8.0d)));
            bArr2[9] = (byte) length;
        }
        if (i2 > 0) {
            bArr2[i5] = (byte) ((int) Math.floor((double) (i2 / 256)));
            bArr2[i5 + 1] = (byte) i2;
        }
        System.arraycopy(obj, 0, bArr2, i3 + i5, obj.length);
        if (this.V) {
            byte[] bArr3 = new byte[]{(byte) ((int) Math.floor(Math.random() * 256.0d)), (byte) ((int) Math.floor(Math.random() * 256.0d)), (byte) ((int) Math.floor(Math.random() * 256.0d)), (byte) ((int) Math.floor(Math.random() * 256.0d))};
            System.arraycopy(bArr3, 0, bArr2, i4, 4);
            Code(bArr2, bArr3, i5);
        }
        return bArr2;
    }

    private static String Code(byte[] bArr) {
        try {
            return new String(bArr, HTTP.UTF_8);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] V(String str) {
        try {
            return str.getBytes(HTTP.UTF_8);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public final byte[] Code(String str) {
        return Code((Object) str, 1, -1);
    }
}
