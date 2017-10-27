package naco_siren.github.a1point3acres.http_actions;

import android.util.Log;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import naco_siren.github.http_utils.HttpErrorType;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.SM;
import org.apache.http.protocol.HTTP;

public class POSTNewThread {
    private static final String LOG_TAG = POSTNewThread.class.getSimpleName();
    private String mCookie;
    private HttpErrorType mErrorCode;
    private String mFormHash;
    private String mPostBody;
    private String mPostTitle;
    private String mResult;
    private int mSubforumID;

    public POSTNewThread(String cookie, int subforumID, String formHash, String postTitle, String postMessage) {
        this.mCookie = cookie;
        this.mSubforumID = subforumID;
        this.mFormHash = formHash;
        this.mPostTitle = postTitle;
        this.mPostBody = postMessage;
    }

    public HttpErrorType execute() {
        URL url;
        UnknownHostException e;
        SocketTimeoutException e2;
        Exception e3;
        try {
            String spec = "http://www.1point3acres.com/bbs/forum.php?mod=post&action=newthread&fid=" + this.mSubforumID + "&topicsubmit=yes&infloat=yes&handlekey=fastnewpost";
            URL url2 = new URL(spec);
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url2.openConnection();
                urlConnection.setRequestMethod(HttpPost.METHOD_NAME);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setReadTimeout(4000);
                urlConnection.setConnectTimeout(4000);
                urlConnection.setRequestProperty("Accept", "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                urlConnection.setRequestProperty(HTTP.CONN_DIRECTIVE, "keep-alive");
                urlConnection.setRequestProperty(HTTP.CONTENT_TYPE, URLEncodedUtils.CONTENT_TYPE);
                urlConnection.setRequestProperty(HTTP.USER_AGENT, "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
                urlConnection.setRequestProperty("Referer", "http://www.1point3acres.com/bbs/forum-" + this.mSubforumID + "-1.html");
                urlConnection.setRequestProperty(HTTP.TARGET_HOST, "www.1point3acres.com");
                urlConnection.setRequestProperty(SM.COOKIE, this.mCookie);
                String data = "message=" + URLEncoder.encode(this.mPostBody, HTTP.UTF_8) + "&formhash=" + this.mFormHash + "&usersig=1&topicsubmit=topicsubmit&subject=" + URLEncoder.encode(this.mPostTitle, HTTP.UTF_8);
                Log.d(LOG_TAG, "Reply: POST data:\n" + data);
                if (spec == null || url2 == null || urlConnection == null || data == null) {
                    Log.e(LOG_TAG, "UNKNOWN EXCEPTION *** spec/url/urlConnection/dada is null");
                    this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
                    url = url2;
                    return this.mErrorCode;
                }
                OutputStream os = urlConnection.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                Log.v(LOG_TAG, "Reply: Getting response code...");
                int responseCode = urlConnection.getResponseCode();
                Log.v(LOG_TAG, "Reply: Response code " + responseCode);
                if (responseCode == 200 || responseCode == HttpStatus.SC_MOVED_PERMANENTLY) {
                    Log.v(LOG_TAG, "Getting cookies...");
                    Map<String, List<String>> map = urlConnection.getHeaderFields();
                    boolean isSuccess = false;
                    Iterator iterator = map.keySet().iterator();
                    while (!isSuccess && iterator.hasNext()) {
                        String key = (String) iterator.next();
                        if (key != null && key.equals(SM.SET_COOKIE)) {
                            isSuccess = false;
                            if (((List) map.get(key)).size() >= 5) {
                                isSuccess = true;
                            }
                        }
                    }
                    if (isSuccess) {
                        Log.v(LOG_TAG, "Post new thread success! ");
                        this.mErrorCode = HttpErrorType.SUCCESS;
                        url = url2;
                        return this.mErrorCode;
                    }
                    Log.e(LOG_TAG, "Cannot find \"Set-Cookie\"!");
                    this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
                    url = url2;
                    return this.mErrorCode;
                }
                Log.e(LOG_TAG, "UrlConnection ResponseCode strange: " + responseCode);
                this.mErrorCode = HttpErrorType.ERROR_CONNECTION;
                url = url2;
                return this.mErrorCode;
            } catch (UnknownHostException e4) {
                e = e4;
                url = url2;
                e.printStackTrace();
                Log.e(LOG_TAG, "Unknown host!");
                this.mErrorCode = HttpErrorType.ERROR_NETWORK;
                return this.mErrorCode;
            } catch (SocketTimeoutException e5) {
                e2 = e5;
                url = url2;
                e2.printStackTrace();
                Log.e(LOG_TAG, "Socket Timeout!");
                this.mErrorCode = HttpErrorType.ERROR_TIMEOUT;
                return this.mErrorCode;
            } catch (Exception e6) {
                e3 = e6;
                url = url2;
                e3.printStackTrace();
                Log.e(LOG_TAG, "UNKNOWN EXCEPTION ***");
                this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
                return this.mErrorCode;
            }
        } catch (UnknownHostException e7) {
            e = e7;
            e.printStackTrace();
            Log.e(LOG_TAG, "Unknown host!");
            this.mErrorCode = HttpErrorType.ERROR_NETWORK;
            return this.mErrorCode;
        } catch (SocketTimeoutException e8) {
            e2 = e8;
            e2.printStackTrace();
            Log.e(LOG_TAG, "Socket Timeout!");
            this.mErrorCode = HttpErrorType.ERROR_TIMEOUT;
            return this.mErrorCode;
        } catch (Exception e9) {
            e3 = e9;
            e3.printStackTrace();
            Log.e(LOG_TAG, "UNKNOWN EXCEPTION ***");
            this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
            return this.mErrorCode;
        }
    }
}
