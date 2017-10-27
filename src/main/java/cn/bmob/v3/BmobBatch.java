package cn.bmob.v3;

import cn.bmob.v3.b.The;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.helper.GsonUtil;
import cn.bmob.v3.http.acknowledge;
import cn.bmob.v3.http.acknowledge.This;
import cn.bmob.v3.http.thing;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.QueryListListener;
import java.util.List;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.cookie.ClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscription;

public class BmobBatch {
    private final JSONArray Code = new JSONArray();

    public BmobBatch insertBatch(List<BmobObject> objects) {
        return Code(HttpPost.METHOD_NAME, objects);
    }

    public BmobBatch deleteBatch(List<BmobObject> objects) {
        return Code(HttpDelete.METHOD_NAME, objects);
    }

    public BmobBatch updateBatch(List<BmobObject> objects) {
        return Code(HttpPut.METHOD_NAME, objects);
    }

    private BmobBatch Code(String str, List<BmobObject> list) {
        try {
            for (BmobObject bmobObject : list) {
                if (bmobObject instanceof BmobUser) {
                    throw new IllegalArgumentException("BmobUser does not support batch operations");
                }
                JSONObject jSONObject = new JSONObject(GsonUtil.toJson(bmobObject));
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("method", str);
                if (str.equals(HttpPut.METHOD_NAME) || str.equals(HttpDelete.METHOD_NAME)) {
                    jSONObject2.put("token", new The().V("sessionToken", ""));
                    jSONObject2.put(ClientCookie.PATH_ATTR, "/1/classes/" + bmobObject.getTableName() + "/" + bmobObject.getObjectId());
                    jSONObject.remove("createdAt");
                    jSONObject.remove("updatedAt");
                    jSONObject.remove("objectId");
                } else {
                    jSONObject2.put(ClientCookie.PATH_ATTR, "/1/classes/" + bmobObject.getTableName());
                }
                jSONObject.remove("_c_");
                jSONObject2.put("body", BmobObject.disposePointerType(bmobObject, jSONObject));
                this.Code.put(jSONObject2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    private acknowledge Code(QueryListListener<BatchResult> queryListListener) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("data", new JSONObject().put("requests", this.Code));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new This().Code(thing.Code(this.Code)).Code("http://open.bmob.cn/8/batch", jSONObject).B().V((BmobCallback) queryListListener).C();
    }

    public Observable<List<BatchResult>> doBatchObservable() {
        return Code(null).Code();
    }

    public Subscription doBatch(QueryListListener<BatchResult> listener) {
        return Code(listener).V();
    }
}
