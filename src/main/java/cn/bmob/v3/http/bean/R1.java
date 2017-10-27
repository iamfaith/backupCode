package cn.bmob.v3.http.bean;

import cn.bmob.v3.exception.BmobException;

public class R1 {
    private Boolean b;
    private BmobException e;

    public R1(Boolean b, BmobException e) {
        this.b = b;
        this.e = e;
    }

    public Boolean getB() {
        return this.b;
    }

    public void setB(Boolean b) {
        this.b = b;
    }

    public BmobException getE() {
        return this.e;
    }

    public void setE(BmobException e) {
        this.e = e;
    }
}
