package cn.bmob.v3.b;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cn.bmob.v3.Bmob;

/* compiled from: SPUtil */
public final class The {
    private SharedPreferences Code;
    private Editor V;

    public The() {
        this(Bmob.getApplicationContext().getSharedPreferences("bmob_sp", 0));
    }

    @SuppressLint({"CommitPrefEdits"})
    private The(SharedPreferences sharedPreferences) {
        this.Code = null;
        this.V = null;
        this.Code = sharedPreferences;
        this.V = sharedPreferences.edit();
    }

    public final void Code(String str, int i) {
        this.V.putInt(str, i);
        this.V.commit();
    }

    public final void Code(String str, String str2) {
        this.V.putString(str, str2);
        this.V.commit();
    }

    public final int V(String str, int i) {
        return this.Code.getInt(str, -1);
    }

    public final String V(String str, String str2) {
        return this.Code.getString(str, str2);
    }

    public final void Code(String str) {
        this.V.remove(str);
        this.V.commit();
    }
}
