package cn.bmob.v3.socketio;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;

/* compiled from: WebSocketClient */
public final class From {
    private HandlerThread B;
    private Handler C;
    private URI Code;
    private boolean D;
    private I F;
    private Socket I;
    private final Object L = new Object();
    private List<BasicNameValuePair> S;
    private This V;
    private Thread Z;

    /* compiled from: WebSocketClient */
    public interface This {
        void Code();

        void Code(int i, String str);

        void Code(Exception exception);

        void Code(String str);
    }

    static /* synthetic */ String Code(From from, cn.bmob.v3.socketio.I.This thisR) throws IOException {
        int read = thisR.read();
        if (read == -1) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder("");
        while (read != 10) {
            if (read != 13) {
                stringBuilder.append((char) read);
            }
            read = thisR.read();
            if (read == -1) {
                return null;
            }
        }
        return stringBuilder.toString();
    }

    public From(URI uri, This thisR, List<BasicNameValuePair> list) {
        this.Code = uri;
        this.V = thisR;
        this.S = null;
        this.D = false;
        this.F = new I(this);
        this.B = new HandlerThread("websocket-thread");
        this.B.start();
        this.C = new Handler(this.B.getLooper());
    }

    public final This Code() {
        return this.V;
    }

    public final void V() {
        if (this.Z == null || !this.Z.isAlive()) {
            this.Z = new Thread(new Runnable(this) {
                private /* synthetic */ From Code;

                {
                    this.Code = r1;
                }

                public final void run() {
                    try {
                        String str;
                        int port = this.Code.Code.getPort() != -1 ? this.Code.Code.getPort() : (this.Code.Code.getScheme().equals("wss") || this.Code.Code.getScheme().equals("https")) ? 443 : 80;
                        String path = TextUtils.isEmpty(this.Code.Code.getPath()) ? "/" : this.Code.Code.getPath();
                        if (TextUtils.isEmpty(this.Code.Code.getQuery())) {
                            str = path;
                        } else {
                            str = path + "?" + this.Code.Code.getQuery();
                        }
                        URI uri = new URI(this.Code.Code.getScheme().equals("wss") ? "https" : HttpHost.DEFAULT_SCHEME_NAME, "//" + this.Code.Code.getHost(), null);
                        SocketFactory V = (this.Code.Code.getScheme().equals("wss") || this.Code.Code.getScheme().equals("https")) ? From.V(this.Code) : SocketFactory.getDefault();
                        this.Code.I = V.createSocket(this.Code.Code.getHost(), port);
                        PrintWriter printWriter = new PrintWriter(this.Code.I.getOutputStream());
                        String Z = From.Z(this.Code);
                        printWriter.print("GET " + str + " HTTP/1.1\r\n");
                        printWriter.print("Upgrade: websocket\r\n");
                        printWriter.print("Connection: Upgrade\r\n");
                        printWriter.print("Host: " + this.Code.Code.getHost() + "\r\n");
                        printWriter.print("Origin: " + uri.toString() + "\r\n");
                        printWriter.print("Sec-WebSocket-Key: " + Z + "\r\n");
                        printWriter.print("Sec-WebSocket-Version: 13\r\n");
                        if (this.Code.S != null) {
                            for (NameValuePair nameValuePair : this.Code.S) {
                                printWriter.print(String.format("%s: %s\r\n", new Object[]{nameValuePair.getName(), nameValuePair.getValue()}));
                            }
                        }
                        printWriter.print("\r\n");
                        printWriter.flush();
                        cn.bmob.v3.socketio.I.This thisR = new cn.bmob.v3.socketio.I.This(this.Code.I.getInputStream());
                        StatusLine Code = From.Code(this.Code, From.Code(this.Code, thisR));
                        if (Code == null) {
                            throw new HttpException("Received no reply from server.");
                        } else if (Code.getStatusCode() != 101) {
                            throw new HttpResponseException(Code.getStatusCode(), Code.getReasonPhrase());
                        } else {
                            while (true) {
                                Object Code2 = From.Code(this.Code, thisR);
                                if (TextUtils.isEmpty(Code2)) {
                                    this.Code.V.Code();
                                    this.Code.D = true;
                                    this.Code.F.Code(thisR);
                                    return;
                                }
                                Header V2 = BasicLineParser.parseHeader(Code2, new BasicLineParser());
                                if (V2.getName().equals("Sec-WebSocket-Accept")) {
                                    String I = From.V(Z);
                                    if (I == null) {
                                        throw new Exception("SHA-1 algorithm not found");
                                    } else if (!I.equals(V2.getValue())) {
                                        throw new Exception("Invalid Sec-WebSocket-Accept, expected: " + I + ", got: " + V2.getValue());
                                    }
                                }
                            }
                        }
                    } catch (Throwable e) {
                        Log.d("WebSocketClient", "WebSocket EOF!", e);
                        this.Code.V.Code(0, "EOF");
                        this.Code.D = false;
                    } catch (Throwable e2) {
                        Log.d("WebSocketClient", "Websocket SSL error!", e2);
                        this.Code.V.Code(0, "SSL");
                        this.Code.D = false;
                    } catch (Exception e3) {
                        this.Code.V.Code(e3);
                    }
                }
            });
            this.Z.start();
        }
    }

    public final void I() {
        if (this.I != null) {
            this.C.post(new Runnable(this) {
                private /* synthetic */ From Code;

                {
                    this.Code = r1;
                }

                public final void run() {
                    if (this.Code.I != null) {
                        try {
                            this.Code.I.close();
                        } catch (Exception e) {
                            Log.d("WebSocketClient", "Error while disconnecting", e);
                            this.Code.V.Code(e);
                        }
                        this.Code.I = null;
                    }
                    this.Code.D = false;
                }
            });
        }
    }

    public final void Code(String str) {
        Code(this.F.Code(str));
    }

    public final boolean Z() {
        return this.D;
    }

    private static String V(String str) {
        try {
            return Base64.encodeToString(MessageDigest.getInstance("SHA-1").digest((str + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes()), 0).trim();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    final void Code(final byte[] bArr) {
        this.C.post(new Runnable(this) {
            private /* synthetic */ From V;

            public final void run() {
                try {
                    synchronized (this.V.L) {
                        OutputStream outputStream = this.V.I.getOutputStream();
                        outputStream.write(bArr);
                        outputStream.flush();
                    }
                } catch (Exception e) {
                    this.V.V.Code(e);
                }
            }
        });
    }

    static /* synthetic */ SSLSocketFactory V(From from) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext instance = SSLContext.getInstance("TLS");
        instance.init(null, null, null);
        return instance.getSocketFactory();
    }

    static /* synthetic */ String Z(From from) {
        byte[] bArr = new byte[16];
        for (int i = 0; i < 16; i++) {
            bArr[i] = (byte) ((int) (Math.random() * 256.0d));
        }
        return Base64.encodeToString(bArr, 0).trim();
    }

    static /* synthetic */ StatusLine Code(From from, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return BasicLineParser.parseStatusLine(str, new BasicLineParser());
    }
}
