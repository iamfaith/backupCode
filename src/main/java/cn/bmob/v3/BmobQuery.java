package cn.bmob.v3;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.helper.GsonUtil;
import cn.bmob.v3.http.This;
import cn.bmob.v3.http.a.mine;
import cn.bmob.v3.http.acknowledge;
import cn.bmob.v3.http.darkness;
import cn.bmob.v3.http.thing;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class BmobQuery<T> {
    private darkness B;
    private Context Code;
    private long I;
    private CachePolicy V;
    private String Z;

    public enum CachePolicy {
        IGNORE_CACHE,
        CACHE_ONLY,
        NETWORK_ONLY,
        CACHE_ELSE_NETWORK,
        NETWORK_ELSE_CACHE,
        CACHE_THEN_NETWORK
    }

    public BmobQuery() {
        this(null);
    }

    public BmobQuery(String tableName) {
        if (!TextUtils.isEmpty(tableName)) {
            this.Z = tableName;
        }
        this.V = CachePolicy.IGNORE_CACHE;
        this.I = 18000000;
        this.B = new darkness();
        this.Code = Bmob.getApplicationContext();
    }

    public JSONObject getWhere() {
        return this.B.Code();
    }

    public BmobQuery<T> addWhereLessThan(String key, Object value) {
        this.B.Code(key, "$lt", value);
        return this;
    }

    public BmobQuery<T> addWhereLessThanOrEqualTo(String key, Object value) {
        this.B.Code(key, "$lte", value);
        return this;
    }

    public BmobQuery<T> addWhereGreaterThan(String key, Object value) {
        this.B.Code(key, "$gt", value);
        return this;
    }

    public BmobQuery<T> addWhereGreaterThanOrEqualTo(String key, Object value) {
        this.B.Code(key, "$gte", value);
        return this;
    }

    public BmobQuery<T> addWhereEqualTo(String key, Object value) {
        darkness cn_bmob_v3_http_darkness = this.B;
        if (value instanceof BmobPointer) {
            try {
                cn_bmob_v3_http_darkness.Code(key, null, new JSONObject(GsonUtil.toJson(value)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            cn_bmob_v3_http_darkness.Code(key, null, value);
        }
        return this;
    }

    public BmobQuery<T> addWhereNotEqualTo(String key, Object value) {
        this.B.Code(key, "$ne", value);
        return this;
    }

    public BmobQuery<T> addWhereContainsAll(String key, Collection<?> values) {
        darkness cn_bmob_v3_http_darkness = this.B;
        Object jSONArray = new JSONArray();
        for (Object put : values) {
            jSONArray.put(put);
        }
        cn_bmob_v3_http_darkness.Code(key, "$all", jSONArray);
        return this;
    }

    public BmobQuery<T> addWhereContainedIn(String key, Collection<? extends Object> values) {
        darkness cn_bmob_v3_http_darkness = this.B;
        Object jSONArray = new JSONArray();
        for (Object put : values) {
            jSONArray.put(put);
        }
        cn_bmob_v3_http_darkness.Code(key, "$in", jSONArray);
        return this;
    }

    public BmobQuery<T> addWhereNotContainedIn(String key, Collection<? extends Object> values) {
        darkness cn_bmob_v3_http_darkness = this.B;
        Object jSONArray = new JSONArray();
        for (Object put : values) {
            jSONArray.put(put);
        }
        cn_bmob_v3_http_darkness.Code(key, "$nin", jSONArray);
        return this;
    }

    public BmobQuery<T> addWhereMatches(String key, String regex) {
        this.B.Code(key, "$regex", (Object) regex);
        return this;
    }

    public BmobQuery<T> addWhereContains(String key, String value) {
        addWhereMatches(key, Pattern.quote(value));
        return this;
    }

    public BmobQuery<T> addWhereStartsWith(String key, String prefix) {
        addWhereMatches(key, "^" + Pattern.quote(prefix));
        return this;
    }

    public BmobQuery<T> addWhereEndsWith(String key, String suffix) {
        addWhereMatches(key, Pattern.quote(suffix) + "$");
        return this;
    }

    public BmobQuery<T> addWhereNear(String key, BmobGeoPoint point) {
        this.B.Code(key, "$nearSphere", (Object) point);
        return this;
    }

    public BmobQuery<T> addWhereWithinMiles(String key, BmobGeoPoint point, double maxDistance) {
        this.B.Code(key, point, maxDistance);
        return this;
    }

    public BmobQuery<T> addWhereWithinKilometers(String key, BmobGeoPoint point, double maxDistance) {
        this.B.V(key, point, maxDistance);
        return this;
    }

    public BmobQuery<T> addWhereWithinRadians(String key, BmobGeoPoint point, double maxDistance) {
        this.B.I(key, point, maxDistance);
        return this;
    }

    public BmobQuery<T> addWhereWithinGeoBox(String key, BmobGeoPoint southwest, BmobGeoPoint northeast) {
        darkness cn_bmob_v3_http_darkness = this.B;
        Object arrayList = new ArrayList();
        arrayList.add(southwest);
        arrayList.add(northeast);
        cn_bmob_v3_http_darkness.Code(key, "$within", arrayList);
        return this;
    }

    public BmobQuery<T> addWhereExists(String key) {
        this.B.Code(key, "$exists", Boolean.valueOf(true));
        return this;
    }

    public BmobQuery<T> addWhereDoesNotExists(String key) {
        this.B.Code(key, "$exists", Boolean.valueOf(false));
        return this;
    }

    public BmobQuery<T> addWhereRelatedTo(String key, BmobPointer pointer) {
        darkness cn_bmob_v3_http_darkness = this.B;
        Object jSONObject = new JSONObject();
        try {
            jSONObject.put("key", key);
            jSONObject.put("object", new JSONObject(GsonUtil.toJson(pointer)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cn_bmob_v3_http_darkness.Code("$relatedTo", null, jSONObject);
        return this;
    }

    public <E> BmobQuery<T> addWhereMatchesQuery(String key, String className, BmobQuery<E> innerQuery) {
        darkness cn_bmob_v3_http_darkness = this.B;
        try {
            Object jSONObject = new JSONObject();
            jSONObject.put("where", innerQuery.getWhere());
            jSONObject.put("className", className);
            cn_bmob_v3_http_darkness.Code(key, "$inQuery", jSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public <E> BmobQuery<T> addWhereDoesNotMatchQuery(String key, String className, BmobQuery<E> innerQuery) {
        darkness cn_bmob_v3_http_darkness = this.B;
        try {
            Object jSONObject = new JSONObject();
            jSONObject.put("where", innerQuery.getWhere());
            jSONObject.put("className", className);
            cn_bmob_v3_http_darkness.Code(key, "$notInQuery", jSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public BmobQuery<T> addQueryKeys(String keys) {
        this.B.Z(keys);
        return this;
    }

    public void setLimit(int newLimit) {
        this.B.Code(Integer.valueOf(newLimit));
    }

    public void setSkip(int newSkip) {
        this.B.V(Integer.valueOf(newSkip));
    }

    public BmobQuery<T> order(String order) {
        this.B.Code(order);
        return this;
    }

    public void include(String fieldName) {
        this.B.V(fieldName);
    }

    public BmobQuery<T> or(List<BmobQuery<T>> where) {
        darkness cn_bmob_v3_http_darkness = this.B;
        Object jSONArray = new JSONArray();
        for (BmobQuery where2 : where) {
            jSONArray.put(where2.getWhere());
        }
        cn_bmob_v3_http_darkness.Code("$or", null, jSONArray);
        return this;
    }

    public BmobQuery<T> and(List<BmobQuery<T>> where) {
        darkness cn_bmob_v3_http_darkness = this.B;
        Object jSONArray = new JSONArray();
        for (BmobQuery where2 : where) {
            jSONArray.put(where2.getWhere());
        }
        cn_bmob_v3_http_darkness.Code("$and", null, jSONArray);
        return this;
    }

    public void setCachePolicy(CachePolicy newCachePolicy) {
        this.V = newCachePolicy;
    }

    public CachePolicy getCachePolicy() {
        return this.V;
    }

    public void setMaxCacheAge(long maxAgeInMilliseconds) {
        this.I = maxAgeInMilliseconds;
    }

    public long getMaxCacheAge() {
        return this.I;
    }

    private JSONObject Code(Context context, Class<T> cls) {
        this.B.Code((Class) cls);
        this.B.I();
        return This.Code(context, this.B.V());
    }

    public Observable<Boolean> hasCachedResultObservable(final Class<T> clazz) {
        return new acknowledge.This().Code(new OnSubscribe<Boolean>(this) {
            private /* synthetic */ BmobQuery V;

            public final /* synthetic */ void call(Object obj) {
                ((Subscriber) obj).onNext(Boolean.valueOf(this.V.hasCachedResult(clazz)));
            }
        }).Code(Schedulers.io()).C().Code();
    }

    public boolean hasCachedResult(Class<T> clazz) {
        return cn.bmob.v3.b.darkness.V(cn.bmob.v3.b.darkness.Code(Code(this.Code, (Class) clazz)), this.I) != null;
    }

    public void clearCachedResult(Class<T> clazz) {
        cn.bmob.v3.b.darkness.Code(cn.bmob.v3.b.darkness.Code(Code(this.Code, (Class) clazz)));
    }

    public static void clearAllCachedResults() {
        cn.bmob.v3.b.darkness.Code();
    }

    private acknowledge Code(String str, QueryListener<JSONObject> queryListener) {
        this.B.Code(1);
        this.B.I(str);
        JSONObject I = this.B.I();
        try {
            I.put("c", this.Z);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new acknowledge.This().V("http://open.bmob.cn/8/find", I).Code().V((BmobCallback) queryListener).C();
    }

    private acknowledge Code(QueryListener<JSONArray> queryListener) {
        this.B.Code(1);
        JSONObject I = this.B.I();
        try {
            I.put("c", this.Z);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new acknowledge.This().V("http://open.bmob.cn/8/find", I).V().V((BmobCallback) queryListener).C();
    }

    public Subscription findObjectsByTable(QueryListener<JSONArray> listener) {
        if (listener != null) {
            return Code(listener).V();
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_LISTENER);
    }

    public Observable<JSONArray> findObjectsByTableObservable() {
        return Code(null).Code();
    }

    public Subscription getObjectByTable(String objectId, QueryListener<JSONObject> listener) {
        if (TextUtils.isEmpty(objectId)) {
            throw new IllegalArgumentException(" objectId must not be null");
        } else if (listener != null) {
            return Code(objectId, (QueryListener) listener).V();
        } else {
            throw new IllegalArgumentException(BmobConstants.ERROR_LISTENER);
        }
    }

    public Observable<JSONObject> getObjectByTableObservable(String objectId) {
        if (!TextUtils.isEmpty(objectId)) {
            return Code(objectId, null).Code();
        }
        throw new IllegalArgumentException(" objectId must not be null");
    }

    private Subscription Code(String str, Class<T> cls, BmobCallback bmobCallback) {
        this.B.Code(2);
        if (!TextUtils.isEmpty(str)) {
            this.B.I(str);
        }
        this.B.Code((Class) cls);
        return mine.Code().Code(str, cls, this.V, this.I, "http://open.bmob.cn/8/find", this.B.I(), bmobCallback);
    }

    public Subscription findObjects(FindListener<T> listener) {
        if (listener != null) {
            return Code(null, (Class) ((ParameterizedType) listener.getClass().getGenericSuperclass()).getActualTypeArguments()[0], (BmobCallback) listener);
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_LISTENER);
    }

    public Observable<List<T>> findObjectsObservable(final Class<T> clazz) {
        if (clazz != null) {
            return new acknowledge.This().Code(new OnSubscribe<List<T>>(this) {
                private /* synthetic */ BmobQuery V;

                public final /* synthetic */ void call(Object obj) {
                    final Subscriber subscriber = (Subscriber) obj;
                    if (subscriber.isUnsubscribed()) {
                        Log.e(BmobConstants.TAG, "findObjectsObservable: subscriber is unsubscribed ");
                    } else {
                        this.V.Code(null, clazz, new FindListener<T>(this) {
                            public final void done(List<T> t, BmobException e) {
                                if (e == null) {
                                    subscriber.onNext(t);
                                } else {
                                    subscriber.onError(e);
                                }
                                subscriber.onCompleted();
                            }
                        });
                    }
                }
            }).C().Code();
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_CLASS);
    }

    public Subscription getObject(String objectId, QueryListener<T> listener) {
        if (TextUtils.isEmpty(objectId)) {
            throw new IllegalArgumentException(" objectId must not be null");
        } else if (listener != null) {
            return Code(objectId, (Class) ((ParameterizedType) listener.getClass().getGenericSuperclass()).getActualTypeArguments()[0], (BmobCallback) listener);
        } else {
            throw new IllegalArgumentException(BmobConstants.ERROR_LISTENER);
        }
    }

    public Observable<T> getObjectObservable(final Class<T> clazz, final String objectId) {
        if (TextUtils.isEmpty(objectId)) {
            throw new IllegalArgumentException(" objectId must not be null");
        } else if (clazz != null) {
            return new acknowledge.This().Code(new OnSubscribe<T>(this) {
                private /* synthetic */ BmobQuery I;

                public final /* synthetic */ void call(Object obj) {
                    final Subscriber subscriber = (Subscriber) obj;
                    if (subscriber.isUnsubscribed()) {
                        Log.e(BmobConstants.TAG, "getObjectObservable: subscriber is unsubscribed ");
                    } else {
                        this.I.Code(objectId, clazz, new QueryListener<T>(this) {
                            public final void done(T t, BmobException e) {
                                if (e == null) {
                                    subscriber.onNext(t);
                                } else {
                                    subscriber.onError(e);
                                }
                                subscriber.onCompleted();
                            }
                        });
                    }
                }
            }).C().Code();
        } else {
            throw new IllegalArgumentException(BmobConstants.ERROR_CLASS);
        }
    }

    private acknowledge Code(Class<T> cls, CountListener countListener) {
        if (cls == null) {
            throw new IllegalArgumentException(BmobConstants.ERROR_CLASS);
        }
        this.B.Code(3);
        this.B.Code((Class) cls);
        JSONObject I = this.B.I();
        thing.Code();
        return thing.V(thing.Code("no check", " no check"), "http://open.bmob.cn/8/find", I, countListener);
    }

    public Observable<Integer> countObservable(Class<T> clazz) {
        return Code((Class) clazz, null).Code();
    }

    public Subscription count(Class<T> clazz, CountListener listener) {
        return Code((Class) clazz, listener).V();
    }

    public BmobQuery<T> setHasGroupCount(boolean hasCount) {
        this.B.Code(hasCount);
        return this;
    }

    public BmobQuery<T> groupby(String[] groupKeys) {
        this.B.Code(groupKeys);
        return this;
    }

    public BmobQuery<T> sum(String[] sumKeys) {
        this.B.V(sumKeys);
        return this;
    }

    public BmobQuery<T> average(String[] averageKeys) {
        this.B.I(averageKeys);
        return this;
    }

    public BmobQuery<T> max(String[] maxKeys) {
        this.B.Z(maxKeys);
        return this;
    }

    public BmobQuery<T> min(String[] minKeys) {
        this.B.B(minKeys);
        return this;
    }

    public BmobQuery<T> having(HashMap<String, Object> map) {
        this.B.Code((HashMap) map);
        return this;
    }

    public Subscription findStatistics(Class<?> clazz, QueryListener<JSONArray> listener) {
        if (clazz != null) {
            return Code((Class) clazz, (QueryListener) listener).V();
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_CLASS);
    }

    public Observable<JSONArray> findStatisticsObservable(Class<?> clazz) {
        if (clazz != null) {
            return Code((Class) clazz, null).Code();
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_CLASS);
    }

    private acknowledge Code(Class<?> cls, QueryListener<JSONArray> queryListener) {
        this.B.Code(4);
        this.B.Code((Class) cls);
        JSONObject I = this.B.I();
        thing.Code();
        return thing.I(thing.Code("no check", " no check"), "http://open.bmob.cn/8/find", I, queryListener);
    }

    public BmobQuery<T> setSQL(String bql) {
        this.B.B(bql);
        return this;
    }

    public BmobQuery<T> setPreparedParams(Object[] values) {
        this.B.Code(values);
        return this;
    }

    private Subscription Code(Class<T> cls, SQLQueryListener<T> sQLQueryListener) {
        this.B.Code(5);
        return mine.Code().Code(null, cls, this.V, this.I, "http://open.bmob.cn/8/cloud_query", this.B.I(), sQLQueryListener);
    }

    public Subscription doSQLQuery(SQLQueryListener<T> listener) {
        if (listener != null) {
            return Code((Class) ((ParameterizedType) listener.getClass().getGenericSuperclass()).getActualTypeArguments()[0], (SQLQueryListener) listener);
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_LISTENER);
    }

    public Observable<BmobQueryResult<T>> doSQLQueryObservable(final Class<T> clazz) {
        if (clazz != null) {
            return new acknowledge.This().Code(new OnSubscribe<BmobQueryResult<T>>(this) {
                private /* synthetic */ BmobQuery V;

                public final /* synthetic */ void call(Object obj) {
                    final Subscriber subscriber = (Subscriber) obj;
                    if (subscriber.isUnsubscribed()) {
                        Log.e(BmobConstants.TAG, "doSQLQueryObservable: subscriber is unsubscribed ");
                    } else {
                        this.V.Code(clazz, new SQLQueryListener<T>(this) {
                            public final void done(BmobQueryResult<T> t, BmobException e) {
                                if (e == null) {
                                    subscriber.onNext(t);
                                } else {
                                    subscriber.onError(e);
                                }
                                subscriber.onCompleted();
                            }
                        });
                    }
                }
            }).C().Code();
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_CLASS);
    }

    public Subscription doSQLQuery(String bql, SQLQueryListener<T> listener) {
        return doSQLQuery(bql, listener, new Object[0]);
    }

    public Subscription doSQLQuery(String bql, SQLQueryListener<T> listener, Object... params) {
        if (listener != null) {
            return Code((Class) ((ParameterizedType) listener.getClass().getGenericSuperclass()).getActualTypeArguments()[0], bql, (BmobCallback) listener, params);
        }
        throw new IllegalArgumentException(BmobConstants.ERROR_LISTENER);
    }

    public Subscription doStatisticQuery(String bql, QueryListener<JSONArray> listener) {
        return Code(null, bql, (BmobCallback) listener, new Object[0]);
    }

    public Subscription doStatisticQuery(String bql, QueryListener<JSONArray> listener, Object... params) {
        return Code(null, bql, (BmobCallback) listener, params);
    }

    private Subscription Code(Class<T> cls, String str, BmobCallback bmobCallback, Object... objArr) {
        this.B.Code(5);
        this.B.B(str);
        if (objArr != null && objArr.length > 0) {
            Object[] objArr2 = new Object[objArr.length];
            for (int i = 0; i < objArr.length; i++) {
                objArr2[i] = objArr[i];
            }
            this.B.Code(objArr2);
        }
        JSONObject I = this.B.I();
        if (bmobCallback instanceof QueryListener) {
            thing.Code();
            return thing.Code("http://open.bmob.cn/8/cloud_query", I, bmobCallback).V();
        } else if (bmobCallback instanceof SQLQueryListener) {
            return mine.Code().Code(null, cls, this.V, this.I, "http://open.bmob.cn/8/cloud_query", I, bmobCallback);
        } else {
            throw new IllegalArgumentException("doBql does not support this BmobCallback");
        }
    }
}
