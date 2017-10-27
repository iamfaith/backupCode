package cn.bmob.v3.http.b;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

/* compiled from: GzipRequestInterceptor */
public final class This implements Interceptor {
    public final Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (request.body() == null || request.header("Accept-Encoding") != null) {
            return chain.proceed(request);
        }
        Builder header = request.newBuilder().header("Accept-Encoding", "gzip");
        String method = request.method();
        final RequestBody body = request.body();
        return chain.proceed(header.method(method, new RequestBody(this) {
            public final MediaType contentType() {
                return body.contentType();
            }

            public final long contentLength() {
                return -1;
            }

            public final void writeTo(BufferedSink sink) throws IOException {
                BufferedSink buffer = Okio.buffer(new GzipSink(sink));
                body.writeTo(buffer);
                buffer.close();
            }
        }).build());
    }
}
