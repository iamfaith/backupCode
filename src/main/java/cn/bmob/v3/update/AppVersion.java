package cn.bmob.v3.update;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class AppVersion extends BmobObject {
    private static final long serialVersionUID = 64546978636988120L;
    private String android_url;
    private String channel;
    private String ios_url;
    private Boolean isforce;
    private BmobFile path;
    private String platform;
    private String target_size;
    private String update_log;
    private String version;
    private Integer version_i;

    public AppVersion(String tableName) {
        super(tableName);
    }

    public String getUpdate_log() {
        return this.update_log;
    }

    public void setUpdate_log(String update_log) {
        this.update_log = update_log;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getVersion_i() {
        return this.version_i;
    }

    public void setVersion_i(Integer version_i) {
        this.version_i = version_i;
    }

    public Boolean getIsforce() {
        return this.isforce;
    }

    public void setIsforce(Boolean isforce) {
        this.isforce = isforce;
    }

    public BmobFile getPath() {
        return this.path;
    }

    public void setPath(BmobFile path) {
        this.path = path;
    }

    public String getTarget_size() {
        return this.target_size;
    }

    public void setTarget_size(String target_size) {
        this.target_size = target_size;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAndroid_url() {
        return this.android_url;
    }

    public void setAndroid_url(String android_url) {
        this.android_url = android_url;
    }

    public String getIos_url() {
        return this.ios_url;
    }

    public void setIos_url(String ios_url) {
        this.ios_url = ios_url;
    }
}
