package cn.bmob.v3.http;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.Log;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConstants;
import cn.bmob.v3.b.The;
import cn.bmob.v3.datatype.a.acknowledge;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.helper.BmobNative;
import cn.bmob.v3.helper.ErrorCode;
import cn.bmob.v3.helper.GsonUtil;
import cn.bmob.v3.helper.RequestHelper;
import cn.bmob.v3.http.b.of;
import cn.bmob.v3.http.b.thing;
import cn.bmob.v3.http.bean.Api;
import cn.bmob.v3.http.bean.Init;
import cn.bmob.v3.http.bean.Result;
import cn.bmob.v3.http.bean.Sk;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observable.Transformer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/* compiled from: BmobClient */
public final class This {
    private static byte[] B = new byte[0];
    public static final MediaType Code = MediaType.parse("application/json; charset=UTF-8");
    private static boolean I = false;
    private static final Transformer S = new Transformer() {
        public final Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
    };
    public static int V = 0;
    private static volatile This Z;
    private OkHttpClient C = new Builder().retryOnConnectionFailure(true).connectTimeout(Bmob.getConnectTimeout(), TimeUnit.SECONDS).readTimeout((long) acknowledge.V, TimeUnit.SECONDS).writeTimeout((long) acknowledge.I, TimeUnit.SECONDS).addInterceptor(new thing().Code(cn.bmob.v3.http.b.thing.This.Code)).addInterceptor(new cn.bmob.v3.http.b.This()).addNetworkInterceptor(new of()).build();

    static /* synthetic */ void Code(This thisR, Init init) {
        int i = 0;
        The the = new The();
        String[] strArr = new String[]{"api", "file", "io", "push", "upyunVer"};
        while (i < 5) {
            the.Code(strArr[i]);
            i++;
        }
        the.Code("api", init.getApi());
        the.Code("file", init.getFile());
        the.Code("push", init.getPush());
        the.Code("io", init.getIo());
        V = init.getUpyunVer();
        BmobNative.saveInterval(String.valueOf((int) ((System.currentTimeMillis() / 1000) - init.getTimestamp())));
    }

    private This() {
    }

    public static This Code() {
        if (Z == null) {
            synchronized (B) {
                if (Z == null) {
                    Z = new This();
                }
            }
        }
        return Z;
    }

    private synchronized Observable<Sk> I() {
        return Code(Sk.class, "http://open.bmob.cn/8/secret", null);
    }

    private synchronized Observable<Init> Z() {
        return Code(Init.class, "http://open.bmob.cn/8/init", null);
    }

    public final Observable<JsonElement> Code(final String str, final JSONObject jSONObject) {
        Observable<JsonElement> compose;
        synchronized (B) {
            if (BmobNative.hasKey() && I) {
                compose = I(str, jSONObject).compose(S);
            } else {
                compose = I().concatMap(new Func1<Sk, Observable<Init>>(this) {
                    private /* synthetic */ This Code;

                    {
                        this.Code = r1;
                    }

                    public final /* synthetic */ Object call(Object obj) {
                        BmobNative.saveKey(((Sk) obj).getSecretKey());
                        return this.Code.Z();
                    }
                }).concatMap(new Func1<Init, Observable<JsonElement>>(this) {
                    private /* synthetic */ This I;

                    public final /* synthetic */ Object call(Object obj) {
                        Init init = (Init) obj;
                        This.I = true;
                        This.Code(this.I, init);
                        return this.I.I(str, jSONObject);
                    }
                }).compose(S);
            }
        }
        return compose;
    }

    public static <T> Transformer<T, T> V() {
        return S;
    }

    private Observable<JsonElement> I(final String str, final JSONObject jSONObject) {
        return Observable.create(new OnSubscribe<JsonElement>(this) {
            private /* synthetic */ This I;

            public final /* synthetic */ void call(Object obj) {
                Subscriber subscriber = (Subscriber) obj;
                if (subscriber.isUnsubscribed()) {
                    Log.e(BmobConstants.TAG, "createObservable:subcriber is cancel");
                    return;
                }
                try {
                    Response execute = this.I.C.newCall(This.V(str, jSONObject)).execute();
                    int code = execute.code();
                    String string = execute.body().string();
                    cn.bmob.v3.b.This.V(code + ",content:" + string);
                    if (200 != code) {
                        subscriber.onError(new BmobException(code, string));
                    } else if (str.equals("http://open.bmob.cn/8/delcdnbatch")) {
                        subscriber.onNext(new JsonParser().parse(string));
                        subscriber.onCompleted();
                    } else {
                        Api api = (Api) GsonUtil.toObject(string, Api.class);
                        Result result = api.getResult();
                        if (result != null) {
                            int code2 = result.getCode();
                            if (200 != code2) {
                                subscriber.onError(new BmobException(code2, result.getMessage()));
                                return;
                            } else {
                                subscriber.onNext(api.getData());
                                return;
                            }
                        }
                        subscriber.onError(new BmobException((int) ErrorCode.E9002, string));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    private <T> Observable<T> Code(final Class<T> cls, final String str, JSONObject jSONObject) {
        return Observable.create(new OnSubscribe<T>(this, null) {
            private /* synthetic */ This Z;

            public final /* synthetic */ void call(Object obj) {
                Subscriber subscriber = (Subscriber) obj;
                if (subscriber.isUnsubscribed()) {
                    Log.e(BmobConstants.TAG, "createObservable:subcriber is cancel");
                    return;
                }
                try {
                    Response execute = this.Z.C.newCall(This.V(str, null)).execute();
                    int code = execute.code();
                    String string = execute.body().string();
                    cn.bmob.v3.b.This.V(code + ",content:" + string);
                    if (200 == code) {
                        Api api = (Api) GsonUtil.toObject(string, Api.class);
                        Result result = api.getResult();
                        if (result != null) {
                            int code2 = result.getCode();
                            if (200 != code2) {
                                subscriber.onError(new BmobException(code2, result.getMessage()));
                                return;
                            } else {
                                subscriber.onNext(new Gson().fromJson(api.getData().toString(), cls));
                                return;
                            }
                        }
                        subscriber.onError(new BmobException((int) ErrorCode.E9002, "解析Api出错,请不要混淆bmobsdk."));
                        return;
                    }
                    subscriber.onError(new BmobException(code, string));
                } catch (Throwable e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    public static Request V(String str, JSONObject jSONObject) {
        String Code;
        cn.bmob.v3.b.This.V("url：" + str);
        String userAgent = RequestHelper.getUserAgent(Bmob.getApplicationContext());
        Context applicationContext = Bmob.getApplicationContext();
        if (jSONObject == null) {
            jSONObject = new JSONObject();
        }
        JSONObject Code2 = Code(applicationContext, jSONObject, str);
        cn.bmob.v3.b.This.I(Code2.toString());
        if (str.equals("http://open.bmob.cn/8/secret")) {
            Code = I.Code(userAgent, Code2.toString());
        } else {
            Code = I.Code(Code2.toString());
        }
        Request.Builder post = new Request.Builder().header(HTTP.CONTENT_TYPE, "text/plain; charset=utf-8").header("Accept-Encoding", "gzip,deflate,sdch").header(HTTP.USER_AGENT, userAgent).url(str).post(RequestBody.create(Code, Code));
        if (!str.equals("http://open.bmob.cn/8/secret")) {
            post.addHeader("Accept-Id", I.Code());
        }
        return post.build();
    }

    private static JSONObject Code(Context context, JSONObject jSONObject, String str) {
        try {
            if (str.equals("http://open.bmob.cn/8/secret")) {
                jSONObject.put("appKey", I.V());
            }
            if (str.equals("http://open.bmob.cn/8/update") || str.equals("http://open.bmob.cn/8/delete") || str.equals("http://open.bmob.cn/8/find") || str.equals("http://open.bmob.cn/8/push") || str.endsWith("http://open.bmob.cn/8/update_user_password")) {
                jSONObject.put("sessionToken", new The().V("sessionToken", ""));
            }
            jSONObject.put("appSign", I.Code(context));
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("caller", "Android");
            JSONObject jSONObject3 = new JSONObject();
            jSONObject3.put(ClientCookie.VERSION_ATTR, VERSION.RELEASE);
            jSONObject3.put("package", context.getPackageName());
            jSONObject3.put("uuid", I.I(context));
            jSONObject2.put("ex", jSONObject3);
            jSONObject.put("client", jSONObject2);
            jSONObject.put("v", BmobConstants.VERSION_NAME);
            if (!(str.equals("http://open.bmob.cn/8/secret") || str.equals("http://open.bmob.cn/8/init"))) {
                jSONObject.put("timestamp", I.I());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    public static JSONObject Code(Context context, JSONObject jSONObject) {
        try {
            jSONObject.put("sessionToken", new The().V("sessionToken", ""));
            jSONObject.put("appSign", I.Code(context));
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("caller", "Android");
            JSONObject jSONObject3 = new JSONObject();
            jSONObject3.put(ClientCookie.VERSION_ATTR, VERSION.RELEASE);
            jSONObject3.put("package", context.getPackageName());
            jSONObject3.put("uuid", I.I(context));
            jSONObject2.put("ex", jSONObject3);
            jSONObject.put("client", jSONObject2);
            jSONObject.put("v", BmobConstants.VERSION_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }
}
