package cn.bmob.v3.listener;

public abstract class BmobCallback2<T1, T2> extends BmobCallback {
    public abstract void done(T1 t1, T2 t2);
}
