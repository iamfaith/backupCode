package cn.bmob.v3.socketio.callback;

import cn.bmob.v3.socketio.This;
import org.json.JSONObject;

public interface JSONCallback {
    void onJSON(JSONObject jSONObject, This thisR);
}
