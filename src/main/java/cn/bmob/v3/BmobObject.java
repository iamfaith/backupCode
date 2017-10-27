package cn.bmob.v3;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.helper.GsonUtil;
import cn.bmob.v3.http.acknowledge;
import cn.bmob.v3.http.acknowledge.This;
import cn.bmob.v3.http.thing;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import com.google.gson.JsonElement;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

public class BmobObject implements Serializable {
    protected static JSONObject data;
    protected static List<JSONObject> increments = new ArrayList();
    private BmobACL ACL;
    private String _c_;
    private String createdAt;
    private String objectId;
    private String updatedAt;

    public BmobObject() {
        this._c_ = "";
        this._c_ = getClass().getSimpleName();
        data = new JSONObject();
    }

    public BmobObject(String tableName) {
        this._c_ = "";
        this._c_ = tableName;
        data = new JSONObject();
    }

    public void setTableName(String tableName) {
        this._c_ = tableName;
    }

    public String getTableName() {
        return this._c_;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    protected void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return this.updatedAt;
    }

    protected void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BmobACL getACL() {
        return this.ACL;
    }

    public void setACL(BmobACL aCL) {
        this.ACL = aCL;
    }

    public void increment(String key) {
        increment(key, Integer.valueOf(1));
    }

    public void increment(String key, Number amount) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("__op", "Increment");
            jSONObject.put("amount", amount);
            jSONObject.put("key", key);
            increments.add(jSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void add(String key, Object value) {
        addAll(key, Arrays.asList(new Object[]{value}));
    }

    public void addAll(String key, Collection<?> values) {
        try {
            data.put(key, Code("Add", (Collection) values));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addUnique(String key, Object value) {
        addAllUnique(key, Arrays.asList(new Object[]{value}));
    }

    public void addAllUnique(String key, Collection<?> values) {
        try {
            data.put(key, Code("AddUnique", (Collection) values));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeAll(String key, Collection<?> values) {
        try {
            data.put(key, Code("Remove", (Collection) values));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void remove(String key) {
        try {
            data.put(key, new JSONObject().put("__op", "Delete"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject Code(String str, Collection<?> collection) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("__op", str);
        JSONArray jSONArray = new JSONArray();
        for (Object next : collection) {
            if (next instanceof String) {
                jSONArray.put(next);
            } else {
                jSONArray.put(new JSONObject(GsonUtil.toJson(next)));
            }
        }
        jSONObject.put("objects", jSONArray);
        return jSONObject;
    }

    public void setValue(String key, Object value) {
        try {
            JSONObject jSONObject = data;
            if ((value instanceof BmobDate) || (value instanceof BmobFile) || (value instanceof BmobGeoPoint) || (value instanceof BmobRelation)) {
                value = new JSONObject(GsonUtil.toJson(value));
            } else if (value instanceof BmobObject) {
                BmobObject bmobObject = (BmobObject) value;
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("__type", "Pointer");
                jSONObject2.put("objectId", bmobObject.getObjectId() == null ? "" : bmobObject.getObjectId());
                jSONObject2.put("className", bmobObject.getTableName());
                value = jSONObject2;
            } else if (!((value instanceof String) || (value instanceof Byte) || (value instanceof Short) || (value instanceof Integer) || (value instanceof Long) || (value instanceof Double) || (value instanceof Float) || (value instanceof Character) || (value instanceof Boolean))) {
                if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    JSONArray jSONArray = new JSONArray();
                    for (Object next : collection) {
                        if (next instanceof String) {
                            jSONArray.put(next);
                        } else {
                            jSONArray.put(new JSONObject(GsonUtil.toJson(next)));
                        }
                    }
                    JSONArray value2 = jSONArray;
                } else {
                    value = new JSONObject(GsonUtil.toJson(value));
                }
            }
            jSONObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected JSONObject getCurrentData() {
        JSONException jSONException;
        JSONObject jSONObject;
        JSONException jSONException2;
        try {
            JSONObject jSONObject2 = new JSONObject(GsonUtil.toJson(this));
            try {
                jSONObject2 = disposePointerType(this, jSONObject2);
                try {
                    jSONObject2.remove("_c_");
                    if (data.length() > 0) {
                        Iterator keys = data.keys();
                        while (keys.hasNext()) {
                            String str = (String) keys.next();
                            jSONObject2.put(str, data.opt(str));
                        }
                    }
                    return jSONObject2;
                } catch (JSONException e) {
                    jSONException = e;
                    jSONObject = jSONObject2;
                    jSONException2 = jSONException;
                    jSONException2.printStackTrace();
                    return jSONObject;
                }
            } catch (JSONException e2) {
                jSONException = e2;
                jSONObject = jSONObject2;
                jSONException2 = jSONException;
                jSONException2.printStackTrace();
                return jSONObject;
            }
        } catch (JSONException e22) {
            jSONException2 = e22;
            jSONObject = null;
            jSONException2.printStackTrace();
            return jSONObject;
        }
    }

    public static JSONObject disposePointerType(BmobObject bmobobj, JSONObject object) throws JSONException {
        for (Field field : bmobobj.getClass().getDeclaredFields()) {
            JSONObject jSONObject;
            if (BmobUser.class.isAssignableFrom(field.getType())) {
                if (!object.isNull(field.getName())) {
                    jSONObject = new JSONObject();
                    jSONObject.put("__type", "Pointer");
                    jSONObject.put("objectId", object.optJSONObject(field.getName()).optString("objectId", "null"));
                    jSONObject.put("className", "_User");
                    object.put(field.getName(), jSONObject);
                }
            } else if (BmobRole.class.isAssignableFrom(field.getType())) {
                if (!object.isNull(field.getName())) {
                    jSONObject = new JSONObject();
                    jSONObject.put("__type", "Pointer");
                    jSONObject.put("objectId", object.optJSONObject(field.getName()).optString("objectId", "null"));
                    jSONObject.put("className", "_Role");
                    object.put(field.getName(), jSONObject);
                }
            } else if (BmobObject.class.isAssignableFrom(field.getType()) && !object.isNull(field.getName())) {
                jSONObject = new JSONObject();
                jSONObject.put("__type", "Pointer");
                jSONObject.put("objectId", object.optJSONObject(field.getName()).optString("objectId", "null"));
                jSONObject.put("className", field.getType().getSimpleName());
                object.put(field.getName(), jSONObject);
            }
        }
        return object;
    }

    private acknowledge Code(SaveListener<String> saveListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject currentData = getCurrentData();
            currentData.remove("createdAt");
            currentData.remove("updatedAt");
            currentData.remove("objectId");
            jSONObject.put("data", currentData);
            jSONObject.put("c", getTableName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new This().Code(thing.Code(getTableName())).Code("http://open.bmob.cn/8/create", jSONObject).Code(new Func1<JsonElement, Object>(this) {
            private /* synthetic */ BmobObject Code;

            {
                this.Code = r1;
            }

            public final /* synthetic */ Object call(Object obj) {
                JsonElement jsonElement = (JsonElement) obj;
                String asString = jsonElement.getAsJsonObject().get("objectId").getAsString();
                String asString2 = jsonElement.getAsJsonObject().get("createdAt").getAsString();
                this.Code.setObjectId(asString);
                this.Code.setCreatedAt(asString2);
                return asString;
            }
        }).V((BmobCallback) saveListener).C();
    }

    public Observable<String> saveObservable() {
        return Code(null).Code();
    }

    public Subscription save() {
        return save(null);
    }

    public Subscription save(SaveListener<String> listener) {
        return Code((SaveListener) listener).V();
    }

    private JSONObject Code(String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject currentData = getCurrentData();
            currentData.remove("createdAt");
            currentData.remove("updatedAt");
            currentData.remove("objectId");
            if (BmobInstallation.class.isAssignableFrom(getClass())) {
                currentData.remove("deviceType");
                currentData.remove("installationId");
            } else if (BmobRole.class.isAssignableFrom(getClass())) {
                currentData.remove("name");
            }
            jSONObject.put("c", getTableName());
            if (increments.size() > 0) {
                for (JSONObject jSONObject2 : increments) {
                    String optString = jSONObject2.optString("key");
                    jSONObject2.remove("key");
                    currentData.put(optString, jSONObject2);
                }
                increments.clear();
            }
            jSONObject.put("data", currentData);
            jSONObject.put("objectId", str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    private acknowledge Code(String str, UpdateListener updateListener) {
        return new This().Code(thing.Code(str, " objectId can't be empty ")).Code("http://open.bmob.cn/8/update", Code(str)).Code(new Func1<JsonElement, Object>(this) {
            private /* synthetic */ BmobObject Code;

            {
                this.Code = r1;
            }

            public final /* synthetic */ Object call(Object obj) {
                this.Code.setUpdatedAt(((JsonElement) obj).getAsJsonObject().get("updatedAt").getAsString());
                return null;
            }
        }).V((BmobCallback) updateListener).C();
    }

    public Observable<Void> updateObservable(String objectId) {
        return Code(objectId, null).Code();
    }

    public Observable<Void> updateObservable() {
        return updateObservable(getObjectId());
    }

    public Subscription update() {
        return update(getObjectId(), null);
    }

    public Subscription update(UpdateListener listener) {
        return update(getObjectId(), listener);
    }

    public Subscription update(String objectId, UpdateListener listener) {
        return Code(objectId, listener).V();
    }

    private acknowledge V(String str, UpdateListener updateListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("objectId", str);
            jSONObject.put("c", getTableName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new This().Code(thing.Code(str, " objectId can't be empty ")).Code("http://open.bmob.cn/8/delete", jSONObject).I().V((BmobCallback) updateListener).C();
    }

    public Observable<Void> deleteObservable(String objectId) {
        return V(objectId, null).Code();
    }

    public Subscription delete() {
        return delete(null);
    }

    public Subscription delete(UpdateListener listener) {
        return delete(getObjectId(), listener);
    }

    public Subscription delete(String objectId, UpdateListener listener) {
        return V(objectId, listener).V();
    }
}
