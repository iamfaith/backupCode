package cn.bmob.v3.datatype;

import java.io.Serializable;

public class BmobSmsState implements Serializable {
    private static final long serialVersionUID = 1;
    private String Code;
    private String V;

    public BmobSmsState(String smsState, String verifyState) {
        this.Code = smsState;
        this.V = verifyState;
    }

    public String getSmsState() {
        return this.Code;
    }

    public void setSmsState(String smsState) {
        this.Code = smsState;
    }

    public String getVerifyState() {
        return this.V;
    }

    public void setVerifyState(String verifyState) {
        this.V = verifyState;
    }
}
