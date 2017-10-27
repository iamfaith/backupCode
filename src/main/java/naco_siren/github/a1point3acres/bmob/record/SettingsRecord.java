package naco_siren.github.a1point3acres.bmob.record;

import cn.bmob.v3.BmobObject;

public class SettingsRecord extends BmobObject {
    private String userId;

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
