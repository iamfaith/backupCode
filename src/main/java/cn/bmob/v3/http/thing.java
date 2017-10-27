package cn.bmob.v3.http;

import android.text.TextUtils;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConstants;
import cn.bmob.v3.b.The;
import cn.bmob.v3.datatype.BmobReturn;
import cn.bmob.v3.datatype.BmobTableSchema;
import cn.bmob.v3.datatype.a.This;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.helper.ErrorCode;
import cn.bmob.v3.helper.GsonUtil;
import cn.bmob.v3.http.acknowledge.This.AnonymousClass13;
import cn.bmob.v3.http.bean.Api;
import cn.bmob.v3.http.bean.CDN;
import cn.bmob.v3.http.bean.R1;
import cn.bmob.v3.http.bean.Result;
import cn.bmob.v3.http.bean.Upyun;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.DeleteBatchListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/* compiled from: BmobFactory */
public final class thing {
    private static volatile thing Code;
    private static byte[] V = new byte[0];

    private thing() {
    }

    public static thing Code() {
        if (Code == null) {
            synchronized (V) {
                if (Code == null) {
                    Code = new thing();
                }
            }
        }
        return Code;
    }

    private static R1 I() {
        return new R1(Boolean.valueOf(Bmob.getApplicationContext() == null), new BmobException((int) ErrorCode.E9012, ErrorCode.E9012S));
    }

    private static R1 V(String str, String str2) {
        return new R1(Boolean.valueOf(TextUtils.isEmpty(str)), new BmobException((int) ErrorCode.E9018, str2));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static R1 V(Object obj, String str) {
        boolean z = true;
        boolean z2 = false;
        if (!(obj instanceof String)) {
            if (obj != null) {
                z = false;
            }
        }
        z2 = z;
        return new R1(Boolean.valueOf(z2), new BmobException((int) ErrorCode.E9018, str));
    }

    public static List<R1> Code(Object obj, String str) {
        return Arrays.asList(new R1[]{I(), V(obj, str)});
    }

    public static List<R1> Code(Object obj, String str, Object obj2, String str2) {
        return Arrays.asList(new R1[]{V(obj, str), V(obj2, str2)});
    }

    public static List<R1> Code(Object obj, String str, Object obj2, String str2, Object obj3, String str3) {
        return Arrays.asList(new R1[]{V(obj, str), V(obj2, str2), V(obj3, str3)});
    }

    public static List<R1> Code(String str, String str2) {
        return Arrays.asList(new R1[]{I(), V(str, str2)});
    }

    public static List<R1> Code(String str, String str2, String str3, String str4) {
        return Arrays.asList(new R1[]{V(str, str2), V(str3, str4)});
    }

    public static List<R1> Code(String str) {
        boolean z;
        R1 r1 = new R1(Boolean.valueOf(str.equals("_User")), new BmobException((int) ErrorCode.E9011, ErrorCode.E9011S));
        if (This.Code(str, 1, 49) || "_Installation".equals(str) || "_Role".equals(str)) {
            z = false;
        } else {
            z = true;
        }
        R1 r12 = new R1(Boolean.valueOf(z), new BmobException((int) ErrorCode.E9013, ErrorCode.E9013S));
        return Arrays.asList(new R1[]{r1, r12});
    }

    public static List<R1> Code(JSONArray jSONArray) {
        boolean z;
        if (jSONArray.length() == 0) {
            z = true;
        } else {
            z = false;
        }
        R1 r1 = new R1(Boolean.valueOf(z), new BmobException((int) ErrorCode.E9005, "A batch operation can not be less than 0"));
        if (jSONArray.length() > 50) {
            z = true;
        } else {
            z = false;
        }
        R1 r12 = new R1(Boolean.valueOf(z), new BmobException((int) ErrorCode.E9005, "A batch operation can not be more than 50"));
        return Arrays.asList(new R1[]{r1, r12});
    }

    public static acknowledge Code(String str, JSONObject jSONObject, BmobCallback bmobCallback) {
        return new acknowledge.This().Code(true, Collections.emptyList()).Code(str, jSONObject).Z().V(bmobCallback).C();
    }

    public static acknowledge Code(List<R1> list, String str, JSONObject jSONObject, BmobCallback bmobCallback) {
        return new acknowledge.This().Code(true, (List) list).Code(str, jSONObject).I().V(bmobCallback).C();
    }

    public static acknowledge V(List<R1> list, String str, JSONObject jSONObject, BmobCallback bmobCallback) {
        acknowledge.This Code = new acknowledge.This().Code(true, (List) list).Code(str, jSONObject);
        Code.Code = Code.Code.map(new Func1<JsonElement, Object>(Code) {
            public final /* synthetic */ Object call(Object obj) {
                return Integer.valueOf(((JsonElement) obj).getAsJsonObject().get("count").getAsInt());
            }
        });
        return Code.V(bmobCallback).C();
    }

    public static acknowledge I(List<R1> list, String str, JSONObject jSONObject, BmobCallback bmobCallback) {
        return new acknowledge.This().Code(true, (List) list).Code(str, jSONObject).Z().V(bmobCallback).C();
    }

    public final <T> acknowledge Code(Class<T> cls, List<R1> list, String str, JSONObject jSONObject, LogInListener<T> logInListener) {
        acknowledge.This Code = new acknowledge.This().Code(true, (List) list).Code(str, jSONObject).Code(new Action1<JsonElement>(this) {
            public final /* synthetic */ void call(Object obj) {
                JsonElement jsonElement = (JsonElement) obj;
                new The().Code("user", jsonElement.toString());
                new The().Code("sessionToken", jsonElement.getAsJsonObject().get("sessionToken").getAsString());
            }
        });
        Code.Code = Code.Code.map(new AnonymousClass13(Code, cls));
        return Code.V((BmobCallback) logInListener).C();
    }

    public final acknowledge Code(QueryListener<Long> queryListener) {
        return new acknowledge.This().Code(true, Collections.emptyList()).Code("http://open.bmob.cn/8/timestamp", null).Code(new Func1<JsonElement, Long>(this) {
            public final /* synthetic */ Object call(Object obj) {
                return Long.valueOf(((JsonElement) obj).getAsJsonObject().get("S").getAsLong());
            }
        }).V((BmobCallback) queryListener).C();
    }

    public final acknowledge Code(JSONObject jSONObject, QueryListListener<BmobTableSchema> queryListListener) {
        return new acknowledge.This().Code(true, Collections.emptyList()).Code("http://open.bmob.cn/8/schemas", jSONObject).Code(new Func1<JsonElement, List<BmobTableSchema>>(this) {
            public final /* synthetic */ Object call(Object obj) {
                return AnonymousClass3.Code((JsonElement) obj);
            }

            private static List<BmobTableSchema> Code(JsonElement jsonElement) {
                try {
                    List<BmobTableSchema> arrayList = new ArrayList();
                    JSONArray jSONArray = new JSONArray(jsonElement.getAsJsonObject().getAsJsonArray("results").toString());
                    if (jSONArray.length() <= 0) {
                        return arrayList;
                    }
                    for (int i = 0; i < jSONArray.length(); i++) {
                        JSONObject jSONObject = jSONArray.getJSONObject(i);
                        arrayList.add(new BmobTableSchema(jSONObject.getString("className"), This.Code(jSONObject.getJSONObject("fields"))));
                    }
                    return arrayList;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }).V((BmobCallback) queryListListener).C();
    }

    public final acknowledge Code(JSONObject jSONObject, QueryListener<BmobTableSchema> queryListener) {
        return new acknowledge.This().Code(true, Collections.emptyList()).Code("http://open.bmob.cn/8/schemas", jSONObject).Code(new Func1<JsonElement, BmobTableSchema>(this) {
            public final /* synthetic */ Object call(Object obj) {
                return AnonymousClass4.Code((JsonElement) obj);
            }

            private static BmobTableSchema Code(JsonElement jsonElement) {
                try {
                    JSONObject jSONObject = new JSONObject(jsonElement.getAsJsonObject().toString());
                    return new BmobTableSchema(jSONObject.getString("className"), This.Code(jSONObject.getJSONObject("fields")));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }).V((BmobCallback) queryListener).C();
    }

    public final acknowledge V() {
        acknowledge.This V = new acknowledge.This().V("http://open.bmob.cn/8/cdn", null);
        V.Code = V.Code.map(new Func1<JsonElement, Upyun>(V) {
            public final /* synthetic */ Object call(Object obj) {
                return AnonymousClass5.Code((JsonElement) obj);
            }

            private static Upyun Code(JsonElement jsonElement) {
                try {
                    return CDN.parse(jsonElement.getAsJsonObject().get("cdn").getAsJsonObject().toString()).getUpyun();
                } catch (Throwable e) {
                    e.printStackTrace();
                    throw Exceptions.propagate(new BmobException(e));
                }
            }
        });
        return V.Code(new Action1<Upyun>(this) {
            public final /* synthetic */ void call(Object obj) {
                final Upyun upyun = (Upyun) obj;
                Schedulers.io().createWorker().schedule(new Action0(this) {
                    public final void call() {
                        String toJson = GsonUtil.toJson(upyun);
                        The the = new The();
                        the.Code(BmobConstants.TYPE_CDN, toJson);
                        the.Code("upyunVer", This.V);
                    }
                });
            }
        }).C();
    }

    private static acknowledge Code(String str, String str2, long j, UpdateListener updateListener) {
        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("url", str);
            jSONObject2.put("filename", str2);
            jSONObject2.put("filesize", j);
            jSONObject2.put("cdn", BmobConstants.TYPE_CDN);
            jSONObject.put("data", jSONObject2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        acknowledge.This V = new acknowledge.This().V("http://open.bmob.cn/8/savecdnupload", jSONObject);
        V.Code = V.Code.map(new Func1<JsonElement, Void>(V) {
            public final /* synthetic */ Object call(Object obj) {
                String asString = ((JsonElement) obj).getAsJsonObject().get("msg").getAsString();
                if (asString != null && asString.equals("ok")) {
                    return null;
                }
                throw Exceptions.propagate(new BmobException((int) ErrorCode.E9020, asString));
            }
        });
        V.Code = V.Code.retryWhen(new cn.bmob.v3.http.c.This(2, BmobConstants.TIME_DELAY_RETRY));
        return V.V((BmobCallback) updateListener).C();
    }

    public final Observable<Void> Code(String str, String str2, long j) {
        final String str3 = str;
        final String str4 = str2;
        final long j2 = j;
        return new acknowledge.This().Code(new OnSubscribe<Void>(this) {
            private /* synthetic */ thing Z;

            public final /* synthetic */ void call(Object obj) {
                final Subscriber subscriber = (Subscriber) obj;
                thing.Code(str3, str4, j2, new UpdateListener(this) {
                    public final void done(BmobException e) {
                        if (e == null) {
                            cn.bmob.v3.b.This.V("saveCDN success");
                        } else {
                            cn.bmob.v3.b.This.Code(e.getMessage());
                        }
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                }).V;
            }
        }).C().Code.asObservable();
    }

    public static acknowledge Code(List<R1> list, String str, UpdateListener updateListener) {
        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("filename", This.V(str));
            jSONObject2.put("cdn", BmobConstants.TYPE_CDN);
            jSONObject.put("data", jSONObject2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        acknowledge.This Code = new acknowledge.This().Code(true, (List) list).Code("http://open.bmob.cn/8/delcdnupload", jSONObject);
        Code.Code = Code.Code.map(new Func1<JsonElement, Void>(Code) {
            public final /* synthetic */ Object call(Object obj) {
                String asString = ((JsonElement) obj).getAsJsonObject().get("msg").getAsString();
                if (asString != null && asString.equals("ok")) {
                    return null;
                }
                throw Exceptions.propagate(new BmobException((int) ErrorCode.E9015, asString));
            }
        });
        return Code.V((BmobCallback) updateListener).C();
    }

    public static acknowledge Code(String[] strArr, DeleteBatchListener deleteBatchListener) {
        JSONObject jSONObject = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        try {
            JSONArray jSONArray = new JSONArray();
            for (String V : strArr) {
                jSONArray.put(This.V(V));
            }
            jSONObject2.put(BmobConstants.TYPE_CDN, jSONArray);
            jSONObject.put("data", jSONObject2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        acknowledge.This thisR = new acknowledge.This();
        R1 r1 = new R1(Boolean.valueOf(strArr.length == 0), new BmobException((int) ErrorCode.E9005, "urls length can not be less than 0"));
        acknowledge.This Code = thisR.Code(true, Arrays.asList(new R1[]{V((Object) strArr, "urls must not be null"), r1})).Code("http://open.bmob.cn/8/delcdnbatch", jSONObject);
        Code.Code = Code.Code.map(new Func1<JsonElement, BmobReturn<String[]>>(Code) {
            public final /* synthetic */ Object call(Object obj) {
                JsonElement jsonElement = (JsonElement) obj;
                Api api = (Api) GsonUtil.toObject(jsonElement.toString(), Api.class);
                Result result = api.getResult();
                if (result == null) {
                    return new BmobReturn(null, new BmobException((int) ErrorCode.E9002, jsonElement.toString()));
                }
                int code = result.getCode();
                String message = result.getMessage();
                if (code == 200) {
                    return new BmobReturn(null, null);
                }
                JsonArray asJsonArray = api.getData().getAsJsonObject().get(BmobConstants.TYPE_CDN).getAsJsonArray();
                if (asJsonArray == null || asJsonArray.size() <= 0) {
                    return new BmobReturn(null, new BmobException(code, message));
                }
                int size = asJsonArray.size();
                Object obj2 = new String[asJsonArray.size()];
                for (int i = 0; i < size; i++) {
                    obj2[i] = asJsonArray.get(i).getAsString();
                }
                return new BmobReturn(obj2, new BmobException(code, message));
            }
        });
        return Code.V((BmobCallback) deleteBatchListener).C();
    }
}
