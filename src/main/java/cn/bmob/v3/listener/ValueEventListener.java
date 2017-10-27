package cn.bmob.v3.listener;

import org.json.JSONObject;

public interface ValueEventListener {
    void onConnectCompleted(Exception exception);

    void onDataChange(JSONObject jSONObject);
}
