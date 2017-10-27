package cn.bmob.v3.socketio;

import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import cn.bmob.v3.BmobConstants;
import cn.bmob.v3.socketio.callback.ConnectCallback;
import cn.bmob.v3.socketio.callback.EventCallback;
import cn.bmob.v3.socketio.thing.thing;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* compiled from: SocketIOConnection */
final class mine {
    Hashtable<String, This> B = new Hashtable();
    int C;
    int Code;
    private thing D;
    private Handler F;
    From I;
    long S = 1000;
    ArrayList<acknowledge> V = new ArrayList();
    cn.bmob.v3.socketio.thing.This Z;

    /* compiled from: SocketIOConnection */
    class AnonymousClass3 implements Runnable {
        private /* synthetic */ From Code;
        private /* synthetic */ mine V;

        AnonymousClass3(mine cn_bmob_v3_socketio_mine, From from) {
            this.V = cn_bmob_v3_socketio_mine;
            this.Code = from;
        }

        public final void run() {
            if (this.V.Code > 0 && this.Code == this.V.I && this.Code != null && this.Code.Z()) {
                this.V.I.Code("2:::");
                this.V.F.postDelayed(this, (long) this.V.Code);
            }
        }
    }

    /* compiled from: SocketIOConnection */
    interface This {
        void Code(acknowledge cn_bmob_v3_socketio_acknowledge);
    }

    static /* synthetic */ void Code(mine cn_bmob_v3_socketio_mine, final Exception exception) {
        cn_bmob_v3_socketio_mine.Code(null, new This(cn_bmob_v3_socketio_mine) {
            private /* synthetic */ mine V;

            public final void Code(final acknowledge cn_bmob_v3_socketio_acknowledge) {
                if (cn_bmob_v3_socketio_acknowledge.V) {
                    cn_bmob_v3_socketio_acknowledge.I = true;
                    if (null != null) {
                        this.V.F.post(new Runnable(this, null) {
                            private /* synthetic */ AnonymousClass5 V;

                            public final void run() {
                                null.onDisconnect(exception);
                            }
                        });
                        return;
                    }
                    return;
                }
                final ConnectCallback connectCallback = cn_bmob_v3_socketio_acknowledge.Z;
                if (connectCallback != null) {
                    this.V.F.post(new Runnable(this) {
                        private /* synthetic */ AnonymousClass5 I;

                        public final void run() {
                            connectCallback.onConnectCompleted(exception, cn_bmob_v3_socketio_acknowledge);
                        }
                    });
                }
            }
        });
        if (cn_bmob_v3_socketio_mine.I == null && cn_bmob_v3_socketio_mine.V.size() != 0) {
            Object obj;
            Iterator it = cn_bmob_v3_socketio_mine.V.iterator();
            while (it.hasNext()) {
                if (((acknowledge) it.next()).I) {
                    obj = 1;
                    break;
                }
            }
            obj = null;
            if (obj != null) {
                cn_bmob_v3_socketio_mine.F.postDelayed(new Runnable(cn_bmob_v3_socketio_mine) {
                    private /* synthetic */ mine Code;

                    {
                        this.Code = r1;
                    }

                    public final void run() {
                        this.Code.V();
                    }
                }, cn_bmob_v3_socketio_mine.S);
                cn_bmob_v3_socketio_mine.S <<= 1;
            }
        }
    }

    public mine(Handler handler, thing cn_bmob_v3_socketio_thing, cn.bmob.v3.socketio.thing.This thisR) {
        this.F = handler;
        this.D = cn_bmob_v3_socketio_thing;
        this.Z = thisR;
    }

    public final boolean Code() {
        return this.I != null && this.I.Z();
    }

    final void V() {
        if (!Code()) {
            new cn.bmob.v3.socketio.thing.AnonymousClass1(this.D, this.Z, new thing(this) {
                final /* synthetic */ mine Code;

                {
                    this.Code = r1;
                }

                public final void Code(Exception exception, String str) {
                    if (exception != null) {
                        mine.Code(this.Code, exception);
                        return;
                    }
                    try {
                        String[] split = str.split(":");
                        String str2 = split[0];
                        if ("".equals(split[1])) {
                            this.Code.Code = 0;
                        } else {
                            this.Code.Code = (Integer.parseInt(split[1]) / 2) * BmobConstants.TIME_DELAY_RETRY;
                        }
                        if (new HashSet(Arrays.asList(split[3].split(","))).contains("websocket")) {
                            String uri = Uri.parse(this.Code.Z.Code()).buildUpon().appendPath("websocket").appendPath(str2).build().toString();
                            this.Code.I = new From(URI.create(uri), new cn.bmob.v3.socketio.From.This(this) {
                                private /* synthetic */ AnonymousClass1 Code;

                                {
                                    this.Code = r1;
                                }

                                public final void Code(String str) {
                                    try {
                                        String[] split = str.split(":", 4);
                                        switch (Integer.parseInt(split[0])) {
                                            case 0:
                                                this.Code.Code.I.I();
                                                mine.Code(this.Code.Code, null);
                                                return;
                                            case 1:
                                                this.Code.Code.Code(split[2], new This(this.Code.Code) {
                                                    public final void Code(acknowledge cn_bmob_v3_socketio_acknowledge) {
                                                        if (!cn_bmob_v3_socketio_acknowledge.Code()) {
                                                            if (!cn_bmob_v3_socketio_acknowledge.V) {
                                                                cn_bmob_v3_socketio_acknowledge.V = true;
                                                                ConnectCallback connectCallback = cn_bmob_v3_socketio_acknowledge.Z;
                                                                if (connectCallback != null) {
                                                                    connectCallback.onConnectCompleted(null, cn_bmob_v3_socketio_acknowledge);
                                                                }
                                                            } else if (cn_bmob_v3_socketio_acknowledge.I) {
                                                                cn_bmob_v3_socketio_acknowledge.I = false;
                                                            }
                                                        }
                                                    }
                                                });
                                                return;
                                            case 2:
                                                this.Code.Code.I.Code("2::");
                                                return;
                                            case 3:
                                                this.Code.Code.Code(split[2], new This(this.Code.Code, split[3], mine.V(this.Code.Code, split[1])) {
                                                    private /* synthetic */ mine I;

                                                    public final void Code(acknowledge cn_bmob_v3_socketio_acknowledge) {
                                                    }
                                                });
                                                return;
                                            case 4:
                                                this.Code.Code.Code(split[2], new This(this.Code.Code, new JSONObject(split[3]), mine.V(this.Code.Code, split[1])) {
                                                    private /* synthetic */ mine I;

                                                    public final void Code(acknowledge cn_bmob_v3_socketio_acknowledge) {
                                                    }
                                                });
                                                return;
                                            case 5:
                                                JSONObject jSONObject = new JSONObject(split[3]);
                                                this.Code.Code.Code(split[2], new This(this.Code.Code, jSONObject.getString("name"), jSONObject.optJSONArray("args"), mine.V(this.Code.Code, split[1])) {
                                                    private /* synthetic */ mine Z;

                                                    public final void Code(final acknowledge cn_bmob_v3_socketio_acknowledge) {
                                                        this.Z.F.post(new Runnable(this) {
                                                            private /* synthetic */ AnonymousClass9 V;

                                                            public final void run() {
                                                                of ofVar = cn_bmob_v3_socketio_acknowledge;
                                                                String str = r3;
                                                                JSONArray jSONArray = r4;
                                                                This thisR = r5;
                                                                List list = (List) ofVar.Code.get(str);
                                                                if (list != null) {
                                                                    Iterator it = list.iterator();
                                                                    while (it.hasNext()) {
                                                                        EventCallback eventCallback = (EventCallback) it.next();
                                                                        eventCallback.onEvent(str, jSONArray, thisR);
                                                                        if (eventCallback instanceof This) {
                                                                            it.remove();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                                return;
                                            case 6:
                                                String[] split2 = split[3].split("\\+", 2);
                                                This thisR = (This) this.Code.Code.B.remove(split2[0]);
                                                if (thisR != null) {
                                                    JSONArray jSONArray;
                                                    if (split2.length == 2) {
                                                        jSONArray = new JSONArray(split2[1]);
                                                    } else {
                                                        jSONArray = null;
                                                    }
                                                    thisR.Code(jSONArray);
                                                    return;
                                                }
                                                return;
                                            case 7:
                                                this.Code.Code.Code(split[2], new This(this.Code.Code, split[3]) {
                                                    private /* synthetic */ mine V;

                                                    public final void Code(acknowledge cn_bmob_v3_socketio_acknowledge) {
                                                    }
                                                });
                                                return;
                                            case 8:
                                                return;
                                            default:
                                                throw new Exception("unknown code");
                                        }
                                    } catch (Exception e) {
                                        this.Code.Code.I.I();
                                        this.Code.Code.I = null;
                                        mine.Code(this.Code.Code, e);
                                    }
                                    this.Code.Code.I.I();
                                    this.Code.Code.I = null;
                                    mine.Code(this.Code.Code, e);
                                }

                                public final void Code(Exception exception) {
                                    mine.Code(this.Code.Code, exception);
                                }

                                public final void Code(int i, String str) {
                                    mine.Code(this.Code.Code, new IOException(String.format("Disconnected code %d for reason %s", new Object[]{Integer.valueOf(i), str})));
                                }

                                public final void Code() {
                                    this.Code.Code.S = 1000;
                                    mine cn_bmob_v3_socketio_mine = this.Code.Code;
                                    new AnonymousClass3(cn_bmob_v3_socketio_mine, cn_bmob_v3_socketio_mine.I).run();
                                }
                            }, null);
                            this.Code.I.V();
                            return;
                        }
                        throw new Exception("websocket not supported");
                    } catch (Exception e) {
                        mine.Code(this.Code, e);
                    }
                }
            }).execute(new Void[0]);
        }
    }

    private void Code(String str, This thisR) {
        Iterator it = this.V.iterator();
        while (it.hasNext()) {
            acknowledge cn_bmob_v3_socketio_acknowledge = (acknowledge) it.next();
            if (str == null || TextUtils.equals(cn_bmob_v3_socketio_acknowledge.B, str)) {
                thisR.Code(cn_bmob_v3_socketio_acknowledge);
            }
        }
    }

    static /* synthetic */ This V(mine cn_bmob_v3_socketio_mine, final String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return new This(cn_bmob_v3_socketio_mine) {
            private /* synthetic */ mine V;

            public final void Code(JSONArray jSONArray) {
                String str = "";
                if (jSONArray != null) {
                    str = str + "+" + jSONArray.toString();
                }
                this.V.I.Code(String.format("6:::%s%s", new Object[]{str, str}));
            }
        };
    }
}
