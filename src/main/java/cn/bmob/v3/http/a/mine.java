package cn.bmob.v3.http.a;

import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.http.thing;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.QueryListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import rx.Subscription;

/* compiled from: PolicyFactory */
public final class mine {
    private static Map<CachePolicy, From> Code;
    private static byte[] I = new byte[0];
    private static volatile mine V;

    static {
        Map hashMap = new HashMap();
        Code = hashMap;
        hashMap.put(CachePolicy.IGNORE_CACHE, new darkness());
        Code.put(CachePolicy.NETWORK_ONLY, new acknowledge());
        Code.put(CachePolicy.CACHE_ONLY, new thing());
        Code.put(CachePolicy.CACHE_ELSE_NETWORK, new This());
        Code.put(CachePolicy.CACHE_THEN_NETWORK, new of());
        Code.put(CachePolicy.NETWORK_ELSE_CACHE, new I());
    }

    private mine() {
    }

    public static mine Code() {
        if (V == null) {
            synchronized (I) {
                if (V == null) {
                    V = new mine();
                }
            }
        }
        return V;
    }

    public final <T> Subscription Code(String str, Class<T> cls, CachePolicy cachePolicy, long j, String str2, JSONObject jSONObject, BmobCallback bmobCallback) {
        List Code;
        From from = (From) Code.get(cachePolicy);
        if (bmobCallback instanceof QueryListener) {
            Code = thing.Code(str, "objectId can't be empty");
        } else {
            Code = Collections.emptyList();
        }
        return from.Code(cls, Code, str2, jSONObject, j, bmobCallback);
    }
}
