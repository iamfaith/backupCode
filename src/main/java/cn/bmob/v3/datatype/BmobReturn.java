package cn.bmob.v3.datatype;

import cn.bmob.v3.exception.BmobException;

public class BmobReturn<T> {
    private T Code;
    private BmobException V;

    public BmobReturn(T t, BmobException e) {
        this.Code = t;
        this.V = e;
    }

    public T getT() {
        return this.Code;
    }

    public void setT(T t) {
        this.Code = t;
    }

    public BmobException getE() {
        return this.V;
    }

    public void setE(BmobException e) {
        this.V = e;
    }
}
