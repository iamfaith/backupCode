package cn.bmob.v3.http.a;

import android.text.TextUtils;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.b.This;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.QueryListener;
import java.util.List;
import org.json.JSONObject;
import rx.Subscription;

/* compiled from: NECPolicyQuery */
public final class I extends From {
    public final Subscription Code(Class cls, List list, String str, JSONObject jSONObject, long j, BmobCallback bmobCallback) {
        final List list2 = list;
        final JSONObject jSONObject2 = jSONObject;
        final long j2 = j;
        final Class cls2 = cls;
        final BmobCallback bmobCallback2 = bmobCallback;
        return Code(Code(list, str, jSONObject, bmobCallback), new QueryListener<String>(this) {
            final /* synthetic */ I I;

            public final /* synthetic */ void done(Object obj, BmobException bmobException) {
                Code((String) obj, bmobException);
            }

            public final /* synthetic */ void done(Object obj, Object obj2) {
                Code((String) obj, (BmobException) obj2);
            }

            private void Code(String str, BmobException bmobException) {
                if (bmobException != null) {
                    This.V("NETWORK_ELSE_CACHE：e->" + bmobException.getMessage());
                    this.I.Code(this.I.Code(list2, jSONObject2, j2), new QueryListener<String>(this) {
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
                This.V("NETWORK_ELSE_CACHE：data->" + (TextUtils.isEmpty(str) ? " empty " : " has data "));
                this.I.Code(cls2, str, bmobCallback2);
            }
        });
    }

    public final boolean Code() {
        return true;
    }

    public final CachePolicy V() {
        return CachePolicy.NETWORK_ELSE_CACHE;
    }
}
