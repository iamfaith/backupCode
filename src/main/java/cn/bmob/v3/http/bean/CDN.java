package cn.bmob.v3.http.bean;

import cn.bmob.v3.BmobConstants;
import org.json.JSONException;
import org.json.JSONObject;

public class CDN {
    private Upyun upyun;

    public static CDN parse(String cdn) throws JSONException {
        return new CDN(Upyun.parse(new JSONObject(cdn).getJSONObject(BmobConstants.TYPE_CDN)));
    }

    public CDN(Upyun upyun) {
        this.upyun = upyun;
    }

    public Upyun getUpyun() {
        return this.upyun;
    }

    public void setUpyun(Upyun upyun) {
        this.upyun = upyun;
    }
}
