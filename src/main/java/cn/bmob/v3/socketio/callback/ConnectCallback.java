package cn.bmob.v3.socketio.callback;

import cn.bmob.v3.socketio.acknowledge;

public interface ConnectCallback {
    void onConnectCompleted(Exception exception, acknowledge cn_bmob_v3_socketio_acknowledge);
}
