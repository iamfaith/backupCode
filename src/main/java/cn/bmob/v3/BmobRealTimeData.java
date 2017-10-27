package cn.bmob.v3;

import android.os.Handler;
import android.util.Log;
import cn.bmob.v3.b.The;
import cn.bmob.v3.http.I;
import cn.bmob.v3.listener.ValueEventListener;
import cn.bmob.v3.socketio.acknowledge;
import cn.bmob.v3.socketio.callback.ConnectCallback;
import cn.bmob.v3.socketio.callback.EventCallback;
import cn.bmob.v3.socketio.thing.This;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BmobRealTimeData {
    public static final String ACTION_DELETEROW = "deleteRow";
    public static final String ACTION_DELETETABLE = "deleteTable";
    public static final String ACTION_UPDATEROW = "updateRow";
    public static final String ACTION_UPDATETABLE = "updateTable";
    public static final String TAG = "BmobRealTimeData";
    private acknowledge Code;

    public void start(final ValueEventListener listener) {
        String V = new The().V("io", "http://io.bmob.cn:3010");
        acknowledge.Code(new This(V), new ConnectCallback(this) {
            private /* synthetic */ BmobRealTimeData V;

            public final void onConnectCompleted(Exception ex, final acknowledge client) {
                if (listener == null) {
                    Log.e(BmobConstants.TAG, BmobConstants.ERROR_LISTENER);
                } else if (ex == null && client.Code()) {
                    this.V.Code = client;
                    listener.onConnectCompleted(null);
                    client.Code("client_send_data", new EventCallback(this) {
                        final /* synthetic */ AnonymousClass1 Code;

                        public final void onEvent(String str, JSONArray jSONArray, cn.bmob.v3.socketio.This thisR) {
                            client.Code("server_pub", new EventCallback(this) {
                                private /* synthetic */ AnonymousClass1 Code;

                                {
                                    this.Code = r1;
                                }

                                public final void onEvent(String str, JSONArray argument, cn.bmob.v3.socketio.This thisR) {
                                    try {
                                        listener.onDataChange(new JSONObject(argument.optString(0)));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    listener.onConnectCompleted(ex);
                }
            }
        }, new Handler());
    }

    public boolean isConnected() {
        if (this.Code == null) {
            return false;
        }
        return this.Code.Code();
    }

    public void subTableUpdate(String tableName) {
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(Code(tableName, "", ACTION_UPDATETABLE).toString());
        this.Code.Code("client_sub", jSONArray);
    }

    public void unsubTableUpdate(String tableName) {
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(Code(tableName, "", "unsub_updateTable").toString());
        this.Code.Code("client_unsub", jSONArray);
    }

    public void subTableDelete(String tableName) {
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(Code(tableName, "", ACTION_DELETETABLE).toString());
        this.Code.Code("client_sub", jSONArray);
    }

    public void unsubTableDelete(String tableName) {
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(Code(tableName, "", "unsub_deleteTable").toString());
        this.Code.Code("client_unsub", jSONArray);
    }

    public void subRowUpdate(String tableName, String objectId) {
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(Code(tableName, objectId, ACTION_UPDATEROW).toString());
        this.Code.Code("client_sub", jSONArray);
    }

    public void unsubRowUpdate(String tableName, String objectId) {
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(Code(tableName, objectId, "unsub_updateRow").toString());
        this.Code.Code("client_unsub", jSONArray);
    }

    public void subRowDelete(String tableName, String objectId) {
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(Code(tableName, objectId, ACTION_DELETEROW).toString());
        this.Code.Code("client_sub", jSONArray);
    }

    public void unsubRowDelete(String tableName, String objectId) {
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(Code(tableName, objectId, "unsub_deleteRow").toString());
        this.Code.Code("client_unsub", jSONArray);
    }

    private static JSONObject Code(String str, String str2, String str3) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("appKey", I.V());
            jSONObject.put("tableName", str);
            jSONObject.put("objectId", str2);
            jSONObject.put("action", str3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject;
    }
}
