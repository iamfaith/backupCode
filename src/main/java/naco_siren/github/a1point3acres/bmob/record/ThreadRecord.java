package naco_siren.github.a1point3acres.bmob.record;

import cn.bmob.v3.BmobObject;

public class ThreadRecord extends BmobObject {
    private String threadId;
    private String userId;

    public String getUserId() {
        return this.userId;
    }

    public String getThreadId() {
        return this.threadId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
}
