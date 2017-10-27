package cn.bmob.v3;

import android.text.TextUtils;
import cn.bmob.v3.b.I;
import cn.bmob.v3.b.The;
import cn.bmob.v3.helper.GsonUtil;
import cn.bmob.v3.http.acknowledge;
import cn.bmob.v3.http.acknowledge.This;
import cn.bmob.v3.http.bean.R1;
import cn.bmob.v3.http.thing;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import com.google.gson.JsonElement;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class BmobUser extends BmobObject {
    static JSONObject current = null;
    private static final long serialVersionUID = -1589804003600796026L;
    private String email;
    private Boolean emailVerified;
    private String mobilePhoneNumber;
    private Boolean mobilePhoneNumberVerified;
    private String password;
    private String sessionToken;
    private String username;

    public static class BmobThirdUserAuth {
        public static final String SNS_TYPE_QQ = "qq";
        public static final String SNS_TYPE_WEIBO = "weibo";
        public static final String SNS_TYPE_WEIXIN = "weixin";
        private String Code;
        private String I;
        private String V;
        private String Z;

        public BmobThirdUserAuth(String snsType, String accessToken, String expiresIn, String userId) {
            this.V = accessToken;
            this.Code = snsType;
            this.I = expiresIn;
            this.Z = userId;
        }

        protected static String getPlatformIdByType(String type) {
            if (SNS_TYPE_QQ.equalsIgnoreCase(type) || SNS_TYPE_WEIXIN.equalsIgnoreCase(type)) {
                return "openid";
            }
            return "uid";
        }

        public String getAccessToken() {
            return this.V;
        }

        public void setAccessToken(String accessToken) {
            this.V = accessToken;
        }

        public String getUserId() {
            return this.Z;
        }

        public void setUserId(String userId) {
            this.Z = userId;
        }

        public String getExpiresIn() {
            return this.I;
        }

        public void setExpiresIn(String expiresIn) {
            this.I = expiresIn;
        }

        public String getSnsType() {
            return this.Code;
        }

        public void setSnsType(String snsType) {
            this.Code = snsType;
        }

        public JSONObject toJSONObject() {
            JSONObject jSONObject = new JSONObject();
            try {
                JSONObject jSONObject2 = new JSONObject();
                if (!TextUtils.isEmpty(this.Code)) {
                    jSONObject2.put(getPlatformIdByType(this.Code), this.Z);
                }
                jSONObject2.put("access_token", this.V);
                if (SNS_TYPE_QQ.equalsIgnoreCase(this.Code)) {
                    jSONObject2.put("expires_in", Double.parseDouble(this.I));
                } else if (SNS_TYPE_WEIBO.equalsIgnoreCase(this.Code)) {
                    jSONObject2.put("expires_in", Long.parseLong(this.I));
                } else {
                    jSONObject2.put("expires_in", Long.parseLong(this.I));
                }
                jSONObject.put(this.Code, jSONObject2);
            } catch (JSONException e) {
            }
            return jSONObject;
        }
    }

    public String getTableName() {
        return "_User";
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerified() {
        return this.emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getSessionToken() {
        return this.sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getMobilePhoneNumber() {
        return this.mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public Boolean getMobilePhoneNumberVerified() {
        return this.mobilePhoneNumberVerified;
    }

    public void setMobilePhoneNumberVerified(Boolean mobilePhoneNumberVerified) {
        this.mobilePhoneNumberVerified = mobilePhoneNumberVerified;
    }

    public <T> Subscription signOrLogin(String smsCode, SaveListener<T> listener) {
        return Code(thing.Code(smsCode, " Verify code can't be empty "), "http://open.bmob.cn/8/login_or_signup", smsCode, (SaveListener) listener);
    }

    public <T> Observable<T> signOrLoginObservable(Class<T> clazz, String smsCode) {
        if (clazz == null) {
            throw new IllegalArgumentException(BmobConstants.ERROR_CLASS);
        }
        return Code((Class) clazz, thing.Code(smsCode, " Verify code can't be empty "), "http://open.bmob.cn/8/login_or_signup", smsCode, null).Code();
    }

    public <T> Observable<T> signUpObservable(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException(BmobConstants.ERROR_CLASS);
        }
        return Code((Class) clazz, thing.Code("no check", " no check "), "http://open.bmob.cn/8/signup", null, null).Code();
    }

    public <T> Subscription signUp(SaveListener<T> listener) {
        return Code(thing.Code("no check", " no check "), "http://open.bmob.cn/8/signup", null, (SaveListener) listener);
    }

    private <T> acknowledge Code(final Class<T> cls, List<R1> list, String str, final String str2, SaveListener<T> saveListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject currentData = getCurrentData();
            if (!TextUtils.isEmpty(str2)) {
                currentData.put("smsCode", str2);
            }
            jSONObject.put("data", currentData);
            jSONObject.put("c", getTableName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new This().Code((List) list).Code(str, jSONObject).Code(new Action1<JsonElement>(this) {
            private /* synthetic */ BmobUser V;

            public final /* synthetic */ void call(Object obj) {
                String jSONObject;
                JsonElement jsonElement = (JsonElement) obj;
                if (TextUtils.isEmpty(str2)) {
                    JSONObject currentData = this.V.getCurrentData();
                    currentData.remove("password");
                    jSONObject = currentData.toString();
                } else {
                    jSONObject = jsonElement.getAsJsonObject().toString();
                }
                new The().Code("user", jSONObject);
                new The().Code("sessionToken", this.V.sessionToken);
            }
        }).Code(new Func1<JsonElement, Object>(this) {
            private /* synthetic */ BmobUser I;

            public final /* synthetic */ Object call(Object obj) {
                String jSONObject;
                JsonElement jsonElement = (JsonElement) obj;
                this.I.setObjectId(jsonElement.getAsJsonObject().get("objectId").getAsString());
                this.I.setCreatedAt(jsonElement.getAsJsonObject().get("createdAt").getAsString());
                this.I.setSessionToken(jsonElement.getAsJsonObject().get("sessionToken").getAsString());
                if (TextUtils.isEmpty(str2)) {
                    JSONObject currentData = this.I.getCurrentData();
                    currentData.remove("password");
                    jSONObject = currentData.toString();
                } else {
                    jSONObject = jsonElement.getAsJsonObject().toString();
                }
                return GsonUtil.toObject(jSONObject, cls);
            }
        }).V((BmobCallback) saveListener).C();
    }

    private <T> Subscription Code(List<R1> list, String str, String str2, SaveListener<T> saveListener) {
        if (saveListener != null) {
            return Code((Class) ((ParameterizedType) saveListener.getClass().getGenericSuperclass()).getActualTypeArguments()[0], (List) list, str, str2, (SaveListener) saveListener).V();
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_LISTENER);
    }

    private <T> acknowledge Code(final Class<T> cls, SaveListener<T> saveListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("username", getUsername());
            jSONObject2.put("password", this.password);
            jSONObject.put("data", jSONObject2);
            jSONObject.put("c", getTableName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new This().Code(thing.Code(getUsername(), " username can't be empty ", this.password, " password can't be empty ")).Code("http://open.bmob.cn/8/login", jSONObject).Code(new Action1<JsonElement>(this) {
            public final /* synthetic */ void call(Object obj) {
                JsonElement jsonElement = (JsonElement) obj;
                String jsonObject = jsonElement.getAsJsonObject().toString();
                String asString = jsonElement.getAsJsonObject().get("sessionToken").getAsString();
                new The().Code("user", jsonObject);
                new The().Code("sessionToken", asString);
            }
        }).Code(new Func1<JsonElement, Object>(this) {
            private /* synthetic */ BmobUser V;

            public final /* synthetic */ Object call(Object obj) {
                JsonElement jsonElement = (JsonElement) obj;
                this.V.setObjectId(jsonElement.getAsJsonObject().get("objectId").getAsString());
                this.V.setCreatedAt(jsonElement.getAsJsonObject().get("createdAt").getAsString());
                this.V.setUpdatedAt(jsonElement.getAsJsonObject().get("updatedAt").getAsString());
                this.V.setSessionToken(jsonElement.getAsJsonObject().get("sessionToken").getAsString());
                if (jsonElement.getAsJsonObject().has("emailVerified")) {
                    this.V.setEmailVerified(Boolean.valueOf(jsonElement.getAsJsonObject().get("emailVerified").getAsBoolean()));
                }
                return GsonUtil.toObject(jsonElement, cls);
            }
        }).V((BmobCallback) saveListener).C();
    }

    public <T> Subscription login(SaveListener<T> listener) {
        if (listener != null) {
            return Code((Class) ((ParameterizedType) listener.getClass().getGenericSuperclass()).getActualTypeArguments()[0], (SaveListener) listener).V();
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_LISTENER);
    }

    public <T> Observable<T> loginObservable(Class<T> clazz) {
        if (clazz != null) {
            return Code((Class) clazz, null).Code();
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_CLASS);
    }

    public Subscription save() {
        throw new IllegalArgumentException(" _User does not support save operation,please use login / signUp method");
    }

    public Subscription save(SaveListener saveListener) {
        throw new IllegalArgumentException(" _User does not support save operation,please use login / signUp method");
    }

    public Subscription update() {
        return update(null);
    }

    public Subscription update(UpdateListener listener) {
        return update(getObjectId(), listener);
    }

    private acknowledge Code(final String str, UpdateListener updateListener) {
        final JSONObject jSONObject = new JSONObject();
        try {
            current = new JSONObject(GsonUtil.toJson(this));
            JSONObject currentData = getCurrentData();
            currentData.remove("objectId");
            currentData.remove("sessionToken");
            currentData.remove("createdAt");
            currentData.remove("updatedAt");
            if (increments.size() > 0) {
                for (JSONObject jSONObject2 : increments) {
                    String optString = jSONObject2.optString("key");
                    jSONObject2.remove("key");
                    currentData.put(optString, jSONObject2);
                }
                increments.clear();
            }
            jSONObject.put("data", currentData);
            jSONObject.put("c", getTableName());
            jSONObject.put("objectId", str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new This().Code(thing.Code(str, " objectId can't be empty ")).Code("http://open.bmob.cn/8/update", jSONObject).Code(new Action1<JsonElement>(this) {
            public final /* synthetic */ void call(Object obj) {
                try {
                    BmobUser.current.put("updatedAt", ((JsonElement) obj).getAsJsonObject().get("updatedAt").getAsString());
                    if (str.equals(BmobUser.getCurrentUser().getObjectId())) {
                        I.Code(Bmob.getApplicationContext()).Code(BmobUser.current.toString(), jSONObject.getJSONObject("data").toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).Code(new Func1<JsonElement, Object>(this) {
            private /* synthetic */ BmobUser V;

            public final /* synthetic */ Object call(Object obj) {
                this.V.setUpdatedAt(((JsonElement) obj).getAsJsonObject().get("updatedAt").getAsString());
                this.V.setObjectId(str);
                return null;
            }
        }).V((BmobCallback) updateListener).C();
    }

    public Observable<Void> updateObservable(String objectId) {
        return Code(objectId, null).Code();
    }

    public Subscription update(String objectId, UpdateListener listener) {
        return Code(objectId, listener).V();
    }

    private static acknowledge Code(BmobThirdUserAuth bmobThirdUserAuth, LogInListener<JSONObject> logInListener) {
        final JSONObject jSONObject = bmobThirdUserAuth == null ? new JSONObject() : bmobThirdUserAuth.toJSONObject();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("data", new JSONObject().put("authData", jSONObject));
            jSONObject2.put("c", "_User");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new This().Code(thing.Code((Object) bmobThirdUserAuth, "authInfo is null")).Code("http://open.bmob.cn/8/login_or_signup", jSONObject2).Code(new Action1<JsonElement>() {
            public final /* synthetic */ void call(Object obj) {
                JsonElement jsonElement = (JsonElement) obj;
                try {
                    new The().Code("user", jsonElement.toString());
                    new The().Code("sessionToken", jsonElement.getAsJsonObject().get("sessionToken").getAsString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).Code(new Func1<JsonElement, Object>() {
            public final /* bridge */ /* synthetic */ Object call(Object obj) {
                return jSONObject;
            }
        }).V((BmobCallback) logInListener).C();
    }

    public static Subscription loginWithAuthData(BmobThirdUserAuth authInfo, LogInListener<JSONObject> listener) {
        return Code(authInfo, (LogInListener) listener).V();
    }

    public static Observable<JSONObject> loginWithAuthDataObservable(BmobThirdUserAuth authInfo) {
        return Code(authInfo, null).Code();
    }

    private static acknowledge Code(BmobThirdUserAuth bmobThirdUserAuth, UpdateListener updateListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("data", new JSONObject().put("authData", bmobThirdUserAuth.toJSONObject()));
            jSONObject.put("c", "_User");
            jSONObject.put("objectId", getCurrentUser().getObjectId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        thing.Code();
        return thing.Code(thing.Code(getCurrentUser(), " user must be login before associate ", (Object) bmobThirdUserAuth, " authInfo is null"), "http://open.bmob.cn/8/update", jSONObject, (BmobCallback) updateListener);
    }

    public static Observable<Void> associateWithAuthDataObservable(BmobThirdUserAuth authInfo, UpdateListener listener) {
        return Code(authInfo, listener).Code();
    }

    public static Subscription associateWithAuthData(BmobThirdUserAuth authInfo, UpdateListener listener) {
        return Code(authInfo, listener).V();
    }

    private static acknowledge V(String str, UpdateListener updateListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("authData", new JSONObject().put(str, JSONObject.NULL));
            jSONObject.put("data", jSONObject2);
            jSONObject.put("c", "_User");
            jSONObject.put("objectId", getCurrentUser().getObjectId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        thing.Code();
        return thing.Code(thing.Code(getCurrentUser(), " user must be login before disassociate ", (Object) str, " type  is null"), "http://open.bmob.cn/8/update", jSONObject, (BmobCallback) updateListener);
    }

    public Subscription dissociateAuthData(String type, UpdateListener listener) {
        return V(type, listener).V();
    }

    public Observable<Void> dissociateAuthDataObservable(String type) {
        return V(type, null).Code();
    }

    private static acknowledge I(String str, UpdateListener updateListener) {
        JSONObject jSONObject;
        JSONException e;
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject = new JSONObject();
            try {
                jSONObject.put("data", new JSONObject().put("email", str));
                jSONObject.put("c", "_User");
            } catch (JSONException e2) {
                e = e2;
                e.printStackTrace();
                thing.Code();
                return thing.Code(thing.Code(str, " email  can't be empty"), "http://open.bmob.cn/8/email_verify", jSONObject, (BmobCallback) updateListener);
            }
        } catch (JSONException e3) {
            JSONException jSONException = e3;
            jSONObject = jSONObject2;
            e = jSONException;
            e.printStackTrace();
            thing.Code();
            return thing.Code(thing.Code(str, " email  can't be empty"), "http://open.bmob.cn/8/email_verify", jSONObject, (BmobCallback) updateListener);
        }
        thing.Code();
        return thing.Code(thing.Code(str, " email  can't be empty"), "http://open.bmob.cn/8/email_verify", jSONObject, (BmobCallback) updateListener);
    }

    public static Subscription requestEmailVerify(String email, UpdateListener listener) {
        return I(email, listener).V();
    }

    public static Observable<Void> requestEmailVerifyObservable(String email) {
        return I(email, null).Code();
    }

    private static acknowledge Z(String str, UpdateListener updateListener) {
        JSONObject jSONObject;
        JSONException e;
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject = new JSONObject();
            try {
                jSONObject.put("data", new JSONObject().put("email", str));
            } catch (JSONException e2) {
                e = e2;
                e.printStackTrace();
                thing.Code();
                return thing.Code(thing.Code(str, " email  can't be empty"), "http://open.bmob.cn/8/reset", jSONObject, (BmobCallback) updateListener);
            }
        } catch (JSONException e3) {
            JSONException jSONException = e3;
            jSONObject = jSONObject2;
            e = jSONException;
            e.printStackTrace();
            thing.Code();
            return thing.Code(thing.Code(str, " email  can't be empty"), "http://open.bmob.cn/8/reset", jSONObject, (BmobCallback) updateListener);
        }
        thing.Code();
        return thing.Code(thing.Code(str, " email  can't be empty"), "http://open.bmob.cn/8/reset", jSONObject, (BmobCallback) updateListener);
    }

    public static Subscription resetPasswordByEmail(String email, UpdateListener listener) {
        return Z(email, listener).V();
    }

    public static Observable<Void> resetPasswordByEmailObservable(String email) {
        return Z(email, null).Code();
    }

    private static acknowledge Code(String str, String str2, UpdateListener updateListener) {
        JSONObject jSONObject;
        JSONException e;
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject = new JSONObject();
            try {
                jSONObject2 = new JSONObject();
                jSONObject2.put("smsCode", str);
                jSONObject2.put("password", str2);
                jSONObject.put("data", jSONObject2);
                jSONObject.put("c", "_User");
            } catch (JSONException e2) {
                e = e2;
                e.printStackTrace();
                thing.Code();
                return thing.Code(thing.Code(str, " Verify code can't be empty", str2, " newPassword can't be empty"), "http://open.bmob.cn/8/phone_reset", jSONObject, (BmobCallback) updateListener);
            }
        } catch (JSONException e3) {
            JSONException jSONException = e3;
            jSONObject = jSONObject2;
            e = jSONException;
            e.printStackTrace();
            thing.Code();
            return thing.Code(thing.Code(str, " Verify code can't be empty", str2, " newPassword can't be empty"), "http://open.bmob.cn/8/phone_reset", jSONObject, (BmobCallback) updateListener);
        }
        thing.Code();
        return thing.Code(thing.Code(str, " Verify code can't be empty", str2, " newPassword can't be empty"), "http://open.bmob.cn/8/phone_reset", jSONObject, (BmobCallback) updateListener);
    }

    public static Subscription resetPasswordBySMSCode(String smsCode, String newPassword, UpdateListener listener) {
        return Code(smsCode, newPassword, listener).V();
    }

    public static Observable<Void> resetPasswordBySMSCodeObservable(String smsCode, String newPassword) {
        return Code(smsCode, newPassword, null).Code();
    }

    private static <T> acknowledge Code(Class<T> cls, String str, String str2, LogInListener<T> logInListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("username", str);
            jSONObject2.put("password", str2);
            jSONObject.put("data", jSONObject2);
            jSONObject.put("c", "_User");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return thing.Code().Code((Class) cls, thing.Code(str, " account can't be empty ", str2, " password can't be empty "), "http://open.bmob.cn/8/login", jSONObject, (LogInListener) logInListener);
    }

    public static <T> Subscription loginByAccount(String account, String password, LogInListener<T> listener) {
        if (listener != null) {
            return Code((Class) ((ParameterizedType) listener.getClass().getGenericSuperclass()).getActualTypeArguments()[0], account, password, (LogInListener) listener).V();
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_LISTENER);
    }

    public static <T> Observable<T> loginByAccountObservable(Class<T> clazz, String account, String password) {
        if (clazz != null) {
            return Code((Class) clazz, account, password, null).Code();
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_CLASS);
    }

    public static <T> Subscription loginBySMSCode(String phoneNumber, String smsCode, LogInListener<T> listener) {
        if (listener != null) {
            return Code((Class) ((ParameterizedType) listener.getClass().getGenericSuperclass()).getActualTypeArguments()[0], "http://open.bmob.cn/8/login", phoneNumber, smsCode, (LogInListener) listener).V();
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_LISTENER);
    }

    public static <T> Subscription signOrLoginByMobilePhone(String phoneNumber, String smsCode, LogInListener<T> listener) {
        if (listener != null) {
            return Code(null, "http://open.bmob.cn/8/login_or_signup", phoneNumber, smsCode, (LogInListener) listener).V();
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_LISTENER);
    }

    private static <T> acknowledge Code(Class<T> cls, String str, String str2, String str3, LogInListener<T> logInListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("mobilePhoneNumber", str2);
            jSONObject2.put("smsCode", str3);
            jSONObject.put("data", jSONObject2);
            jSONObject.put("c", "_User");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return thing.Code().Code((Class) cls, thing.Code(str2, " phoneNumber can't be empty ", str3, " smsCode can't be empty "), str, jSONObject, (LogInListener) logInListener);
    }

    public static Subscription updateCurrentUserPassword(String oldPwd, String newPwd, UpdateListener listener) {
        JSONObject jSONObject = new JSONObject();
        try {
            String objectId = getCurrentUser().getObjectId();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("oldPassword", oldPwd);
            jSONObject2.put("newPassword", newPwd);
            jSONObject.put("data", jSONObject2);
            jSONObject.put("objectId", objectId);
            jSONObject.put("c", "_User");
        } catch (Exception e) {
            e.printStackTrace();
        }
        thing.Code();
        return thing.Code(thing.Code(oldPwd, " oldPassword can't be empty", newPwd, " newPassword can't be empty", getCurrentUser(), "Cannot update user password until it has been logined. Please call login first."), "http://open.bmob.cn/8/update_user_password", jSONObject, (BmobCallback) listener).V();
    }

    public static <T> T getCurrentUser(Class<T> clazz) {
        return GsonUtil.toObject(new The().V("user", ""), (Class) clazz);
    }

    public static BmobUser getCurrentUser() {
        return (BmobUser) getCurrentUser(BmobUser.class);
    }

    public static void logOut() {
        new The().Code("user");
        new The().Code("sessionToken");
    }

    public static Object getObjectByKey(String key) {
        String V = new The().V("user", "");
        Object obj = null;
        try {
            obj = new JSONObject(V).opt(key);
        } catch (Exception e) {
        }
        return obj;
    }
}
