package cn.bmob.v3.datatype.a;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.helper.ErrorCode;
import cn.bmob.v3.http.bean.Upyun;
import cn.bmob.v3.listener.UploadFileListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.http.cookie.ClientCookie;

/* compiled from: UpYunUploader */
public final class From extends of {
    private Context B;
    private File C;
    Upyun I;
    private WakeLock Z;

    static /* synthetic */ void Code(From from, boolean z, String str) {
        if (z) {
            try {
                String str2 = "http://" + from.I.getDomain() + ((String) ((Map) new Gson().fromJson(str, new TypeToken<Map<String, String>>(from) {
                }.getType())).get(ClientCookie.PATH_ATTR));
                from.Code.obtain(from.C.getName(), "", str2);
                if (from.V != null) {
                    from.V.done(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (from.V != null) {
                    from.V.done(new BmobException((int) ErrorCode.E9015, e.getMessage()));
                }
            }
        } else if (from.V != null) {
            from.V.done(new BmobException((int) ErrorCode.E9015, str));
        }
    }

    public From(Context context, Upyun upyun, BmobFile bmobFile, UploadFileListener uploadFileListener) {
        super(bmobFile, uploadFileListener);
        this.I = upyun;
        this.B = context;
        this.C = bmobFile.getLocalFile();
    }

    public final void Code() {
        String str;
        String str2;
        this.Z = ((PowerManager) this.B.getSystemService("power")).newWakeLock(1, getClass().getName());
        this.Z.acquire();
        Map hashMap = new HashMap();
        hashMap.put("bucket", this.I.getName());
        StringBuilder stringBuilder = new StringBuilder();
        Calendar instance = Calendar.getInstance();
        int i = instance.get(1);
        int i2 = instance.get(2) + 1;
        int i3 = instance.get(5);
        if (i2 < 10) {
            str = "0" + i2;
        } else {
            str = String.valueOf(i2);
        }
        if (i3 < 10) {
            str2 = "0" + i3;
        } else {
            str2 = String.valueOf(i3);
        }
        String path = this.C.getPath();
        stringBuilder.append("/").append(i).append("/").append(str).append("/").append(str2).append("/").append(UUID.randomUUID().toString().replace("-", "")).append(".").append(path.substring(path.lastIndexOf(".") + 1));
        hashMap.put("save-key", stringBuilder.toString());
        mine anonymousClass1 = new mine(this) {
            private /* synthetic */ From Code;

            {
                this.Code = r1;
            }

            public final void Code(long j, long j2) {
                int i = (int) ((100 * j) / j2);
                if (this.Code.V != null) {
                    this.Code.V.onProgress(Integer.valueOf(i));
                }
            }
        };
        I anonymousClass2 = new I(this) {
            private /* synthetic */ From Code;

            {
                this.Code = r1;
            }

            public final void Code(boolean z, String str) {
                From.Code(this.Code, z, str);
                if (this.Code.Z != null) {
                    this.Code.Z.release();
                }
                if (this.Code.V != null) {
                    this.Code.V.onFinish();
                }
            }
        };
        The.Code().Code(this.C, hashMap, new darkness(this) {
            private /* synthetic */ From Code;

            {
                this.Code = r1;
            }

            public final String Code(String str) {
                return This.Code(str + this.Code.I.getSecret());
            }
        }, anonymousClass2, anonymousClass1);
    }
}
