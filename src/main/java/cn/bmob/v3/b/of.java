package cn.bmob.v3.b;

import android.content.Context;
import android.content.res.Resources;

/* compiled from: BmobResource */
public class of {
    private static of Code = null;
    private final String I;
    private Resources V;

    static {
        of.class.getName();
    }

    private of(Context context) {
        this.V = context.getResources();
        this.I = context.getPackageName();
    }

    public static synchronized of Code(Context context) {
        of ofVar;
        synchronized (of.class) {
            if (Code == null) {
                Code = new of(context.getApplicationContext());
            }
            ofVar = Code;
        }
        return ofVar;
    }

    public final int Code(String str) {
        return Code(str, "id");
    }

    public final int V(String str) {
        return Code(str, "layout");
    }

    public final int I(String str) {
        return Code(str, "string");
    }

    private int Code(String str, String str2) {
        int identifier = this.V.getIdentifier(str, str2, this.I);
        if (identifier != 0) {
            return identifier;
        }
        This.V("getRes(" + str2 + "/ " + str + ")");
        This.Code("Error getting resource. Make sure you have copied all resources (res/) from SDK to your project. ");
        return 0;
    }
}
