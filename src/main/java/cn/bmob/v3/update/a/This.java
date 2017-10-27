package cn.bmob.v3.update.a;

import android.text.TextUtils;
import cn.bmob.v3.b.From;

/* compiled from: UpdateConfig */
public final class This {
    private static boolean B = false;
    private static String Code;
    private static boolean I = false;
    private static boolean V = true;
    private static boolean Z = true;

    public static boolean Code() {
        return V;
    }

    public static void Code(boolean z) {
        V = z;
    }

    public static String V() {
        if (TextUtils.isEmpty(Code)) {
            Code = From.Code("BMOB_CHANNEL");
        }
        return Code;
    }

    public static boolean I() {
        return I;
    }

    public static void V(boolean z) {
        I = z;
    }

    public static boolean Z() {
        return Z;
    }

    public static void I(boolean z) {
        Z = z;
    }

    public static boolean B() {
        return B;
    }

    public static void Z(boolean z) {
        B = z;
    }
}
