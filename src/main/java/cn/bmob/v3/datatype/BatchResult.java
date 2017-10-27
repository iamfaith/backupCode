package cn.bmob.v3.datatype;

import cn.bmob.v3.exception.BmobException;
import java.io.Serializable;

public class BatchResult implements Serializable {
    private static final long serialVersionUID = 1;
    private BmobException B;
    private boolean Code;
    private String I;
    private String V;
    private String Z;

    public boolean isSuccess() {
        return this.Code;
    }

    public void setSuccess(boolean isSuccess) {
        this.Code = isSuccess;
    }

    public String getObjectId() {
        return this.V;
    }

    public void setObjectId(String objectId) {
        this.V = objectId;
    }

    public String getCreatedAt() {
        return this.I;
    }

    public void setCreatedAt(String createdAt) {
        this.I = createdAt;
    }

    public String getUpdatedAt() {
        return this.Z;
    }

    public void setUpdatedAt(String updatedAt) {
        this.Z = updatedAt;
    }

    public BmobException getError() {
        return this.B;
    }

    public void setError(BmobException error) {
        this.B = error;
    }
}
