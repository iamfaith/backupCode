package cn.bmob.v3.http;

import android.text.TextUtils;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.b.From;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.helper.ErrorCode;
import cn.bmob.v3.helper.GsonUtil;
import cn.bmob.v3.http.bean.R1;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.BmobCallback1;
import cn.bmob.v3.listener.BmobCallback2;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UploadFileListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func6;
import rx.functions.Func7;

/* compiled from: RxBmob */
public final class acknowledge {
    final Observable Code;
    final Subscription V;

    /* compiled from: RxBmob */
    public static final class This {
        Observable Code = Observable.empty();
        Subscription V = this.Code.subscribe();

        /* compiled from: RxBmob */
        class AnonymousClass13 implements Func1<JsonElement, T> {
            private /* synthetic */ Class Code;

            AnonymousClass13(This thisR, Class cls) {
                this.Code = cls;
            }

            public final /* synthetic */ Object call(Object obj) {
                return GsonUtil.toObject((JsonElement) obj, this.Code);
            }
        }

        public final This Code(boolean z, List<R1> list) {
            if (list == null) {
                throw new IllegalArgumentException(" R1 list is null ");
            }
            Observable create = Observable.create(new OnSubscribe<String>(this) {
                public final /* synthetic */ void call(Object obj) {
                    Subscriber subscriber = (Subscriber) obj;
                    if (Bmob.getApplicationContext() == null) {
                        subscriber.onError(new BmobException((int) ErrorCode.E9012, ErrorCode.E9012S));
                    } else {
                        subscriber.onNext("");
                    }
                }
            });
            Observable create2 = Observable.create(new OnSubscribe<String>(this) {
                public final /* synthetic */ void call(Object obj) {
                    Subscriber subscriber = (Subscriber) obj;
                    if (TextUtils.isEmpty(I.V())) {
                        subscriber.onError(new BmobException((int) ErrorCode.E9001, ErrorCode.E9001S));
                    } else {
                        subscriber.onNext("");
                    }
                }
            });
            Observable create3 = Observable.create(new OnSubscribe<String>(this) {
                public final /* synthetic */ void call(Object obj) {
                    Subscriber subscriber = (Subscriber) obj;
                    if (!subscriber.isUnsubscribed()) {
                        if (From.V(Bmob.getApplicationContext())) {
                            subscriber.onNext("");
                        } else {
                            subscriber.onError(new BmobException((int) ErrorCode.E9016, ErrorCode.E9016S));
                        }
                    }
                }
            });
            final List<R1> list2 = list;
            Observable create4 = Observable.create(new OnSubscribe<String>(this) {
                public final /* synthetic */ void call(Object obj) {
                    Subscriber subscriber = (Subscriber) obj;
                    if (list2.size() <= 0 || !((R1) list2.get(0)).getB().booleanValue()) {
                        subscriber.onNext("");
                    } else {
                        subscriber.onError(((R1) list2.get(0)).getE());
                    }
                }
            });
            list2 = list;
            Observable create5 = Observable.create(new OnSubscribe<String>(this) {
                public final /* synthetic */ void call(Object obj) {
                    Subscriber subscriber = (Subscriber) obj;
                    if (list2.size() <= 1 || !((R1) list2.get(1)).getB().booleanValue()) {
                        subscriber.onNext("");
                    } else {
                        subscriber.onError(((R1) list2.get(1)).getE());
                    }
                }
            });
            list2 = list;
            Observable create6 = Observable.create(new OnSubscribe<String>(this) {
                public final /* synthetic */ void call(Object obj) {
                    Subscriber subscriber = (Subscriber) obj;
                    if (list2.size() <= 2 || !((R1) list2.get(2)).getB().booleanValue()) {
                        subscriber.onNext("");
                    } else {
                        subscriber.onError(((R1) list2.get(2)).getE());
                    }
                }
            });
            list2 = list;
            Observable create7 = Observable.create(new OnSubscribe<String>(this) {
                public final /* synthetic */ void call(Object obj) {
                    Subscriber subscriber = (Subscriber) obj;
                    if (list2.size() <= 3 || !((R1) list2.get(3)).getB().booleanValue()) {
                        subscriber.onNext("");
                    } else {
                        subscriber.onError(((R1) list2.get(3)).getE());
                    }
                }
            });
            if (z) {
                this.Code = Observable.combineLatest(create, create2, create3, create4, create5, create6, create7, new Func7<String, String, String, String, String, String, String, Boolean>(this) {
                    public final /* synthetic */ Object call(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7) {
                        boolean z;
                        String str = (String) obj2;
                        String str2 = (String) obj3;
                        String str3 = (String) obj4;
                        String str4 = (String) obj5;
                        String str5 = (String) obj6;
                        String str6 = (String) obj7;
                        if (TextUtils.isEmpty((String) obj) && TextUtils.isEmpty(str) && TextUtils.isEmpty(str2) && TextUtils.isEmpty(str3) && TextUtils.isEmpty(str4) && TextUtils.isEmpty(str5) && TextUtils.isEmpty(str6)) {
                            z = true;
                        } else {
                            z = false;
                        }
                        return Boolean.valueOf(z);
                    }
                });
            } else {
                this.Code = Observable.combineLatest(create, create2, create4, create5, create6, create7, new Func6<String, String, String, String, String, String, Boolean>(this) {
                    public final /* synthetic */ Object call(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
                        boolean z;
                        String str = (String) obj2;
                        String str2 = (String) obj3;
                        String str3 = (String) obj4;
                        String str4 = (String) obj5;
                        String str5 = (String) obj6;
                        if (TextUtils.isEmpty((String) obj) && TextUtils.isEmpty(str) && TextUtils.isEmpty(str2) && TextUtils.isEmpty(str3) && TextUtils.isEmpty(str4) && TextUtils.isEmpty(str5)) {
                            z = true;
                        } else {
                            z = false;
                        }
                        return Boolean.valueOf(z);
                    }
                });
            }
            return this;
        }

        public final This Code(List<R1> list) {
            return Code(true, (List) list);
        }

        public final This Code(OnSubscribe onSubscribe) {
            this.Code = Observable.create(onSubscribe);
            return this;
        }

        public final This Code(Observable observable) {
            this.Code = observable;
            return this;
        }

        public final This Code(String str, JSONObject jSONObject) {
            return V(This.Code().Code(str, jSONObject));
        }

        public final <T> This V(final Observable<T> observable) {
            this.Code = this.Code.concatMap(new Func1<Boolean, Observable<T>>(this) {
                public final /* bridge */ /* synthetic */ Object call(Object obj) {
                    return observable;
                }
            });
            return this;
        }

        public final This V(String str, JSONObject jSONObject) {
            this.Code = This.Code().Code(str, jSONObject);
            return this;
        }

        public final <T> This Code(Action1<T> action1) {
            this.Code = this.Code.doOnNext(action1);
            return this;
        }

        public final <T, R> This Code(Func1<T, R> func1) {
            this.Code = this.Code.map(func1);
            return this;
        }

        public final This Code() {
            this.Code = this.Code.map(new Func1<JsonElement, JSONObject>(this) {
                public final /* synthetic */ Object call(Object obj) {
                    return AnonymousClass3.Code((JsonElement) obj);
                }

                private static JSONObject Code(JsonElement jsonElement) {
                    try {
                        return new JSONObject(jsonElement.toString());
                    } catch (JSONException e) {
                        try {
                            e.printStackTrace();
                            return null;
                        } catch (Throwable e2) {
                            e2.printStackTrace();
                            throw Exceptions.propagate(new BmobException(e2));
                        }
                    }
                }
            });
            return this;
        }

        public final This V() {
            this.Code = this.Code.map(new Func1<JsonElement, JSONArray>(this) {
                public final /* synthetic */ Object call(Object obj) {
                    return AnonymousClass4.Code((JsonElement) obj);
                }

                private static JSONArray Code(JsonElement jsonElement) {
                    try {
                        return new JSONArray(jsonElement.getAsJsonObject().get("results").getAsJsonArray().toString());
                    } catch (JSONException e) {
                        try {
                            e.printStackTrace();
                            return null;
                        } catch (Throwable e2) {
                            e2.printStackTrace();
                            throw Exceptions.propagate(new BmobException(e2));
                        }
                    }
                }
            });
            return this;
        }

        public final This I() {
            this.Code = this.Code.map(new Func1<JsonElement, Void>(this) {
                public final /* bridge */ /* synthetic */ Object call(Object obj) {
                    return null;
                }
            });
            return this;
        }

        public final This Z() {
            this.Code = this.Code.map(new Func1<JsonElement, JSONArray>(this) {
                public final /* synthetic */ Object call(Object obj) {
                    return AnonymousClass11.Code((JsonElement) obj);
                }

                private static JSONArray Code(JsonElement jsonElement) {
                    try {
                        JsonArray asJsonArray = jsonElement.getAsJsonObject().get("results").getAsJsonArray();
                        if (asJsonArray == null || asJsonArray.size() <= 0) {
                            return null;
                        }
                        return new JSONArray(asJsonArray.toString());
                    } catch (JSONException e) {
                        return null;
                    }
                }
            });
            return this;
        }

        public final This B() {
            this.Code = this.Code.map(new Func1<JsonElement, List<BatchResult>>(this) {
                public final /* synthetic */ Object call(Object obj) {
                    return AnonymousClass14.Code((JsonElement) obj);
                }

                private static List<BatchResult> Code(JsonElement jsonElement) {
                    List<BatchResult> arrayList = new ArrayList();
                    try {
                        arrayList.clear();
                        JSONArray jSONArray = new JSONArray(jsonElement.toString());
                        int length = jSONArray.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject jSONObject = jSONArray.getJSONObject(i);
                            BatchResult batchResult = new BatchResult();
                            if (jSONObject.has("success")) {
                                batchResult.setSuccess(true);
                                jSONObject = jSONObject.getJSONObject("success");
                                if (jSONObject.has("createdAt")) {
                                    batchResult.setCreatedAt(jSONObject.getString("createdAt"));
                                }
                                if (jSONObject.has("objectId")) {
                                    batchResult.setObjectId(jSONObject.getString("objectId"));
                                }
                                if (jSONObject.has("updatedAt")) {
                                    batchResult.setUpdatedAt(jSONObject.getString("updatedAt"));
                                }
                            } else if (jSONObject.has("error")) {
                                batchResult.setSuccess(false);
                                JSONObject jSONObject2 = jSONObject.getJSONObject("error");
                                batchResult.setError(new BmobException(jSONObject2.has("code") ? jSONObject2.getInt("code") : ErrorCode.E9015, jSONObject2.has("error") ? jSONObject2.getString("error") : ""));
                            } else {
                                batchResult.setSuccess(false);
                                batchResult.setError(new BmobException((int) ErrorCode.E9015, "服务端返回异常"));
                            }
                            arrayList.add(batchResult);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return arrayList;
                }
            });
            return this;
        }

        public final This Code(final BmobCallback bmobCallback) {
            this.Code = this.Code.map(new Func1<JsonElement, String>(this) {
                public final /* synthetic */ Object call(Object obj) {
                    JsonElement jsonElement = (JsonElement) obj;
                    if ((bmobCallback instanceof QueryListener) || (bmobCallback instanceof SQLQueryListener)) {
                        return jsonElement.toString();
                    }
                    if (bmobCallback instanceof FindListener) {
                        return jsonElement.getAsJsonObject().getAsJsonArray("results").toString();
                    }
                    throw Exceptions.propagate(new BmobException(" mapPolicyQuery does not support this BmobCallback"));
                }
            });
            return this;
        }

        public final This Code(Scheduler scheduler) {
            this.Code = this.Code.subscribeOn(scheduler);
            return this;
        }

        public final This V(final BmobCallback bmobCallback) {
            if (bmobCallback == null) {
                cn.bmob.v3.b.This.Code("listener is null,just create observable.");
            } else {
                this.V = this.Code.subscribe(new Subscriber(this) {
                    public final void onCompleted() {
                        bmobCallback.onFinish();
                    }

                    public final void onError(Throwable e) {
                        if (bmobCallback instanceof BmobCallback2) {
                            if (e instanceof BmobException) {
                                ((BmobCallback2) bmobCallback).done(null, e);
                            } else {
                                ((BmobCallback2) bmobCallback).done(null, new BmobException((int) ErrorCode.E9015, e));
                            }
                        } else if (!(bmobCallback instanceof BmobCallback1) && !(bmobCallback instanceof UploadFileListener)) {
                            cn.bmob.v3.b.This.Code("not support this callback");
                        } else if (e instanceof BmobException) {
                            ((BmobCallback1) bmobCallback).done(e);
                        } else {
                            ((BmobCallback1) bmobCallback).done(new BmobException((int) ErrorCode.E9015, e));
                        }
                    }

                    public final void onNext(Object t) {
                        if (bmobCallback instanceof BmobCallback2) {
                            ((BmobCallback2) bmobCallback).done(t, null);
                        } else if (bmobCallback instanceof BmobCallback1) {
                            ((BmobCallback1) bmobCallback).done(t);
                        } else if (bmobCallback instanceof UploadFileListener) {
                            ((UploadFileListener) bmobCallback).done(null);
                        } else {
                            cn.bmob.v3.b.This.Code("not support this callback");
                        }
                    }
                });
            }
            return this;
        }

        public final acknowledge C() {
            return new acknowledge(this);
        }
    }

    public acknowledge() {
        this(new This());
    }

    public acknowledge(This thisR) {
        this.Code = thisR.Code.compose(This.V());
        this.V = thisR.V;
    }

    public final Observable Code() {
        return this.Code.asObservable();
    }

    public final Subscription V() {
        return this.V;
    }
}
