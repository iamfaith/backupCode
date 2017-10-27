package cn.bmob.v3.http.bean;

import org.apache.http.cookie.ClientCookie;
import org.json.JSONException;
import org.json.JSONObject;

public class Upyun {
    private String domain;
    private String name;
    private String secret;

    public static Upyun parse(JSONObject upyun) throws JSONException {
        String str = "";
        String str2 = "";
        String str3 = "";
        if (upyun.has("name")) {
            str = upyun.getString("name");
        }
        if (upyun.has(ClientCookie.DOMAIN_ATTR)) {
            str3 = upyun.getString(ClientCookie.DOMAIN_ATTR);
        }
        if (upyun.has("secret")) {
            str2 = upyun.getString("secret");
        }
        return new Upyun(str, str3, str2);
    }

    public Upyun(String name, String domain, String secret) {
        this.name = name;
        this.domain = domain;
        this.secret = secret;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String toString() {
        return super.toString();
    }
}
