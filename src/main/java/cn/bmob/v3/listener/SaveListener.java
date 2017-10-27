package cn.bmob.v3.listener;

import cn.bmob.v3.exception.BmobException;

public abstract class SaveListener<T> extends BmobCallback2<T, BmobException> {
    public abstract void done(T t, BmobException bmobException);
}
