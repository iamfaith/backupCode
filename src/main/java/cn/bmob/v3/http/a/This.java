package cn.bmob.v3.http.a;

import android.text.TextUtils;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.QueryListener;
import java.util.List;
import org.json.JSONObject;
import rx.Subscription;

/* compiled from: CENPolicyQuery */
public final class This extends From {
    public final Subscription Code(Class cls, List list, String str, JSONObject jSONObject, long j, BmobCallback bmobCallback) {
        final Class cls2 = cls;
        final BmobCallback bmobCallback2 = bmobCallback;
        final List list2 = list;
        final String str2 = str;
        final JSONObject jSONObject2 = jSONObject;
        return Code(Code(list, jSONObject, j), new QueryListener<String>(this) {
            final /* synthetic */ This I;

            public final /* synthetic */ void done(Object obj, BmobException bmobException) {
                Code((String) obj, bmobException);
            }

            public final /* synthetic */ void done(Object obj, Object obj2) {
                Code((String) obj, (BmobException) obj2);
            }

            private void Code(String str, BmobException bmobException) {
                if (TextUtils.isEmpty(str)) {
                    cn.bmob.v3.b.This.V("CACHE_ELSE_NETWORK：data-> empty ,e->" + (bmobException == null ? " null " : bmobException.getMessage()));
                    this.I.Code(this.I.Code(list2, str2, jSONObject2, bmobCallback2), new QueryListener<String>(this) {
                        private /* synthetic */ AnonymousClass1 Code;

                        {
                            this.Code = r1;
                        }

                        public final /* synthetic */ void done(Object obj, BmobException bmobException) {
                            Code((String) obj, bmobException);
                        }

                        public final /* synthetic */ void done(Object obj, Object obj2) {
                            Code((String) obj, (BmobException) obj2);
                        }

                        private void Code(String str, BmobException bmobException) {
                            if (bmobException == null) {
                                this.Code.I.Code(cls2, str, bmobCallback2);
                            } else {
                                From.Code((Throwable) bmobException, bmobCallback2);
                            }
                        }
                    });
                    return;
                }
                cn.bmob.v3.b.This.V("CACHE_ELSE_NETWORK：data-> has data ");
                this.I.Code(cls2, str, bmobCallback2);
            }
        });
    }

    public final boolean Code() {
        return true;
    }

    public final CachePolicy V() {
        return CachePolicy.CACHE_ELSE_NETWORK;
    }
}
