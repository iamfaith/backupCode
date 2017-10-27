package naco_siren.github.a1point3acres.bmob.user;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;

public class User extends BmobUser {
    private Integer blogStartupCount;
    private BmobDate lastLoginDate;
    private Integer mainStartupCount;
    private String pwd;
    private Integer settingsStartupCount;
    private Integer threadStartupCount;
    private String userId;
    private String userRank;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setUserRank(String userRank) {
        this.userRank = userRank;
    }

    public void setLastLoginDate(BmobDate lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public void setMainStartupCount(Integer mainStartupCount) {
        this.mainStartupCount = mainStartupCount;
    }

    public void setThreadStartupCount(Integer threadStartupCount) {
        this.threadStartupCount = threadStartupCount;
    }

    public void setBlogStartupCount(Integer blogStartupCount) {
        this.blogStartupCount = blogStartupCount;
    }

    public void setSettingsStartupCount(Integer settingsStartupCount) {
        this.settingsStartupCount = settingsStartupCount;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getPwd() {
        return this.pwd;
    }

    public String getUserRank() {
        return this.userRank;
    }

    public BmobDate getLastLoginDate() {
        return this.lastLoginDate;
    }

    public Integer getMainStartupCount() {
        return this.mainStartupCount;
    }

    public Integer getThreadStartupCount() {
        return this.threadStartupCount;
    }

    public Integer getBlogStartupCount() {
        return this.blogStartupCount;
    }

    public Integer getSettingsStartupCount() {
        return this.settingsStartupCount;
    }
}
