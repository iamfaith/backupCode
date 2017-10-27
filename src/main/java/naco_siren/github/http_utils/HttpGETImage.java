package naco_siren.github.http_utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.SM;
import org.apache.http.protocol.HTTP;

public class HttpGETImage {
    private final String LOG_TAG = HttpGETImage.class.getSimpleName();
    private final String mCookie;
    private HttpErrorType mErrorCode;
    private Bitmap mImage;
    private boolean mIsExecuted;
    private final String mTarget;

    public HttpGETImage(String url, String cookie) {
        this.mTarget = url;
        this.mCookie = cookie;
        this.mIsExecuted = false;
        this.mImage = null;
    }

    public HttpErrorType Execute() {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(this.mTarget).openConnection();
            urlConnection.setRequestMethod(HttpGet.METHOD_NAME);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(4000);
            urlConnection.setConnectTimeout(4000);
            urlConnection.setRequestProperty("Accept", "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,mImage/webp,*/*;q=0.8");
            urlConnection.setRequestProperty(HTTP.CONN_DIRECTIVE, "keep-alive");
            urlConnection.setRequestProperty(HTTP.CONTENT_TYPE, URLEncodedUtils.CONTENT_TYPE);
            urlConnection.setRequestProperty(HTTP.USER_AGENT, "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
            urlConnection.setRequestProperty(SM.COOKIE, this.mCookie);
            if (urlConnection.getResponseCode() == 200) {
                InputStream is = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, HTTP.UTF_8);
                this.mImage = BitmapFactory.decodeStream(is);
                is.close();
                this.mErrorCode = HttpErrorType.SUCCESS;
                this.mIsExecuted = true;
                return this.mErrorCode;
            }
            Log.e(this.LOG_TAG, "Failed to connect:\n" + this.mTarget);
            this.mErrorCode = HttpErrorType.ERROR_CONNECTION;
            this.mIsExecuted = true;
            return this.mErrorCode;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.LOG_TAG, "UNKNOWN ERROR connecting: \n" + this.mTarget);
            this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
            this.mIsExecuted = true;
            return this.mErrorCode;
        }
    }

    public Bitmap getImage() {
        if (this.mIsExecuted) {
            return this.mImage;
        }
        Log.e(this.LOG_TAG, "HttpGETImage not executed, target:\n" + this.mTarget);
        return null;
    }

    public HttpErrorType getErrorCode() {
        return this.mErrorCode;
    }
}
