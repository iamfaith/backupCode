package cn.bmob.v3.listener;

import cn.bmob.v3.exception.BmobException;

public abstract class DeleteBatchListener extends BmobCallback2<String[], BmobException> {
    public abstract void done(String[] strArr, BmobException bmobException);
}
