package cn.bmob.v3.socketio;

import cn.bmob.v3.socketio.callback.EventCallback;
import java.util.ArrayList;

/* compiled from: EventEmitter */
public class of {
    darkness<EventCallback> Code = new darkness();

    /* compiled from: EventEmitter */
    interface This extends EventCallback {
    }

    public final void Code(String str, EventCallback eventCallback) {
        darkness cn_bmob_v3_socketio_darkness = this.Code;
        ArrayList arrayList = (ArrayList) cn_bmob_v3_socketio_darkness.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList();
            cn_bmob_v3_socketio_darkness.put(str, arrayList);
        }
        arrayList.add(eventCallback);
    }
}
