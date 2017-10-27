package cn.bmob.v3;

import cn.bmob.v3.http.acknowledge;
import cn.bmob.v3.http.acknowledge.This;
import cn.bmob.v3.http.thing;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.CloudCodeListener;
import com.google.gson.JsonElement;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class AsyncCustomEndpoints {
    public Subscription callEndpoint(String method, CloudCodeListener listener) {
        return callEndpoint(method, null, listener);
    }

    public Observable<Object> callEndpointObservable(String method) {
        return callEndpointObservable(method, null);
    }

    public Subscription callEndpoint(String method, JSONObject cloudCodeParams, CloudCodeListener listener) {
        return Code(method, cloudCodeParams, listener).V();
    }

    public Observable<Object> callEndpointObservable(String method, JSONObject cloudCodeParams) {
        return Code(method, cloudCodeParams, null).Code();
    }

    private acknowledge Code(String str, JSONObject jSONObject, CloudCodeListener cloudCodeListener) {
        JSONObject jSONObject2 = new JSONObject();
        try {
            JSONObject jSONObject3 = new JSONObject();
            jSONObject3.put("_e", str);
            if (jSONObject != null) {
                Iterator keys = jSONObject.keys();
                while (keys.hasNext()) {
                    String obj = keys.next().toString();
                    jSONObject3.put(obj, jSONObject.get(obj));
                }
            }
            jSONObject2.put("data", jSONObject3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new This().Code(thing.Code("no check", "no check")).Code("http://open.bmob.cn/8/functions", jSONObject2).Code(new Func1<JsonElement, Object>(this) {
            public final /* synthetic */ Object call(Object obj) {
                return AnonymousClass1.Code((JsonElement) obj);
            }

            private static Object Code(JsonElement jsonElement) {
                try {
                    JsonElement jsonElement2 = jsonElement.getAsJsonObject().get("results");
                    if (jsonElement2.isJsonObject()) {
                        return jsonElement2.getAsJsonObject().toString();
                    }
                    if (jsonElement2.isJsonArray()) {
                        return jsonElement2.getAsJsonArray().toString();
                    }
                    return jsonElement2.getAsString().toString();
                } catch (Exception e) {
                    Object jsonElement3 = jsonElement.toString();
                    if (jsonElement3.startsWith("\"")) {
                        return jsonElement3.substring(0, jsonElement3.length() - 1).substring(1);
                    }
                    return jsonElement3;
                }
            }
        }).V((BmobCallback) cloudCodeListener).C();
    }
}
