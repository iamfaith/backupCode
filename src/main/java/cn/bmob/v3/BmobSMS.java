package cn.bmob.v3;

import cn.bmob.v3.datatype.BmobSmsState;
import cn.bmob.v3.http.acknowledge;
import cn.bmob.v3.http.acknowledge.This;
import cn.bmob.v3.http.bean.R1;
import cn.bmob.v3.http.thing;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import com.google.gson.JsonElement;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class BmobSMS {
    private static acknowledge Code(List<R1> list, String str, JSONObject jSONObject, QueryListener<Integer> queryListener) {
        return new This().Code((List) list).Code(str, jSONObject).Code(new Func1<JsonElement, Integer>() {
            public final /* synthetic */ Object call(Object obj) {
                return Integer.valueOf(((JsonElement) obj).getAsJsonObject().get("smsId").getAsInt());
            }
        }).V((BmobCallback) queryListener).C();
    }

    private static acknowledge Code(String str, String str2, String str3, QueryListener<Integer> queryListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("mobilePhoneNumber", str);
            jSONObject2.put("content", str2);
            jSONObject2.put("sendTime", str3);
            jSONObject.put("data", jSONObject2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Code(thing.Code(str, "phoneNumber can't be empty", str2, "smsContent can't be empty", str3, "sendTime can't be empty"), "http://open.bmob.cn/8/request_sms", jSONObject, (QueryListener) queryListener);
    }

    public static Subscription requestSMS(String phoneNumber, String smsContent, String sendTime, QueryListener<Integer> listener) {
        return Code(phoneNumber, smsContent, sendTime, (QueryListener) listener).V();
    }

    public static Observable<Integer> requestSMSObservable(String phoneNumber, String smsContent, String sendTime) {
        return Code(phoneNumber, smsContent, sendTime, null).Code();
    }

    private static acknowledge Code(String str, String str2, QueryListener<Integer> queryListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("mobilePhoneNumber", str);
            jSONObject2.put("template", str2);
            jSONObject.put("data", jSONObject2);
        } catch (JSONException e) {
        }
        return Code(thing.Code((Object) str, "phoneNumber can't be empty", (Object) str2, "template can't be empty"), "http://open.bmob.cn/8/request_sms_code", jSONObject, (QueryListener) queryListener);
    }

    public static Subscription requestSMSCode(String phoneNumber, String template, QueryListener<Integer> listener) {
        return Code(phoneNumber, template, (QueryListener) listener).V();
    }

    public static Observable<Integer> requestSMSCodeObservable(String phoneNumber, String template) {
        return Code(phoneNumber, template, null).Code();
    }

    private static acknowledge Code(String str, String str2, UpdateListener updateListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("mobilePhoneNumber", str);
            jSONObject2.put("smsCode", str2);
            jSONObject.put("data", jSONObject2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new This().Code(thing.Code((Object) str, "phoneNumber can't be empty", (Object) str2, "smsCode can't be empty")).Code("http://open.bmob.cn/8/verify_sms_code", jSONObject).I().V((BmobCallback) updateListener).C();
    }

    public static Subscription verifySmsCode(String phoneNumber, String smsCode, UpdateListener listener) {
        return Code(phoneNumber, smsCode, listener).V();
    }

    public static Observable<Void> verifySmsCodeObservable(String phoneNumber, String smsCode) {
        return Code(phoneNumber, smsCode, null).Code();
    }

    private static acknowledge Code(Integer num, QueryListener<BmobSmsState> queryListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("smsId", num);
            jSONObject.put("data", jSONObject2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new This().Code(thing.Code((Object) num, "smsId is null")).Code("http://open.bmob.cn/8/query_sms", jSONObject).Code(new Func1<JsonElement, BmobSmsState>() {
            public final /* synthetic */ Object call(Object obj) {
                JsonElement jsonElement = (JsonElement) obj;
                return new BmobSmsState(jsonElement.getAsJsonObject().get("sms_state").getAsString(), jsonElement.getAsJsonObject().get("verify_state").getAsString());
            }
        }).V((BmobCallback) queryListener).C();
    }

    public static Subscription querySmsState(Integer smsId, QueryListener<BmobSmsState> listener) {
        return Code(smsId, listener).V();
    }

    public static Observable<BmobSmsState> querySmsStateObservable(Integer smsId) {
        return Code(smsId, null).Code();
    }
}
