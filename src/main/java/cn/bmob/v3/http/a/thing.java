package cn.bmob.v3.http.a;

import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.QueryListener;
import java.util.List;
import org.json.JSONObject;
import rx.Subscription;

/* compiled from: COPolicyQuery */
public final class thing extends From {
    public final Subscription Code(final Class cls, List list, String str, JSONObject jSONObject, long j, final BmobCallback bmobCallback) {
        return Code(Code(list, jSONObject, j), new QueryListener<String>(this) {
            private /* synthetic */ thing I;

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
        return false;
    }

    public final CachePolicy V() {
        return CachePolicy.CACHE_ONLY;
    }
}
