package cn.bmob.v3.listener;

import cn.bmob.v3.exception.BmobException;

public abstract class UploadFileListener extends BmobErrorCallback<Void> {
    public abstract void done(BmobException bmobException);

    public void onProgress(Integer num) {
    }

    protected final void done(Void voidR, BmobException e) {
        done(e);
    }
}
