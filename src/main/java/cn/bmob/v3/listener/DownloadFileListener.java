package cn.bmob.v3.listener;

import android.os.Handler;
import android.os.Looper;
import cn.bmob.v3.datatype.a.This;
import cn.bmob.v3.exception.BmobException;

public abstract class DownloadFileListener extends BmobErrorCallback<String> implements ProgressCallback {
    public abstract void done(String str, BmobException bmobException);

    public void doneError(final int code, final String msg) {
        if (This.Code()) {
            done(null, new BmobException(code, msg));
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable(this) {
                private /* synthetic */ DownloadFileListener I;

                public final void run() {
                    this.I.done(null, new BmobException(code, msg));
                }
            });
        }
    }
}
