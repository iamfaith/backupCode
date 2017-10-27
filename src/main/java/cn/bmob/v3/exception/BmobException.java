package cn.bmob.v3.exception;

import cn.bmob.v3.helper.ErrorCode;

public class BmobException extends Exception {
    private static final long serialVersionUID = 1;
    private int Code;

    public BmobException(String detailMessage) {
        super(detailMessage);
    }

    public BmobException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BmobException(Throwable throwable) {
        super(throwable);
        this.Code = ErrorCode.E9015;
    }

    public BmobException(int exceptionCode) {
        this.Code = exceptionCode;
    }

    public BmobException(int exceptionCode, String detailMessage) {
        super(detailMessage);
        this.Code = exceptionCode;
    }

    public BmobException(int exceptionCode, String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        this.Code = exceptionCode;
    }

    public BmobException(int exceptionCode, Throwable throwable) {
        super(throwable);
        this.Code = exceptionCode;
    }

    public int getErrorCode() {
        return this.Code;
    }

    public String toString() {
        return "errorCode:" + this.Code + ",errorMsg:" + getMessage();
    }
}
