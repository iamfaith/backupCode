package cn.bmob.v3.a.b;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.a.acknowledge;
import cn.bmob.v3.exception.BmobException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.http.protocol.HTTP;

/* compiled from: DigestUtils */
public class This {
    private OkHttpClient Code = new Builder().connectTimeout(Bmob.getConnectTimeout(), TimeUnit.SECONDS).readTimeout((long) acknowledge.V, TimeUnit.SECONDS).writeTimeout((long) acknowledge.I, TimeUnit.SECONDS).followRedirects(true).build();

    private static MessageDigest I(String str) {
        try {
            return MessageDigest.getInstance(str);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String Code(String str) {
        return cn.bmob.v3.a.a.This.Code(I("MD5").digest(cn.bmob.v3.datatype.a.This.Code(str, HTTP.UTF_8)));
    }

    public String Code(String str, Map<String, String> map) throws IOException, BmobException {
        FormBody.Builder builder = new FormBody.Builder();
        for (Entry entry : map.entrySet()) {
            builder.add((String) entry.getKey(), (String) entry.getValue());
        }
        Response execute = this.Code.newCall(new Request.Builder().addHeader("x-upyun-api-version ", "2").url(str).post(builder.build()).build()).execute();
        if (execute.isSuccessful()) {
            return execute.body().string();
        }
        throw new BmobException(execute.code(), execute.body().string());
    }

    public String Code(String str, cn.bmob.v3.datatype.a.This thisR) throws IOException, BmobException {
        Map map = thisR.V;
        MultipartBody.Builder type = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Entry entry : map.entrySet()) {
            type.addFormDataPart((String) entry.getKey(), (String) entry.getValue());
        }
        type.addFormDataPart("file", thisR.I, RequestBody.create(null, thisR.Code));
        Response execute = this.Code.newCall(new Request.Builder().addHeader("x-upyun-api-version ", "2").url(str).post(type.build()).build()).execute();
        if (execute.isSuccessful()) {
            return execute.body().string();
        }
        throw new BmobException(execute.code(), execute.body().string());
    }

    public Response V(String str) throws IOException, BmobException {
        Response execute = this.Code.newCall(new Request.Builder().url(str).build()).execute();
        if (execute.isSuccessful()) {
            return execute;
        }
        throw new BmobException(execute.code(), execute.body().string());
    }
}
