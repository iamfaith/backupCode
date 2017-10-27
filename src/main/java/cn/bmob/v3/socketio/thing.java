package cn.bmob.v3.socketio;

import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

/* compiled from: AsyncHttpClient */
public final class thing {

    /* compiled from: AsyncHttpClient */
    class AnonymousClass1 extends AsyncTask<Void, Void, Void> {
        private /* synthetic */ This Code;
        private /* synthetic */ thing I;
        private /* synthetic */ thing V;

        AnonymousClass1(thing cn_bmob_v3_socketio_thing, This thisR, thing cn_bmob_v3_socketio_thing_thing) {
            this.I = cn_bmob_v3_socketio_thing;
            this.Code = thisR;
            this.V = cn_bmob_v3_socketio_thing_thing;
        }

        protected final /* synthetic */ Object doInBackground(Object[] objArr) {
            return Code();
        }

        private Void Code() {
            AndroidHttpClient newInstance = AndroidHttpClient.newInstance("android-websockets-2.0");
            Object httpPost = new HttpPost(this.Code.Code());
            AnonymousClass1.Code(httpPost, this.Code.I());
            try {
                String Code = new String(thing.Code(newInstance.execute(httpPost).getEntity().getContent()));
                if (this.V != null) {
                    this.V.Code(null, Code);
                }
                newInstance.close();
            } catch (Exception e) {
                if (this.V != null) {
                    this.V.Code(e, null);
                }
                newInstance.close();
            } catch (Throwable th) {
                newInstance.close();
            }
            return null;
        }

        private static void Code(HttpRequest httpRequest, List<BasicNameValuePair> list) {
            if (list != null) {
                for (BasicNameValuePair basicNameValuePair : list) {
                    httpRequest.addHeader(basicNameValuePair.getName(), basicNameValuePair.getValue());
                }
            }
        }
    }

    /* compiled from: AsyncHttpClient */
    public static class This {
        private String Code;
        private List<BasicNameValuePair> I;
        private String V;

        public This(String str) {
            this(str, null);
        }

        private This(String str, String str2) {
            this(str, null, null);
        }

        private This(String str, String str2, List<BasicNameValuePair> list) {
            this.Code = Uri.parse(str).buildUpon().encodedPath("/socket.io/1/").build().toString();
            this.V = str2;
            this.I = null;
        }

        public final String Code() {
            return this.Code;
        }

        public final String V() {
            return this.V;
        }

        public final List<BasicNameValuePair> I() {
            return this.I;
        }
    }

    /* compiled from: AsyncHttpClient */
    public interface thing {
        void Code(Exception exception, String str);
    }

    private static byte[] Code(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        byte[] bArr = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            int read = dataInputStream.read(bArr);
            if (read == -1) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
    }
}
