package cn.bmob.v3.http.a;

import android.text.TextUtils;
import android.util.Log;
import cn.bmob.v3.BmobConstants;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.b.darkness;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.helper.ErrorCode;
import cn.bmob.v3.helper.GsonUtil;
import cn.bmob.v3.http.acknowledge.This;
import cn.bmob.v3.http.bean.R1;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.BmobCallback2;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/* compiled from: PolicyQuery */
public abstract class From<T> {
    public abstract Subscription Code(Class<T> cls, List<R1> list, String str, JSONObject jSONObject, long j, BmobCallback bmobCallback);

    public abstract boolean Code();

    public abstract CachePolicy V();

    public final Subscription Code(Observable<String> observable, final QueryListener<String> queryListener) {
        return observable.subscribe(new Observer<String>(this) {
            private /* synthetic */ From V;

            public final /* synthetic */ void onNext(Object obj) {
                queryListener.done((String) obj, null);
            }

            public final void onCompleted() {
                queryListener.onFinish();
            }

            public final void onError(Throwable e) {
                From.Code(e, queryListener);
            }
        });
    }

    public final Observable<String> Code(List<R1> list, final JSONObject jSONObject, final long j) {
        return new This().Code(Code(), (List) list).V(new This().Code(new OnSubscribe<String>(this) {
            private /* synthetic */ From I;

            public final /* synthetic */ void call(Object obj) {
                Subscriber subscriber = (Subscriber) obj;
                if (subscriber.isUnsubscribed()) {
                    cn.bmob.v3.b.This.V("createCacheObservable:subscrible is cancel ");
                    return;
                }
                Object Code = darkness.Code(darkness.Code(jSONObject), j);
                cn.bmob.v3.b.This.V("cache data:" + (Code == null ? "no cache" : Code.toString()));
                subscriber.onNext(Code == null ? "" : Code.toString());
                if (this.I.V() == CachePolicy.CACHE_ONLY) {
                    subscriber.onCompleted();
                }
            }
        }).C().Code()).C().Code();
    }

    public final Observable<String> Code(List<R1> list, String str, final JSONObject jSONObject, final BmobCallback bmobCallback) {
        return new This().Code(Code(), (List) list).V(new This().V(str, jSONObject).Code(bmobCallback).Code(new Action1<String>(this) {
            final /* synthetic */ From I;

            public final /* synthetic */ void call(Object obj) {
                final String str = (String) obj;
                if (bmobCallback == null) {
                    Log.e(BmobConstants.TAG, BmobConstants.NULL_LISTENER);
                } else if (this.I.V() != CachePolicy.IGNORE_CACHE) {
                    final Worker createWorker = Schedulers.io().createWorker();
                    createWorker.schedule(new Action0(this) {
                        private /* synthetic */ AnonymousClass3 I;

                        public final void call() {
                            if (From.Code(str, bmobCallback)) {
                                cn.bmob.v3.b.This.V("缓存的data:" + str);
                                darkness.Code(darkness.Code(jSONObject), cn.bmob.v3.datatype.a.This.I(str));
                                return;
                            }
                            cn.bmob.v3.b.This.Code("no data need to be cache");
                            createWorker.unsubscribe();
                        }
                    });
                } else {
                    cn.bmob.v3.b.This.Code("IGNORE_CACHE：该策略下无需缓存数据");
                }
            }
        }).C().Code()).C().Code();
    }

    private static boolean Code(String str, BmobCallback bmobCallback) {
        String jSONArray;
        if (bmobCallback instanceof SQLQueryListener) {
            JSONArray jSONArray2 = null;
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("results")) {
                    jSONArray2 = jSONObject.getJSONArray("results");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jSONArray = jSONArray2 == null ? "" : jSONArray2.toString();
        } else {
            jSONArray = str;
        }
        List toList = GsonUtil.toList(jSONArray);
        if (toList == null || toList.size() <= 0) {
            return false;
        }
        return true;
    }

    public final void Code(Class<T> cls, String str, BmobCallback bmobCallback) {
        int i = 0;
        if (bmobCallback == null) {
            Log.e(BmobConstants.TAG, BmobConstants.NULL_LISTENER);
        } else if ((V() == CachePolicy.CACHE_ONLY || V() == CachePolicy.NETWORK_ELSE_CACHE) && TextUtils.isEmpty(str)) {
            Code(new BmobException((int) ErrorCode.E9009, "No cache data."), bmobCallback);
        } else {
            Gson create = new GsonBuilder().create();
            if (bmobCallback instanceof QueryListener) {
                cn.bmob.v3.b.This.V("getObject data：" + str);
                ((QueryListener) bmobCallback).done(create.fromJson(str, (Class) cls), null);
            } else if (bmobCallback instanceof FindListener) {
                cn.bmob.v3.b.This.V("query datas：" + str);
                List arrayList = new ArrayList();
                r0 = (List) create.fromJson(str, List.class);
                if (r0 != null) {
                    while (i < r0.size()) {
                        arrayList.add(create.fromJson(create.toJson(r0.get(i)), (Class) cls));
                        i++;
                    }
                }
                ((FindListener) bmobCallback).done(arrayList, null);
            } else if (bmobCallback instanceof SQLQueryListener) {
                List arrayList2 = new ArrayList();
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    if (jSONObject.has("results")) {
                        r0 = (List) create.fromJson(jSONObject.getJSONArray("results").toString(), List.class);
                        if (r0 != null) {
                            for (int i2 = 0; i2 < r0.size(); i2++) {
                                arrayList2.add(create.fromJson(create.toJson(r0.get(i2)), (Class) cls));
                            }
                        }
                    }
                    if (jSONObject.has("count")) {
                        i = jSONObject.getInt("count");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cn.bmob.v3.b.This.V("query sql datas：" + i + ",\n" + str);
                ((SQLQueryListener) bmobCallback).done(new BmobQueryResult(arrayList2, i), null);
            } else {
                Log.e(BmobConstants.TAG, " onNextListener does not support this listener");
            }
        }
    }

    public static void Code(Throwable th, BmobCallback bmobCallback) {
        if (bmobCallback == null || !(bmobCallback instanceof BmobCallback2)) {
            Log.e(BmobConstants.TAG, BmobConstants.NULL_LISTENER);
        } else if (th instanceof BmobException) {
            ((BmobCallback2) bmobCallback).done(null, th);
        } else {
            ((BmobCallback2) bmobCallback).done(null, new BmobException(th));
        }
    }
}
