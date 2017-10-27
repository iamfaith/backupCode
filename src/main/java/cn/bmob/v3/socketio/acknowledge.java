package cn.bmob.v3.socketio;

import android.os.Handler;
import android.text.TextUtils;
import cn.bmob.v3.socketio.callback.ConnectCallback;
import cn.bmob.v3.socketio.thing.This;
import org.json.JSONArray;
import org.json.JSONObject;

/* compiled from: SocketIOClient */
public final class acknowledge extends of {
    String B;
    private mine C;
    boolean I;
    boolean V;
    ConnectCallback Z;

    public static void Code(final This thisR, final ConnectCallback connectCallback, final Handler handler) {
        final mine cn_bmob_v3_socketio_mine = new mine(handler, new thing(), thisR);
        cn_bmob_v3_socketio_mine.V.add(new acknowledge(cn_bmob_v3_socketio_mine, "", new ConnectCallback() {
            public final void onConnectCompleted(Exception ex, acknowledge client) {
                if (ex == null && !TextUtils.isEmpty(thisR.V())) {
                    cn_bmob_v3_socketio_mine.V.remove(client);
                    client.Code(thisR.V(), new ConnectCallback(this) {
                        private /* synthetic */ AnonymousClass1 Code;

                        {
                            this.Code = r1;
                        }

                        public final void onConnectCompleted(Exception ex, acknowledge client) {
                            if (connectCallback != null) {
                                connectCallback.onConnectCompleted(ex, client);
                            }
                        }
                    });
                } else if (connectCallback != null) {
                    connectCallback.onConnectCompleted(ex, client);
                }
            }
        }));
        cn_bmob_v3_socketio_mine.V();
    }

    private acknowledge(mine cn_bmob_v3_socketio_mine, String str, ConnectCallback connectCallback) {
        this.B = str;
        this.C = cn_bmob_v3_socketio_mine;
        this.Z = connectCallback;
    }

    public final boolean Code() {
        return this.V && !this.I && this.C.Code();
    }

    public final void Code(String str, ConnectCallback connectCallback) {
        mine cn_bmob_v3_socketio_mine = this.C;
        cn_bmob_v3_socketio_mine.V.add(new acknowledge(this.C, str, connectCallback));
        cn_bmob_v3_socketio_mine.I.Code(String.format("1::%s", new Object[]{r1.B}));
    }

    public final void Code(String str, JSONArray jSONArray) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("name", str);
            jSONObject.put("args", jSONArray);
            String jSONObject2 = jSONObject.toString();
            mine cn_bmob_v3_socketio_mine = this.C;
            String str2 = "";
            if (null != null) {
                StringBuilder stringBuilder = new StringBuilder();
                int i = cn_bmob_v3_socketio_mine.C;
                cn_bmob_v3_socketio_mine.C = i + 1;
                String stringBuilder2 = stringBuilder.append(i).toString();
                str2 = stringBuilder2 + "+";
                cn_bmob_v3_socketio_mine.B.put(stringBuilder2, null);
            }
            cn_bmob_v3_socketio_mine.I.Code(String.format("%d:%s:%s:%s", new Object[]{Integer.valueOf(5), str2, this.B, jSONObject2}));
        } catch (Exception e) {
        }
    }
}
