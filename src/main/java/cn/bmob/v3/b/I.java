package cn.bmob.v3.b;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import cn.bmob.v3.BmobConstants;
import cn.bmob.v3.datatype.a.This;
import cn.bmob.v3.helper.GsonUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: CacheManager */
public class I {
    private static volatile I Code;

    private I(Context context) {
    }

    public static I Code(Context context) {
        if (Code == null) {
            synchronized (I.class) {
                if (Code == null) {
                    Code = new I(context);
                }
            }
        }
        return Code;
    }

    public final void Code(String str, String str2) {
        Object V = new The().V("user", "");
        if (!TextUtils.isEmpty(V)) {
            Map toMap = GsonUtil.toMap(V);
            Map toMap2 = GsonUtil.toMap(str);
            if (toMap2.size() > 0 && toMap.size() > 0) {
                for (Entry entry : toMap2.entrySet()) {
                    toMap.put(entry.getKey(), toMap2.get(entry.getKey()));
                }
            }
            str = GsonUtil.mapToJson(toMap);
        }
        new The().Code("user", V(str, str2));
    }

    private String V(String str, String str2) {
        String str3;
        JSONObject jSONObject = new JSONObject(str);
        JSONObject jSONObject2 = new JSONObject(str2);
        Iterator keys = jSONObject2.keys();
        while (keys.hasNext()) {
            String str4 = (String) keys.next();
            Object obj = jSONObject2.get(str4);
            if (str4.contains(".")) {
                try {
                    String[] split = str4.split("\\.");
                    if (split.length >= 2) {
                        String str5 = split[0];
                        JSONObject jSONObject3;
                        if (split.length == 2) {
                            str3 = split[1];
                            try {
                                JSONArray jSONArray;
                                int parseInt = Integer.parseInt(str3);
                                if (jSONObject.has(str5)) {
                                    jSONArray = jSONObject.getJSONArray(str5);
                                } else {
                                    jSONArray = new JSONArray();
                                }
                                jSONArray.put(parseInt, obj);
                                jSONObject.put(str5, jSONArray);
                            } catch (Exception e) {
                                if (jSONObject.has(str5)) {
                                    jSONObject3 = jSONObject.getJSONObject(str5);
                                } else {
                                    jSONObject3 = new JSONObject();
                                }
                                jSONObject3.put(str3, obj);
                                jSONObject.put(str5, jSONObject3);
                            }
                        } else if (split.length == 3) {
                            JSONArray jSONArray2;
                            if (jSONObject.has(str5)) {
                                jSONArray2 = jSONObject.getJSONArray(str5);
                            } else {
                                jSONArray2 = new JSONArray();
                            }
                            int parseInt2 = Integer.parseInt(split[1]);
                            String str6 = split[2];
                            jSONObject3 = jSONArray2.getJSONObject(parseInt2);
                            if (jSONObject3 == null) {
                                jSONObject3 = new JSONObject();
                            }
                            jSONObject3.put(str6, obj);
                            jSONArray2.put(parseInt2, jSONObject3);
                            jSONObject.put(str5, jSONArray2);
                        } else {
                            Log.e(BmobConstants.TAG, "key length (" + split.length + ") is not support.");
                        }
                    } else {
                        Log.e(BmobConstants.TAG, "key length less than 2.");
                    }
                } catch (Exception e2) {
                    try {
                        e2.printStackTrace();
                    } catch (Exception e22) {
                        e22.printStackTrace();
                    }
                }
            } else if (obj instanceof JSONObject) {
                JSONObject jSONObject4 = (JSONObject) obj;
                if (jSONObject4.has("__op")) {
                    str3 = jSONObject4.getString("__op");
                    if (str3.equals("Increment")) {
                        int i;
                        if (jSONObject4.has("amount")) {
                            i = jSONObject4.getInt("amount");
                        } else {
                            i = 0;
                        }
                        if (jSONObject.has(str4)) {
                            i += jSONObject.getInt(str4);
                        }
                        jSONObject.put(str4, i);
                    } else if (!str3.equals("Delete")) {
                        Code(str4, jSONObject4, jSONObject);
                    } else if (jSONObject.has(str4)) {
                        jSONObject.remove(str4);
                    } else {
                        Log.e(BmobConstants.TAG, "no key Delete");
                    }
                } else {
                    jSONObject.put(str4, jSONObject4);
                }
            } else if (obj instanceof JSONArray) {
                jSONObject.put(str4, (JSONArray) obj);
            } else {
                jSONObject.put(str4, obj);
            }
        }
        str = jSONObject.toString();
        return str;
    }

    private static void Code(String str, JSONObject jSONObject, JSONObject jSONObject2) throws JSONException {
        int i;
        int i2 = 0;
        JSONArray jSONArray = new JSONArray();
        if (jSONObject.has("objects")) {
            JSONArray jSONArray2 = jSONObject.getJSONArray("objects");
            if (jSONArray2 != null && jSONArray2.length() > 0) {
                int length = jSONArray2.length();
                for (i = 0; i < length; i++) {
                    jSONArray.put(jSONArray2.get(i));
                }
            }
        }
        String string = jSONObject.getString("__op");
        if (string.equals("Remove")) {
            if (jSONObject2.has(str)) {
                ArrayList Code = This.Code(jSONObject2.getJSONArray(str));
                ArrayList Code2 = This.Code(jSONArray);
                if (Code2.size() > 0) {
                    int size = Code2.size();
                    while (i2 < size) {
                        Code.remove(Code2.get(i2));
                        i2++;
                    }
                    jSONObject2.put(str, This.V(Code));
                    return;
                }
                Log.e(BmobConstants.TAG, "no data remove");
                return;
            }
            Log.e(BmobConstants.TAG, "no key remove");
        } else if (string.equals("AddUnique")) {
            if (jSONObject2.has(str)) {
                ArrayList Code3 = This.Code(jSONObject2.getJSONArray(str));
                Code3.addAll(This.Code(jSONArray));
                jSONObject2.put(str, This.V(This.Code(Code3)));
                return;
            }
            jSONObject2.put(str, jSONArray);
        } else if (!string.equals("Add")) {
            Log.e(BmobConstants.TAG, "其他类型：" + string);
        } else if (!jSONObject2.has(str)) {
            jSONObject2.put(str, jSONArray);
        } else if (jSONArray.length() > 0) {
            i = jSONArray.length();
            while (i2 < i) {
                jSONObject2.accumulate(str, jSONArray.get(i2));
                i2++;
            }
        } else {
            Log.e(BmobConstants.TAG, "no data add");
        }
    }
}
