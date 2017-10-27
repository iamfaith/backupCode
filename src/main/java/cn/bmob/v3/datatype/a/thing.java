package cn.bmob.v3.datatype.a;

import android.util.Log;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.a.b.This;
import cn.bmob.v3.exception.BmobException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.http.cookie.ClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: BlockUploader */
public final class thing implements Runnable {
    private I B;
    private File C;
    private String Code;
    private String D;
    private String F;
    private long I;
    private int L;
    private This S;
    private String V;
    private mine Z;
    private String a;
    private String b;
    private RandomAccessFile c = null;
    private int[] d;
    private This e;
    private Map<String, Object> f;
    private String g;
    private darkness h;
    private int i;
    private int j;

    public thing(This thisR, File file, Map<String, Object> map, String str, darkness cn_bmob_v3_datatype_a_darkness, I i, mine cn_bmob_v3_datatype_a_mine) {
        this.S = thisR;
        this.C = file;
        this.f = map;
        this.Z = cn_bmob_v3_datatype_a_mine;
        this.B = i;
        this.g = str;
        this.h = cn_bmob_v3_datatype_a_darkness;
        this.j = Bmob.getFileBlockSize();
    }

    public final void run() {
        try {
            this.Code = (String) this.f.remove("bucket");
            this.V = "http://m0.api.upyun.com/" + this.Code;
            this.I = ((Long) this.f.get("expiration")).longValue();
            this.f.put("file_blocks", Integer.valueOf(This.Code(this.C, this.j)));
            this.f.put("file_size", Long.valueOf(this.C.length()));
            this.f.put("file_hash", This.Code(this.C));
            String str = (String) this.f.remove("save-key");
            String str2 = (String) this.f.get(ClientCookie.PATH_ATTR);
            if (str != null && str2 == null) {
                this.f.put(ClientCookie.PATH_ATTR, str);
            }
            this.F = This.Code(this.f);
            if (this.g != null) {
                this.D = This.Code(this.f, this.g);
            } else if (this.h != null) {
                this.D = this.h.Code(Code(this.f));
            } else {
                throw new RuntimeException("apikey 和 signatureListener 不可都为null");
            }
            this.c = new RandomAccessFile(this.C, "r");
            this.L = This.Code(this.C, this.j);
            V();
        } catch (Throwable e) {
            throw new RuntimeException("文件不存在", e);
        }
    }

    private void Code(int i) {
        int i2;
        Exception exception;
        Exception e;
        while (true) {
            if (this.e == null) {
                this.e = new This();
            }
            try {
                this.e.Code = V(i);
            } catch (BmobException e2) {
                this.B.Code(false, e2.toString());
            }
            Map hashMap = new HashMap();
            hashMap.put("save_token", this.a);
            hashMap.put("expiration", Long.valueOf(this.I));
            hashMap.put("block_index", Integer.valueOf(this.d[i]));
            hashMap.put("block_hash", This.Code(this.e.Code));
            String Code = This.Code(hashMap);
            String Code2 = This.Code(hashMap, this.b);
            Map hashMap2 = new HashMap();
            hashMap2.put("policy", Code);
            hashMap2.put("signature", Code2);
            this.e.I = this.C.getName();
            this.e.V = hashMap2;
            try {
                this.S.Code(this.V, this.e);
                if (this.Z != null) {
                    this.Z.Code((long) i, (long) this.d.length);
                }
                i2 = i + 1;
                try {
                    if (i == this.d.length - 1) {
                        Code();
                        this.e = null;
                        return;
                    }
                    this.e = null;
                    i = i2;
                } catch (Exception e3) {
                    i = i2;
                    exception = e3;
                    e3 = exception;
                    try {
                        i2 = this.i + 1;
                        this.i = i2;
                        if (i2 > 2 || ((e3 instanceof BmobException) && ((BmobException) e3).getErrorCode() / 100 != 5)) {
                            this.B.Code(false, e3.getMessage());
                            this.e = null;
                        }
                        this.e = null;
                    } catch (Throwable th) {
                        this.e = null;
                    }
                } catch (Exception e32) {
                    i = i2;
                    exception = e32;
                    e32 = exception;
                    i2 = this.i + 1;
                    this.i = i2;
                    if (i2 > 2) {
                        break;
                    }
                    break;
                    this.B.Code(false, e32.getMessage());
                    this.e = null;
                }
            } catch (IOException e4) {
                exception = e4;
                e32 = exception;
                i2 = this.i + 1;
                this.i = i2;
                if (i2 > 2) {
                    break;
                }
                break;
                this.B.Code(false, e32.getMessage());
                this.e = null;
            } catch (BmobException e5) {
                exception = e5;
                e32 = exception;
                i2 = this.i + 1;
                this.i = i2;
                if (i2 > 2) {
                    break;
                }
                break;
                this.B.Code(false, e32.getMessage());
                this.e = null;
            }
        }
        this.B.Code(false, e32.getMessage());
        this.e = null;
    }

    private void Code() {
        Exception e;
        while (true) {
            Map hashMap = new HashMap();
            hashMap.put("expiration", Long.valueOf(this.I));
            hashMap.put("save_token", this.a);
            String Code = This.Code(hashMap);
            String Code2 = This.Code(hashMap, this.b);
            Map linkedHashMap = new LinkedHashMap();
            linkedHashMap.put("policy", Code);
            linkedHashMap.put("signature", Code2);
            try {
                Code2 = this.S.Code(this.V, linkedHashMap);
                this.Z.Code((long) this.d.length, (long) this.d.length);
                this.B.Code(true, Code2);
                return;
            } catch (IOException e2) {
                e = e2;
            } catch (BmobException e3) {
                e = e3;
            }
        }
        this.B.Code(false, e.getMessage());
        e.printStackTrace();
        int i = this.i + 1;
        this.i = i;
        if (i > 2 || ((e instanceof BmobException) && ((BmobException) e).getErrorCode() / 100 != 5)) {
            this.B.Code(false, e.getMessage());
        }
    }

    private void V() {
        Exception e;
        int i;
        Map linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("policy", this.F);
        linkedHashMap.put("signature", this.D);
        try {
            JSONObject jSONObject = new JSONObject(this.S.Code(this.V, linkedHashMap));
            this.a = jSONObject.optString("save_token");
            this.b = jSONObject.optString("token_secret");
            this.d = Code(jSONObject.getJSONArray("status"));
            if (this.d.length == 0) {
                Code();
            } else {
                Code(0);
            }
        } catch (IOException e2) {
            e = e2;
            e.printStackTrace();
            i = this.i + 1;
            this.i = i;
            if (i <= 2 || ((e instanceof BmobException) && ((BmobException) e).getErrorCode() / 100 != 5)) {
                this.B.Code(false, e.getMessage());
            } else {
                V();
            }
        } catch (BmobException e3) {
            e = e3;
            e.printStackTrace();
            i = this.i + 1;
            this.i = i;
            if (i <= 2) {
            }
            this.B.Code(false, e.getMessage());
        } catch (JSONException e4) {
            e4.printStackTrace();
            this.B.Code(false, e4.getMessage());
        }
    }

    private byte[] V(int i) throws BmobException {
        if (i > this.L) {
            Log.e("Block index error", "the index is bigger than totalBlockNum.");
            throw new BmobException("readBlockByIndex: the index is bigger than totalBlockNum.");
        }
        Object obj = new byte[this.j];
        try {
            this.c.seek((long) (this.d[i] * this.j));
            int read = this.c.read(obj, 0, this.j);
            if (read >= this.j) {
                return obj;
            }
            Object obj2 = new byte[read];
            System.arraycopy(obj, 0, obj2, 0, read);
            return obj2;
        } catch (IOException e) {
            throw new BmobException(e.getMessage());
        }
    }

    private static int[] Code(JSONArray jSONArray) throws JSONException {
        int i;
        int i2 = 0;
        int i3 = 0;
        for (i = 0; i < jSONArray.length(); i++) {
            if (jSONArray.getInt(i) == 0) {
                i3++;
            }
        }
        int[] iArr = new int[i3];
        i = 0;
        while (i2 < jSONArray.length()) {
            if (jSONArray.getInt(i2) == 0) {
                iArr[i] = i2;
                i++;
            }
            i2++;
        }
        return iArr;
    }

    private static String Code(Map<String, Object> map) {
        Object[] toArray = map.keySet().toArray();
        Arrays.sort(toArray);
        StringBuffer stringBuffer = new StringBuffer("");
        for (Object obj : toArray) {
            stringBuffer.append(obj).append(map.get(obj));
        }
        return stringBuffer.toString();
    }
}
