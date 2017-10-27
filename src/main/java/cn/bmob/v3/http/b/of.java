package cn.bmob.v3.http.b;

import android.text.TextUtils;
import android.util.Log;
import cn.bmob.v3.BmobConstants;
import cn.bmob.v3.datatype.a.This;
import cn.bmob.v3.http.I;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/* compiled from: ResponseInterceptor */
public final class of implements Interceptor {
    public final Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String httpUrl = request.url().toString();
        Response proceed = chain.proceed(request);
        String I = This.I(proceed.body().bytes());
        String str = "";
        if (httpUrl.equals("http://open.bmob.cn/8/secret")) {
            Object header = proceed.header("Response-Id", "");
            if (TextUtils.isEmpty(header)) {
                Log.e(BmobConstants.TAG, "responseId is null");
            } else {
                str = I.V(header, I);
            }
        } else {
            str = I.V(I);
        }
        return proceed.newBuilder().body(ResponseBody.create(cn.bmob.v3.http.This.Code, str)).build();
    }
}
