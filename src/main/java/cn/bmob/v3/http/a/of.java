package cn.bmob.v3.http.a;

import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.QueryListener;
import java.util.List;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/* compiled from: CTEPolicyQuery */
public final class of extends From {
    public final Subscription Code(final Class cls, List list, String str, JSONObject jSONObject, long j, final BmobCallback bmobCallback) {
        final List list2 = list;
        final String str2 = str;
        final JSONObject jSONObject2 = jSONObject;
        final BmobCallback bmobCallback2 = bmobCallback;
        return Code(Code(list, jSONObject, j).concatMap(new Func1<String, Observable<String>>(this) {
            private /* synthetic */ of B;

            public final /* synthetic */ Object call(Object obj) {
                return this.B.Code(list2, str2, jSONObject2, bmobCallback2);
            }
        }), new QueryListener<String>(this) {
            private /* synthetic */ of I;

            public final /* synthetic */ void done(Object obj, BmobException bmobException) {
                Code((String) obj, bmobException);
            }

            public final /* synthetic */ void done(Object obj, Object obj2) {
                Code((String) obj, (BmobException) obj2);
            }

            private void Code(String str, BmobException bmobException) {
                if (bmobException == null) {
                    this.I.Code(cls, str, bmobCallback);
                } else {
                    From.Code((Throwable) bmobException, bmobCallback);
                }
            }
        });
    }

    public final boolean Code() {
        return true;
    }

    public final CachePolicy V() {
        return CachePolicy.CACHE_THEN_NETWORK;
    }
}
