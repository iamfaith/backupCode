package cn.bmob.v3.b;

import android.text.TextUtils;
import android.util.Log;
import cn.bmob.v3.BmobConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: BLog */
public final class This {
    private static final thing Code = new thing();
    private static String V = BmobConstants.TAG;

    /* compiled from: BLog */
    public enum This {
        ;

        static {
            V = 1;
            Code = 2;
            I = new int[]{1, 2};
        }
    }

    /* compiled from: BLog */
    public static class thing {
        int Code = 5;
        int I = This.Code;
        boolean V = true;
    }

    private This() {
    }

    private static void Code(String str, String str2, int i) {
        Code(i);
        Code(3, str, str2, i);
    }

    public static void Code(String str) {
        String str2 = V;
        int i = Code.Code;
        Code(i);
        if (str == null) {
            str = "No message/exception is set";
        }
        Code(6, str2, str, i);
    }

    public static void V(String str) {
        String str2 = V;
        int i = Code.Code;
        Code(i);
        Code(4, str2, str, i);
    }

    public static void I(String str) {
        V(V, str, Code.Code);
    }

    private static void V(String str, String str2, int i) {
        Code(i);
        if (TextUtils.isEmpty(str2)) {
            Code(str, "Empty/Null json content", i);
            return;
        }
        try {
            if (str2.startsWith("{")) {
                Code(str, new JSONObject(str2).toString(4), i);
            } else if (str2.startsWith("[")) {
                Code(str, new JSONArray(str2).toString(4), i);
            }
        } catch (JSONException e) {
            Code(str, e.getCause().getMessage() + "\n" + str2, i);
        }
    }

    private static synchronized void Code(int i, String str, String str2, int i2) {
        synchronized (This.class) {
            if (Code.I != This.Code) {
                V(i, str, "╔════════════════════════════════════════════════════════════════════════════════════════");
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (Code.V) {
                    V(i, str, "║ Thread: " + Thread.currentThread().getName());
                    V(i, str, "╟────────────────────────────────────────────────────────────────────────────────────────");
                }
                String str3 = "";
                int Code = Code(stackTrace);
                for (int i3 = i2; i3 > 0; i3--) {
                    int i4 = i3 + Code;
                    StringBuilder stringBuilder = new StringBuilder();
                    StringBuilder append = stringBuilder.append("║ ").append(str3);
                    String className = stackTrace[i4].getClassName();
                    append.append(className.substring(className.lastIndexOf(".") + 1)).append(".").append(stackTrace[i4].getMethodName()).append("  (").append(stackTrace[i4].getFileName()).append(":").append(stackTrace[i4].getLineNumber()).append(")");
                    str3 = str3 + "   ";
                    V(i, str, stringBuilder.toString());
                }
                byte[] bytes = str2.getBytes();
                int length = bytes.length;
                if (length <= 4000) {
                    if (i2 > 0) {
                        V(i, str, "╟────────────────────────────────────────────────────────────────────────────────────────");
                    }
                    Code(i, str, str2);
                    V(i, str, "╚════════════════════════════════════════════════════════════════════════════════════════");
                } else {
                    if (i2 > 0) {
                        V(i, str, "╟────────────────────────────────────────────────────────────────────────────────────────");
                    }
                    for (int i5 = 0; i5 < length; i5 += 4000) {
                        Code(i, str, new String(bytes, i5, Math.min(length - i5, 4000)));
                    }
                    V(i, str, "╚════════════════════════════════════════════════════════════════════════════════════════");
                }
            }
        }
    }

    private static void Code(int i, String str, String str2) {
        for (String str3 : str2.split(System.getProperty("line.separator"))) {
            V(i, str, "║ " + str3);
        }
    }

    private static void Code(int i) {
        if (i < 0 || i > 5) {
            throw new IllegalStateException("methodCount must be > 0 and < 5");
        }
    }

    private static int Code(StackTraceElement[] stackTraceElementArr) {
        for (int i = 3; i < stackTraceElementArr.length; i++) {
            if (!stackTraceElementArr[i].getClassName().equals(This.class.getName())) {
                return i - 1;
            }
        }
        return -1;
    }

    private static void V(int i, String str, String str2) {
        String str3;
        if (TextUtils.isEmpty(str) || TextUtils.equals(V, str)) {
            str3 = V;
        } else {
            str3 = V + "-" + str;
        }
        switch (i) {
            case 2:
                Log.v(str3, str2);
                return;
            case 4:
                Log.i(str3, str2);
                return;
            case 5:
                Log.w(str3, str2);
                return;
            case 6:
                Log.e(str3, str2);
                return;
            case 7:
                Log.wtf(str3, str2);
                return;
            default:
                Log.d(str3, str2);
                return;
        }
    }
}
