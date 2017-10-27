package naco_siren.github.http_utils;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.SM;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HttpGET {
    private final String LOG_TAG;
    private String mCharSet;
    private final String mCookie;
    private HttpErrorType mErrorCode;
    private String mHTML;
    private final String mTarget;

    public HttpGET(String url, String cookie) {
        this(url, cookie, HTTP.UTF_8);
    }

    public HttpGET(String url, String cookie, String charSet) {
        this.LOG_TAG = HttpGET.class.getSimpleName();
        this.mTarget = url;
        this.mCookie = cookie;
        this.mCharSet = charSet;
        this.mErrorCode = Init();
        if (this.mErrorCode != HttpErrorType.SUCCESS) {
            this.mHTML = null;
        }
    }

    private HttpErrorType Init() {
        String result = "";
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(this.mTarget).openConnection();
            urlConnection.setRequestMethod(HttpGet.METHOD_NAME);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(8000);
            urlConnection.setConnectTimeout(8000);
            urlConnection.setRequestProperty("Accept", "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            urlConnection.setRequestProperty(HTTP.CONN_DIRECTIVE, "keep-alive");
            urlConnection.setRequestProperty(HTTP.CONTENT_TYPE, URLEncodedUtils.CONTENT_TYPE);
            urlConnection.setRequestProperty(HTTP.USER_AGENT, "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
            urlConnection.setRequestProperty(SM.COOKIE, this.mCookie);
            if (urlConnection.getResponseCode() == 200) {
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), this.mCharSet));
                StringBuilder stringBuilder = new StringBuilder();
                String str = "";
                while (true) {
                    str = bufferReader.readLine();
                    if (str != null) {
                        stringBuilder.append(str + "\n");
                    } else {
                        this.mHTML = stringBuilder.toString();
                        this.mErrorCode = HttpErrorType.SUCCESS;
                        return this.mErrorCode;
                    }
                }
            }
            Log.e(this.LOG_TAG, "Failed to connect:\n" + this.mTarget);
            this.mErrorCode = HttpErrorType.ERROR_CONNECTION;
            return this.mErrorCode;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            Log.e(this.LOG_TAG, "Socket Timeout!");
            this.mErrorCode = HttpErrorType.ERROR_TIMEOUT;
            return this.mErrorCode;
        } catch (InterruptedIOException e2) {
            Log.v(this.LOG_TAG, "HttpGET interrupted!");
            this.mErrorCode = HttpErrorType.ERROR_INTERRUPT;
            return this.mErrorCode;
        } catch (Exception e3) {
            e3.printStackTrace();
            Log.e(this.LOG_TAG, "UNKNOWN EXCEPTION ***");
            this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
            return this.mErrorCode;
        }
    }

    public String getHTML() {
        return this.mHTML;
    }

    public HttpErrorType getErrorCode() {
        return this.mErrorCode;
    }

    public Document getJsoupDocument() {
        if (this.mHTML != null) {
            return Jsoup.parse(this.mHTML);
        }
        return null;
    }
}
