package cn.bmob.v3;

import android.content.Context;
import android.text.TextUtils;
import cn.bmob.v3.BmobConfig.Builder;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobTableSchema;
import cn.bmob.v3.http.acknowledge;
import cn.bmob.v3.http.thing;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UploadBatchListener;
import java.io.File;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscription;

public class Bmob {
    private static byte[] Code = new byte[0];

    public static void initialize(Context context, String appkey) {
        BmobWrapper.Code(new Builder(context).setApplicationId(appkey).setUploadBlockSize(BmobConstants.BLOCK_SIZE).setFileExpiration(BmobConstants.EXPIRATION).setConnectTimeout((long) BmobConstants.CONNECT_TIMEOUT).build());
    }

    public static void initialize(BmobConfig config) {
        if (TextUtils.isEmpty(config.applicationId)) {
            throw new RuntimeException("ApplicationId is null. You must call setApplicationId(Your AppId) method in BmobConfig.Builder(context).");
        }
        BmobWrapper.Code(config);
    }

    public static File getCacheDir() {
        return BmobWrapper.getInstance().Code();
    }

    public static File getFilesDir() {
        return BmobWrapper.getInstance().V();
    }

    public static File getCacheDir(String subDir) {
        File file;
        synchronized (Code) {
            file = new File(getCacheDir(), subDir);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return file;
    }

    public static File getFilesDir(String subDir) {
        File file;
        synchronized (Code) {
            file = new File(getFilesDir(), subDir);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return file;
    }

    private static void Code() {
        if (BmobWrapper.getInstance().getApplicationContext() == null) {
            throw new RuntimeException("applicationContext is null. You must call initialize  before using the Bmob library.");
        }
    }

    public static Context getApplicationContext() {
        Code();
        return BmobWrapper.getInstance().getApplicationContext();
    }

    public static long getConnectTimeout() {
        Code();
        return BmobWrapper.getInstance().getConnectTimeout();
    }

    public static int getFileBlockSize() {
        Code();
        return BmobWrapper.getInstance().getUploadBlockSize();
    }

    public static long getFileExpiration() {
        Code();
        return BmobWrapper.getInstance().getFileExpiration();
    }

    @Deprecated
    public static void uploadBatch(String[] filePaths, UploadBatchListener listener) {
        BmobFile.uploadBatch(filePaths, listener);
    }

    public static Subscription getServerTime(QueryListener<Long> listener) {
        return thing.Code().Code((QueryListener) listener).V();
    }

    public static Observable<Long> getServerTimeObservable() {
        return thing.Code().Code(null).Code();
    }

    private static acknowledge Code(QueryListListener<BmobTableSchema> queryListListener) {
        JSONObject jSONObject;
        JSONException e;
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject = new JSONObject();
            try {
                jSONObject.put("data", new JSONObject());
                jSONObject.put("c", "");
            } catch (JSONException e2) {
                e = e2;
                e.printStackTrace();
                return thing.Code().Code(jSONObject, (QueryListListener) queryListListener);
            }
        } catch (JSONException e3) {
            JSONException jSONException = e3;
            jSONObject = jSONObject2;
            e = jSONException;
            e.printStackTrace();
            return thing.Code().Code(jSONObject, (QueryListListener) queryListListener);
        }
        return thing.Code().Code(jSONObject, (QueryListListener) queryListListener);
    }

    public static Subscription getAllTableSchema(QueryListListener<BmobTableSchema> listener) {
        return Code(listener).V();
    }

    public static Observable<List<BmobTableSchema>> getAllTableSchemaObservable() {
        return Code(null).Code();
    }

    private static acknowledge Code(String str, QueryListener<BmobTableSchema> queryListener) {
        JSONObject jSONObject;
        JSONException e;
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject = new JSONObject();
            try {
                jSONObject.put("data", new JSONObject());
                jSONObject.put("c", str);
            } catch (JSONException e2) {
                e = e2;
                e.printStackTrace();
                return thing.Code().Code(jSONObject, (QueryListener) queryListener);
            }
        } catch (JSONException e3) {
            JSONException jSONException = e3;
            jSONObject = jSONObject2;
            e = jSONException;
            e.printStackTrace();
            return thing.Code().Code(jSONObject, (QueryListener) queryListener);
        }
        return thing.Code().Code(jSONObject, (QueryListener) queryListener);
    }

    public static Subscription getTableSchema(String tableName, QueryListener<BmobTableSchema> listener) {
        return Code(tableName, listener).V();
    }

    public static Observable<BmobTableSchema> getTableSchemaObservable(String tableName) {
        return Code(tableName, null).Code();
    }
}
