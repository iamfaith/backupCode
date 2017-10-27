package cn.bmob.v3.listener;

import android.os.Handler;
import android.os.Looper;
import cn.bmob.v3.datatype.a.This;

public abstract class BmobCallback {
    public void onStart() {
    }

    public void onFinish() {
    }

    public void internalStart() {
        if (This.Code()) {
            onStart();
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable(this) {
                private /* synthetic */ BmobCallback Code;

                {
                    this.Code = r1;
                }

                public final void run() {
                    this.Code.onStart();
                }
            });
        }
    }
}
