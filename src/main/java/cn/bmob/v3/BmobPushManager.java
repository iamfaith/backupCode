package cn.bmob.v3;

import cn.bmob.v3.http.acknowledge;
import cn.bmob.v3.http.acknowledge.This;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.PushListener;
import java.util.Collections;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscription;

public class BmobPushManager<T> {
    private BmobQuery<T> Code;

    public Subscription pushMessage(String message) {
        return pushMessage(message, null);
    }

    public Subscription pushMessage(String message, PushListener listener) {
        if (this.Code != null) {
            return Code(message, this.Code.getWhere(), listener).V();
        }
        return pushMessageAll(message, listener);
    }

    public Subscription pushMessage(JSONObject data) {
        return pushMessage(data, null);
    }

    public Subscription pushMessage(JSONObject data, PushListener listener) {
        if (this.Code != null) {
            return Code(data, this.Code.getWhere(), listener).V();
        }
        return pushMessageAll(data, listener);
    }

    public Subscription pushMessageAll(String message) {
        return pushMessageAll(message, null);
    }

    public Subscription pushMessageAll(JSONObject data) {
        return pushMessageAll(data, null);
    }

    public Subscription pushMessageAll(String message, PushListener listener) {
        return Code(message, null, listener).V();
    }

    public Observable<Void> pushMessageAllObservable(String message) {
        return Code(message, null, null).Code();
    }

    public Subscription pushMessageAll(JSONObject data, PushListener listener) {
        return Code(data, null, listener).V();
    }

    public Observable<Void> pushMessageAllObservable(JSONObject data) {
        return Code(data, null, null).Code();
    }

    private static acknowledge Code(String str, JSONObject jSONObject, PushListener pushListener) {
        JSONObject jSONObject2 = new JSONObject();
        try {
            JSONObject jSONObject3 = new JSONObject();
            jSONObject3.put("alert", str);
            JSONObject jSONObject4 = new JSONObject();
            jSONObject4.put("data", jSONObject3);
            if (jSONObject != null) {
                jSONObject4.put("where", jSONObject);
            }
            jSONObject2.put("data", jSONObject4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new This().Code(Collections.emptyList()).Code("http://open.bmob.cn/8/push", jSONObject2).I().V((BmobCallback) pushListener).C();
    }

    private static acknowledge Code(JSONObject jSONObject, JSONObject jSONObject2, PushListener pushListener) {
        JSONObject jSONObject3 = new JSONObject();
        try {
            JSONObject jSONObject4 = new JSONObject();
            jSONObject4.put("data", jSONObject);
            if (jSONObject2 != null) {
                jSONObject4.put("where", jSONObject2);
            }
            jSONObject3.put("data", jSONObject4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new This().Code(Collections.emptyList()).Code("http://open.bmob.cn/8/push", jSONObject3).I().V((BmobCallback) pushListener).C();
    }

    public BmobQuery<T> getQuery() {
        return this.Code;
    }

    public void setQuery(BmobQuery<T> query) {
        this.Code = query;
    }
}
