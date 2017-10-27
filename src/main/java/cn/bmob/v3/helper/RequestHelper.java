package cn.bmob.v3.helper;

import android.content.Context;
import cn.bmob.v3.BmobConstants;
import cn.bmob.v3.datatype.a.This;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

public class RequestHelper {
    private static char[] Code = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    private static byte[] V = new byte[]{(byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 62, (byte) -1, (byte) -1, (byte) -1, (byte) 63, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 58, (byte) 59, (byte) 60, (byte) 61, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 10, (byte) 11, (byte) 12, (byte) 13, (byte) 14, (byte) 15, (byte) 16, (byte) 17, (byte) 18, (byte) 19, (byte) 20, (byte) 21, (byte) 22, (byte) 23, (byte) 24, (byte) 25, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) 26, (byte) 27, (byte) 28, (byte) 29, (byte) 30, (byte) 31, (byte) 32, (byte) 33, (byte) 34, (byte) 35, (byte) 36, (byte) 37, (byte) 38, (byte) 39, (byte) 40, (byte) 41, (byte) 42, (byte) 43, (byte) 44, (byte) 45, (byte) 46, (byte) 47, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -1};

    public static String encode(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = data.length;
        int i = 0;
        while (i < length) {
            int i2 = i + 1;
            int i3 = data[i] & 255;
            if (i2 == length) {
                stringBuilder.append(Code[i3 >>> 2]);
                stringBuilder.append(Code[(i3 & 3) << 4]);
                stringBuilder.append("==");
                break;
            }
            int i4 = i2 + 1;
            i2 = data[i2] & 255;
            if (i4 == length) {
                stringBuilder.append(Code[i3 >>> 2]);
                stringBuilder.append(Code[((i3 & 3) << 4) | ((i2 & 240) >>> 4)]);
                stringBuilder.append(Code[(i2 & 15) << 2]);
                stringBuilder.append("=");
                break;
            }
            i = i4 + 1;
            i4 = data[i4] & 255;
            stringBuilder.append(Code[i3 >>> 2]);
            stringBuilder.append(Code[((i3 & 3) << 4) | ((i2 & 240) >>> 4)]);
            stringBuilder.append(Code[((i2 & 15) << 2) | ((i4 & 192) >>> 6)]);
            stringBuilder.append(Code[i4 & 63]);
        }
        return stringBuilder.toString();
    }

    public static byte[] decode(String str) {
        byte[] bytes = str.getBytes();
        int length = bytes.length;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(length);
        int i = 0;
        while (i < length) {
            byte b;
            byte b2;
            while (true) {
                int i2 = i + 1;
                byte b3 = V[bytes[i]];
                byte b4;
                if (i2 >= length || b3 != (byte) -1) {
                    if (b3 == (byte) -1) {
                        break;
                    }
                    while (true) {
                        i = i2 + 1;
                        b = V[bytes[i2]];
                        if (i >= length || b != (byte) -1) {
                            if (b == (byte) -1) {
                                break;
                            }
                            byteArrayOutputStream.write((b3 << 2) | ((b & 48) >>> 4));
                            while (true) {
                                i2 = i + 1;
                                b2 = bytes[i];
                                if (b2 != (byte) 61) {
                                    return byteArrayOutputStream.toByteArray();
                                }
                                b3 = V[b2];
                                if (i2 >= length || b3 != (byte) -1) {
                                    if (b3 == (byte) -1) {
                                        break;
                                    }
                                    byteArrayOutputStream.write(((b & 15) << 4) | ((b3 & 60) >>> 2));
                                    while (true) {
                                        i = i2 + 1;
                                        b4 = bytes[i2];
                                        if (b4 == (byte) 61) {
                                            return byteArrayOutputStream.toByteArray();
                                        }
                                        b4 = V[b4];
                                        if (i >= length || b4 != (byte) -1) {
                                            if (b4 == (byte) -1) {
                                                break;
                                            }
                                            byteArrayOutputStream.write(b4 | ((b3 & 3) << 6));
                                        } else {
                                            i2 = i;
                                        }
                                    }
                                    if (b4 == (byte) -1) {
                                        break;
                                    }
                                    byteArrayOutputStream.write(b4 | ((b3 & 3) << 6));
                                } else {
                                    i = i2;
                                }
                            }
                            if (b3 == (byte) -1) {
                                byteArrayOutputStream.write(((b & 15) << 4) | ((b3 & 60) >>> 2));
                                while (true) {
                                    i = i2 + 1;
                                    b4 = bytes[i2];
                                    if (b4 == (byte) 61) {
                                        b4 = V[b4];
                                        if (i >= length) {
                                            break;
                                        }
                                        break;
                                    }
                                    return byteArrayOutputStream.toByteArray();
                                    i2 = i;
                                }
                                if (b4 == (byte) -1) {
                                    break;
                                }
                                byteArrayOutputStream.write(b4 | ((b3 & 3) << 6));
                            } else {
                                break;
                            }
                        }
                        i2 = i;
                    }
                    if (b == (byte) -1) {
                        byteArrayOutputStream.write((b3 << 2) | ((b & 48) >>> 4));
                        while (true) {
                            i2 = i + 1;
                            b2 = bytes[i];
                            if (b2 != (byte) 61) {
                                b3 = V[b2];
                                if (i2 >= length) {
                                    break;
                                }
                                break;
                            }
                            return byteArrayOutputStream.toByteArray();
                            i = i2;
                        }
                        if (b3 == (byte) -1) {
                            break;
                        }
                        byteArrayOutputStream.write(((b & 15) << 4) | ((b3 & 60) >>> 2));
                        while (true) {
                            i = i2 + 1;
                            b4 = bytes[i2];
                            if (b4 == (byte) 61) {
                                b4 = V[b4];
                                if (i >= length) {
                                    break;
                                }
                                break;
                            }
                            return byteArrayOutputStream.toByteArray();
                            i2 = i;
                        }
                        if (b4 == (byte) -1) {
                            break;
                        }
                        byteArrayOutputStream.write(b4 | ((b3 & 3) << 6));
                    } else {
                        break;
                    }
                }
                i = i2;
            }
            if (b3 == (byte) -1) {
                while (true) {
                    i = i2 + 1;
                    b = V[bytes[i2]];
                    if (i >= length) {
                        break;
                    }
                    break;
                    i2 = i;
                }
                if (b == (byte) -1) {
                    break;
                }
                byteArrayOutputStream.write((b3 << 2) | ((b & 48) >>> 4));
                while (true) {
                    i2 = i + 1;
                    b2 = bytes[i];
                    if (b2 != (byte) 61) {
                        b3 = V[b2];
                        if (i2 >= length) {
                            break;
                        }
                        break;
                    }
                    return byteArrayOutputStream.toByteArray();
                    i = i2;
                }
                if (b3 == (byte) -1) {
                    byteArrayOutputStream.write(((b & 15) << 4) | ((b3 & 60) >>> 2));
                    while (true) {
                        i = i2 + 1;
                        b4 = bytes[i2];
                        if (b4 == (byte) 61) {
                            b4 = V[b4];
                            if (i >= length) {
                                break;
                            }
                            break;
                        }
                        return byteArrayOutputStream.toByteArray();
                        i2 = i;
                    }
                    if (b4 == (byte) -1) {
                        break;
                    }
                    byteArrayOutputStream.write(b4 | ((b3 & 3) << 6));
                } else {
                    break;
                }
            }
            break;
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static final String Code(Context context) {
        String str = "volley/0";
        try {
            String packageName = context.getPackageName();
            str = packageName + "/" + context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static final String getUserAgent(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Code(context));
        stringBuilder.append(String.valueOf(System.currentTimeMillis()));
        stringBuilder.append("Android");
        stringBuilder.append(String.valueOf(BmobConstants.VERSION_NAME));
        return stringBuilder.toString();
    }

    public static final String getKey1(String agent) {
        byte[] bytes;
        if (agent == null || agent.length() <= 0) {
            bytes = "".getBytes(Charset.forName("utf-8"));
        } else {
            bytes = agent.getBytes(Charset.forName("utf-8"));
        }
        byte[] bArr = new byte[16];
        for (int i = 0; i < 16; i++) {
            bArr[i] = bytes[(bytes.length - 16) + i];
        }
        return This.V(bArr);
    }

    public static final String getKey2(String responseId) {
        byte[] bytes = responseId.getBytes();
        byte[] bArr = new byte[16];
        for (int i = 0; i < 16; i++) {
            bArr[i] = bytes[(bytes.length - 16) + i];
        }
        return This.V(bArr);
    }

    public static final String getKey3(String agent) {
        byte[] bytes = agent.getBytes();
        byte[] bArr = new byte[16];
        for (int i = 0; i < 16; i++) {
            bArr[i] = bytes[i + 1];
        }
        return This.V(bArr);
    }
}
