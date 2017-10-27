package cn.bmob.v3.http.b;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Platform;
import okhttp3.internal.http.HttpEngine;
import okio.Buffer;
import okio.BufferedSource;
import org.apache.http.protocol.HTTP;

/* compiled from: HttpLoggingInterceptor */
public final class thing implements Interceptor {
    private static final Charset Code = Charset.forName(HTTP.UTF_8);
    private volatile int I;
    private final thing V;

    /* compiled from: HttpLoggingInterceptor */
    public enum This {
        ;

        static {
            Code = 1;
            Z = 2;
            V = 3;
            I = 4;
            B = new int[]{1, 2, 3, 4};
        }
    }

    /* compiled from: HttpLoggingInterceptor */
    public interface thing {
        public static final thing Code = new thing() {
            public final void Code(String str) {
                Platform.get().log(4, str, null);
            }
        };

        void Code(String str);
    }

    public thing() {
        this(thing.Code);
    }

    private thing(thing cn_bmob_v3_http_b_thing_thing) {
        this.I = This.Code;
        this.V = cn_bmob_v3_http_b_thing_thing;
    }

    public final thing Code(int i) {
        if (i == 0) {
            throw new NullPointerException("level == null. Use Level.NONE instead.");
        }
        this.I = i;
        return this;
    }

    public final Response intercept(Chain chain) throws IOException {
        int i = this.I;
        Request request = chain.request();
        if (i == This.Code) {
            return chain.proceed(request);
        }
        String str;
        Object obj = i == This.I ? 1 : null;
        Object obj2 = (obj != null || i == This.V) ? 1 : null;
        RequestBody body = request.body();
        Object obj3 = body != null ? 1 : null;
        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        StringBuilder append = new StringBuilder("--> ").append(request.method()).append(' ').append(request.url()).append(' ');
        if (protocol == Protocol.HTTP_1_0) {
            str = "HTTP/1.0";
        } else {
            str = "HTTP/1.1";
        }
        str = append.append(str).toString();
        if (obj2 == null && obj3 != null) {
            str = str + " (" + body.contentLength() + "-byte body)";
        }
        this.V.Code(str);
        if (obj2 != null) {
            if (obj3 != null) {
                if (body.contentType() != null) {
                    this.V.Code("Content-Type: " + body.contentType());
                }
                if (body.contentLength() != -1) {
                    this.V.Code("Content-Length: " + body.contentLength());
                }
            }
            Headers headers = request.headers();
            int size = headers.size();
            for (int i2 = 0; i2 < size; i2++) {
                String name = headers.name(i2);
                if (!(HTTP.CONTENT_TYPE.equalsIgnoreCase(name) || HTTP.CONTENT_LEN.equalsIgnoreCase(name))) {
                    this.V.Code(name + ": " + headers.value(i2));
                }
            }
            if (obj == null || obj3 == null) {
                this.V.Code("--> END " + request.method());
            } else if (Code(request.headers())) {
                this.V.Code("--> END " + request.method() + " (encoded body omitted)");
            } else {
                Object buffer = new Buffer();
                body.writeTo(buffer);
                Charset charset = Code;
                MediaType contentType = body.contentType();
                if (contentType != null) {
                    charset = contentType.charset(Code);
                }
                this.V.Code("");
                this.V.Code(buffer.readString(charset));
                this.V.Code("--> END " + request.method() + " (" + body.contentLength() + "-byte body)");
            }
        }
        long nanoTime = System.nanoTime();
        Response proceed = chain.proceed(request);
        long toMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTime);
        ResponseBody body2 = proceed.body();
        long contentLength = body2.contentLength();
        this.V.Code("<-- " + proceed.code() + ' ' + proceed.message() + ' ' + proceed.request().url() + " (" + toMillis + "ms" + (obj2 == null ? ", " + (contentLength != -1 ? contentLength + "-byte" : "unknown-length") + " body" : "") + ')');
        if (obj2 != null) {
            Headers headers2 = proceed.headers();
            int size2 = headers2.size();
            for (int i3 = 0; i3 < size2; i3++) {
                this.V.Code(headers2.name(i3) + ": " + headers2.value(i3));
            }
            if (obj == null || !HttpEngine.hasBody(proceed)) {
                this.V.Code("<-- END HTTP");
            } else if (Code(proceed.headers())) {
                this.V.Code("<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = body2.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer2 = source.buffer();
                Charset charset2 = Code;
                MediaType contentType2 = body2.contentType();
                if (contentType2 != null) {
                    charset2 = contentType2.charset(Code);
                }
                if (contentLength != 0) {
                    this.V.Code("");
                    this.V.Code(buffer2.clone().readString(charset2));
                }
                this.V.Code("<-- END HTTP (" + buffer2.size() + "-byte body)");
            }
        }
        return proceed;
    }

    private static boolean Code(Headers headers) {
        String str = headers.get("Content-Encoding");
        return (str == null || str.equalsIgnoreCase(HTTP.IDENTITY_CODING)) ? false : true;
    }
}
