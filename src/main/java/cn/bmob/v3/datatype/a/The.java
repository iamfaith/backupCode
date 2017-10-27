package cn.bmob.v3.datatype.a;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.a.b.This;
import cn.bmob.v3.http.I;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* compiled from: UploadManager */
public class The {
    private static volatile The Code;
    private This I = new This();
    private ExecutorService V = Executors.newFixedThreadPool(acknowledge.Code);

    private The() {
    }

    public static The Code() {
        if (Code == null) {
            synchronized (The.class) {
                if (Code == null) {
                    Code = new The();
                }
            }
        }
        return Code;
    }

    public final void Code(File file, Map<String, Object> map, darkness cn_bmob_v3_datatype_a_darkness, final I i, final mine cn_bmob_v3_datatype_a_mine) {
        if (file == null) {
            i.Code(false, "文件不可以为空");
        } else if (map == null) {
            i.Code(false, "参数不可为空");
        } else if (cn_bmob_v3_datatype_a_darkness == null) {
            i.Code(false, "APIkey和signatureListener不可同时为null");
        } else if (i == null) {
            throw new RuntimeException("completeListener 不可为null");
        } else {
            if (map.get("bucket") == null) {
                map.put("bucket", null);
            }
            if (map.get("expiration") == null) {
                map.put("expiration", Long.valueOf(Bmob.getFileExpiration() + I.I()));
            }
            mine anonymousClass1 = new mine(this) {
                public final void Code(long j, long j2) {
                    final long j3 = j;
                    final long j4 = j2;
                    This.Code(new Runnable(this) {
                        private /* synthetic */ AnonymousClass1 I;

                        public final void run() {
                            if (cn_bmob_v3_datatype_a_mine != null) {
                                cn_bmob_v3_datatype_a_mine.Code(j3, j4);
                            }
                        }
                    });
                }
            };
            I anonymousClass2 = new I(this) {
                public final void Code(final boolean z, final String str) {
                    This.Code(new Runnable(this) {
                        private /* synthetic */ AnonymousClass2 I;

                        public final void run() {
                            i.Code(z, str);
                        }
                    });
                }
            };
            Map hashMap = new HashMap();
            hashMap.putAll(map);
            this.V.execute(new thing(this.I, file, hashMap, null, cn_bmob_v3_datatype_a_darkness, anonymousClass2, anonymousClass1));
        }
    }
}
