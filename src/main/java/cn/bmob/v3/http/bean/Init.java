package cn.bmob.v3.http.bean;

public class Init {
    private String api;
    private String file;
    private String io;
    private boolean isUp;
    private String other;
    private String push;
    private long timestamp;
    private int upyunVer;

    public String getApi() {
        return this.api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getPush() {
        return this.push;
    }

    public void setPush(String push) {
        this.push = push;
    }

    public String getIo() {
        return this.io;
    }

    public void setIo(String io) {
        this.io = io;
    }

    public String getOther() {
        return this.other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isUp() {
        return this.isUp;
    }

    public void setUp(boolean isUp) {
        this.isUp = isUp;
    }

    public int getUpyunVer() {
        return this.upyunVer;
    }

    public void setUpyunVer(int upyunVer) {
        this.upyunVer = upyunVer;
    }
}
