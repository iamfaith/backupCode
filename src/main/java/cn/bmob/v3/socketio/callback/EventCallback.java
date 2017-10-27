package cn.bmob.v3.socketio.callback;

import cn.bmob.v3.socketio.This;
import org.json.JSONArray;

public interface EventCallback {
    void onEvent(String str, JSONArray jSONArray, This thisR);
}
