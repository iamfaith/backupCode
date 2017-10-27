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
import naco_siren.github.a1point3acres.contents.ThreadComment;
import naco_siren.github.http_utils.HttpErrorType;
import naco_siren.github.http_utils.HttpGET;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.SM;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class POSTReply {
    private final String LOG_TAG = POSTReply.class.getSimpleName();
    private String mCookie;
    private HttpErrorType mErrorCode;
    private String mFormHash;
    private String mReplyMessage;
    private String mResult;
    private int mSubforumID;
    private ThreadComment mThreadComment;
    private String mThreadID;

    public POSTReply(String cookie, int subforumID, String threadID, String threadFormHash, String replyMessage, ThreadComment threadComment) {
        this.mCookie = cookie;
        this.mSubforumID = subforumID;
        this.mThreadID = threadID;
        this.mFormHash = threadFormHash;
        this.mReplyMessage = replyMessage;
        this.mThreadComment = threadComment;
    }

    public HttpErrorType execute() {
        UnknownHostException e;
        SocketTimeoutException e2;
        Exception e3;
        try {
            String spec;
            HttpURLConnection urlConnection;
            String data;
            URL url;
            URL url2;
            if (this.mThreadComment == null) {
                spec = "http://www.1point3acres.com/bbs/forum.php?mod=post&action=reply&fid=" + this.mSubforumID + "&tid=" + this.mThreadID + "&extra=page%3D1%26filter%3Dsortid%26sortid%3D192&replysubmit=yes&infloat=yes&handlekey=fastpost";
                url = new URL(spec);
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod(HttpPost.METHOD_NAME);
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setReadTimeout(4000);
                    urlConnection.setConnectTimeout(4000);
                    urlConnection.setRequestProperty("Accept", "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                    urlConnection.setRequestProperty(HTTP.CONN_DIRECTIVE, "keep-alive");
                    urlConnection.setRequestProperty(HTTP.CONTENT_TYPE, URLEncodedUtils.CONTENT_TYPE);
                    urlConnection.setRequestProperty(HTTP.USER_AGENT, "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
                    urlConnection.setRequestProperty("Referer", "http://www.1point3acres.com/bbs/forum.php?mod=viewthread&tid=" + this.mThreadID + "&extra=page%3D1%26filter%3Dsortid%3D192");
                    urlConnection.setRequestProperty(HTTP.TARGET_HOST, "www.1point3acres.com");
                    urlConnection.setRequestProperty(SM.COOKIE, this.mCookie);
                    data = "message=" + URLEncoder.encode(this.mReplyMessage, HTTP.UTF_8) + "&formhash=" + this.mFormHash + "&usersig=1&subject=";
                    Log.d(this.LOG_TAG, "Reply: POST data:\n" + data);
                    url2 = url;
                } catch (UnknownHostException e4) {
                    e = e4;
                    url2 = url;
                    e.printStackTrace();
                    Log.e(this.LOG_TAG, "Unknown host!");
                    this.mErrorCode = HttpErrorType.ERROR_NETWORK;
                    return this.mErrorCode;
                } catch (SocketTimeoutException e5) {
                    e2 = e5;
                    url2 = url;
                    e2.printStackTrace();
                    Log.e(this.LOG_TAG, "Socket Timeout!");
                    this.mErrorCode = HttpErrorType.ERROR_TIMEOUT;
                    return this.mErrorCode;
                } catch (Exception e6) {
                    e3 = e6;
                    url2 = url;
                    e3.printStackTrace();
                    Log.e(this.LOG_TAG, "UNKNOWN EXCEPTION ***");
                    this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
                    return this.mErrorCode;
                }
            }
            Element postformFORM = Jsoup.parse(new HttpGET(this.mThreadComment.getFastReplyHref(), this.mCookie, HTTP.UTF_8).getHTML()).body().getElementById("postform");
            String noticeauthor = postformFORM.getElementsByAttributeValue("name", "noticeauthor").first().attr("value");
            String noticetrimstr = postformFORM.getElementsByAttributeValue("name", "noticetrimstr").first().attr("value");
            String noticeauthormsg = postformFORM.getElementsByAttributeValue("name", "noticeauthormsg").first().attr("value");
            spec = "http://www.1point3acres.com/bbs/forum.php?mod=post&action=reply&fid=" + this.mSubforumID + "&tid=" + this.mThreadID + "&extra=&replysubmit=yes&infloat=yes&inajax=1";
            url = new URL(spec);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(HttpPost.METHOD_NAME);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(4000);
            urlConnection.setConnectTimeout(4000);
            urlConnection.setRequestProperty("Accept", "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            urlConnection.setRequestProperty(HTTP.CONN_DIRECTIVE, "keep-alive");
            urlConnection.setRequestProperty(HTTP.CONTENT_TYPE, URLEncodedUtils.CONTENT_TYPE);
            urlConnection.setRequestProperty(HTTP.USER_AGENT, "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
            urlConnection.setRequestProperty("Referer", "http://www.1point3acres.com/bbs/forum.php?mod=viewthread&tid=" + this.mThreadID + "&extra=page%3D1%26filter%3Dsortid%3D192");
            urlConnection.setRequestProperty(HTTP.TARGET_HOST, "www.1point3acres.com");
            urlConnection.setRequestProperty(SM.COOKIE, this.mCookie);
            data = "message=" + URLEncoder.encode(this.mReplyMessage, HTTP.UTF_8) + "&formhash=" + this.mFormHash + "&usersig=1&subject=&reppid=" + this.mThreadComment.getCommentID() + "&reppost=" + this.mThreadComment.getCommentID() + "&noticeauthor=" + URLEncoder.encode(noticeauthor, HTTP.UTF_8) + "&noticetrimstr=" + URLEncoder.encode(noticetrimstr, HTTP.UTF_8) + "&noticeauthormsg=" + URLEncoder.encode(noticeauthormsg, HTTP.UTF_8) + "&host=www.1point3acres.com";
            Log.d(this.LOG_TAG, "Reply: POST data:\n" + data);
            url2 = url;
            if (spec == null || url == null || urlConnection == null || data == null) {
                Log.e(this.LOG_TAG, "UNKNOWN EXCEPTION *** spec/url/urlConnection/dada is null");
                this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
                return this.mErrorCode;
            }
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            Log.v(this.LOG_TAG, "Reply: Getting response code...");
            int responseCode = urlConnection.getResponseCode();
            Log.v(this.LOG_TAG, "Reply: Response code " + responseCode);
            if (responseCode == 200 || responseCode == 301) {
                Log.v(this.LOG_TAG, "Getting cookies...");
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
                    Log.v(this.LOG_TAG, "Reply success! " + this.mReplyMessage);
                    this.mErrorCode = HttpErrorType.SUCCESS;
                    return this.mErrorCode;
                }
                Log.e(this.LOG_TAG, "Cannot find \"Set-Cookie\"!");
                this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
                return this.mErrorCode;
            }
            Log.e(this.LOG_TAG, "UrlConnection ResponseCode strange: " + responseCode);
            this.mErrorCode = HttpErrorType.ERROR_CONNECTION;
            return this.mErrorCode;
        } catch (UnknownHostException e7) {
            e = e7;
            e.printStackTrace();
            Log.e(this.LOG_TAG, "Unknown host!");
            this.mErrorCode = HttpErrorType.ERROR_NETWORK;
            return this.mErrorCode;
        } catch (SocketTimeoutException e8) {
            e2 = e8;
            e2.printStackTrace();
            Log.e(this.LOG_TAG, "Socket Timeout!");
            this.mErrorCode = HttpErrorType.ERROR_TIMEOUT;
            return this.mErrorCode;
        } catch (Exception e9) {
            e3 = e9;
            e3.printStackTrace();
            Log.e(this.LOG_TAG, "UNKNOWN EXCEPTION ***");
            this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
            return this.mErrorCode;
        }
    }
}
