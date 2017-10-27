package cn.bmob.v3.listener;

import cn.bmob.v3.exception.BmobException;

public abstract class CloudCodeListener extends BmobCallback2<Object, BmobException> {
    public abstract void done(Object obj, BmobException bmobException);
}
