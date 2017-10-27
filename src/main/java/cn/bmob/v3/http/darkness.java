package cn.bmob.v3.http;

import android.text.TextUtils;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.a.This;
import cn.bmob.v3.helper.GsonUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: QueryConditions */
public final class darkness {
    private JSONObject B = new JSONObject();
    private String C;
    private int Code;
    private String D;
    private Integer F;
    private JSONObject I = new JSONObject();
    private String L;
    private Integer S;
    private JSONObject V = new JSONObject();
    private Class<?> Z;
    private String a;
    private String b;
    private String c;
    private String d;
    private String e;
    private String f;
    private boolean g = false;
    private JSONObject h = new JSONObject();
    private String i;
    private Object[] j = null;

    public final void Code(int i) {
        this.Code = i;
    }

    public final void Code(Class<?> cls) {
        this.Z = cls;
    }

    public final void Code(Integer num) {
        this.S = num;
    }

    public final void V(Integer num) {
        this.F = num;
    }

    public final void Code(String str) {
        this.D = str;
    }

    public final void V(String str) {
        this.L = str;
    }

    public final JSONObject Code() {
        return this.B;
    }

    public final void I(String str) {
        this.a = str;
    }

    public final JSONObject V() {
        return this.V;
    }

    public final void Code(String str, BmobGeoPoint bmobGeoPoint, double d) {
        Code(str, "$maxDistanceInMiles", bmobGeoPoint, d);
    }

    public final void V(String str, BmobGeoPoint bmobGeoPoint, double d) {
        Code(str, "$maxDistanceInKilometers", bmobGeoPoint, d);
    }

    public final void I(String str, BmobGeoPoint bmobGeoPoint, double d) {
        Code(str, "$maxDistanceInKilometers", bmobGeoPoint, d);
    }

    private void Code(String str, String str2, BmobGeoPoint bmobGeoPoint, double d) {
        Code(str, "$nearSphere", (Object) bmobGeoPoint);
        Code(str, str2, Double.valueOf(d));
    }

    public final void Z(String str) {
        this.C = str;
    }

    public final void Code(String str, String str2, Object obj) {
        try {
            BmobUser bmobUser;
            JSONObject jSONObject;
            BmobObject bmobObject;
            if (!TextUtils.isEmpty(str2)) {
                if (obj instanceof BmobGeoPoint) {
                    obj = new JSONObject(GsonUtil.toJson(obj));
                } else if (obj instanceof BmobUser) {
                    bmobUser = (BmobUser) obj;
                    jSONObject = new JSONObject();
                    jSONObject.put("__type", "Pointer");
                    jSONObject.put("objectId", bmobUser.getObjectId());
                    jSONObject.put("className", "_User");
                    r11 = jSONObject;
                } else if (obj instanceof BmobObject) {
                    bmobObject = (BmobObject) obj;
                    jSONObject = new JSONObject();
                    jSONObject.put("__type", "Pointer");
                    jSONObject.put("objectId", bmobObject.getObjectId());
                    jSONObject.put("className", bmobObject.getClass().getSimpleName());
                    r11 = jSONObject;
                } else if (obj instanceof BmobDate) {
                    r11 = new JSONObject(GsonUtil.toJson(obj));
                } else if (obj instanceof ArrayList) {
                    ArrayList arrayList = (ArrayList) obj;
                    JSONArray jSONArray = new JSONArray();
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("__type", "GeoPoint");
                    jSONObject2.put("longitude", ((BmobGeoPoint) arrayList.get(0)).getLongitude());
                    jSONObject2.put("latitude", ((BmobGeoPoint) arrayList.get(0)).getLatitude());
                    JSONObject jSONObject3 = new JSONObject();
                    jSONObject3.put("__type", "GeoPoint");
                    jSONObject3.put("longitude", ((BmobGeoPoint) arrayList.get(1)).getLongitude());
                    jSONObject3.put("latitude", ((BmobGeoPoint) arrayList.get(1)).getLatitude());
                    jSONArray.put(jSONObject2);
                    jSONArray.put(jSONObject3);
                    obj = new JSONObject();
                    obj.put("$box", jSONArray);
                }
                if (this.B.has(str)) {
                    Object obj2 = this.B.get(str);
                    if (obj2 instanceof JSONObject) {
                        jSONObject = (JSONObject) obj2;
                        if (jSONObject == null) {
                            jSONObject = new JSONObject();
                        }
                        jSONObject.put(str2, obj);
                        this.B.put(str, jSONObject);
                    }
                }
                jSONObject = null;
                if (jSONObject == null) {
                    jSONObject = new JSONObject();
                }
                jSONObject.put(str2, obj);
                this.B.put(str, jSONObject);
            } else if (obj instanceof BmobUser) {
                bmobUser = (BmobUser) obj;
                jSONObject = new JSONObject();
                jSONObject.put("__type", "Pointer");
                jSONObject.put("objectId", bmobUser.getObjectId());
                jSONObject.put("className", "_User");
                this.B.put(str, jSONObject);
            } else if (obj instanceof BmobObject) {
                bmobObject = (BmobObject) obj;
                jSONObject = new JSONObject();
                jSONObject.put("__type", "Pointer");
                jSONObject.put("objectId", bmobObject.getObjectId());
                jSONObject.put("className", bmobObject.getClass().getSimpleName());
                this.B.put(str, jSONObject);
            } else {
                this.B.put(str, obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public final void Code(boolean z) {
        this.g = z;
    }

    public final void Code(String[] strArr) {
        this.b = This.Code(strArr);
    }

    public final void V(String[] strArr) {
        this.c = This.Code(strArr);
    }

    public final void I(String[] strArr) {
        this.d = This.Code(strArr);
    }

    public final void Z(String[] strArr) {
        this.e = This.Code(strArr);
    }

    public final void B(String[] strArr) {
        this.f = This.Code(strArr);
    }

    public final void Code(HashMap<String, Object> hashMap) {
        if (hashMap != null && hashMap.size() > 0) {
            for (Entry entry : hashMap.entrySet()) {
                try {
                    this.h.put((String) entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                }
            }
        }
    }

    public final void B(String str) {
        this.i = str;
    }

    public final void Code(Object[] objArr) {
        this.j = objArr;
    }

    public final JSONObject I() {
        int i = 0;
        try {
            if (this.Code == 1 || this.Code == 2) {
                if (this.a != null) {
                    this.V.put("objectId", this.a);
                } else if (this.B.length() > 0) {
                    this.I.put("where", this.B);
                }
                if (!TextUtils.isEmpty(this.C)) {
                    this.I.put("keys", this.C);
                }
                this.I.put("limit", this.S);
                this.I.put("skip", this.F);
                if (!TextUtils.isEmpty(this.D)) {
                    this.I.put("order", this.D);
                }
                if (!TextUtils.isEmpty(this.L)) {
                    this.I.put("include", this.L);
                }
            } else if (this.Code == 3) {
                if (this.B.length() != 0) {
                    this.I.put("where", this.B);
                }
                this.I.put("limit", 0);
                this.I.put("count", true);
            } else if (this.Code == 4) {
                if (this.B.length() != 0) {
                    this.I.put("where", this.B);
                }
                this.I.put("keys", this.C);
                this.I.put("skip", this.F);
                this.I.put("limit", this.S);
                this.I.put("order", this.D);
                this.I.put("include", this.L);
                this.I.put("sum", this.c);
                this.I.put("max", this.e);
                this.I.put("min", this.f);
                this.I.put("average", this.d);
                this.I.put("groupby", this.b);
                if (this.h.length() != 0) {
                    this.I.put("having", this.h);
                }
                if (this.g) {
                    this.I.put("groupcount", true);
                }
            } else if (this.Code == 5) {
                if (!TextUtils.isEmpty(this.i)) {
                    this.I.put("bql", this.i);
                }
                if (this.j != null && this.j.length > 0) {
                    r1 = new JSONArray();
                    r2 = this.j;
                    r3 = r2.length;
                    while (i < r3) {
                        r1.put(r2[i]);
                        i++;
                    }
                    this.I.put("values", r1);
                }
            } else {
                if (this.a != null) {
                    this.V.put("objectId", this.a);
                } else if (this.B.length() > 0) {
                    this.I.put("where", this.B);
                }
                if (!TextUtils.isEmpty(this.C)) {
                    this.I.put("keys", this.C);
                }
                this.I.put("limit", this.S);
                this.I.put("skip", this.F);
                if (!TextUtils.isEmpty(this.D)) {
                    this.I.put("order", this.D);
                }
                if (!TextUtils.isEmpty(this.L)) {
                    this.I.put("include", this.L);
                }
                if (!TextUtils.isEmpty(this.i)) {
                    this.I.put("bql", this.i);
                }
                if (this.j != null && this.j.length > 0) {
                    r1 = new JSONArray();
                    r2 = this.j;
                    r3 = r2.length;
                    while (i < r3) {
                        r1.put(r2[i]);
                        i++;
                    }
                    this.I.put("values", r1);
                }
            }
            if (this.Z != null) {
                if (BmobUser.class.isAssignableFrom(this.Z)) {
                    this.V.put("c", "_User");
                } else if (BmobInstallation.class.isAssignableFrom(this.Z)) {
                    this.V.put("c", "_Installation");
                } else {
                    this.V.put("c", this.Z.getSimpleName());
                }
            }
            this.V.put("data", this.I);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.V;
    }
}
