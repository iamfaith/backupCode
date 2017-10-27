package naco_siren.github.a1point3acres.http_actions;

import android.os.Bundle;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import naco_siren.github.http_utils.HttpErrorType;
import naco_siren.github.http_utils.HttpGET;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.SM;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class POSTLogin {
    private final String LOG_TAG = POSTLogin.class.getSimpleName();
    private String mCookie;
    private final String mEmail;
    private HttpErrorType mErrorCode;
    private final String mPassword;
    private String mResult;
    private String mUserID;
    private String mUserName;
    private String mUserRank;
    private String mUserRankHint;

    public POSTLogin(String email, String password) {
        this.mEmail = email;
        this.mPassword = password;
    }

    public HttpErrorType execute() {
        this.mCookie = null;
        String formhashValue = "";
        try {
            Document doc = Jsoup.connect("http://www.1point3acres.com/bbs/member.php?mod=logging&action=login&mobile=2").get();
            Log.v(this.LOG_TAG, "Jsoup connected and parsed login-webpage.");
            formhashValue = doc.getElementById("formhash").attr("value");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(this.LOG_TAG, "Get Formhash value:\n" + formhashValue);
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://www.1point3acres.com/bbs/member.php?mod=logging&action=login&loginsubmit=yes&loginhash=LplDL&mobile=2&handlekey=loginform&inajax=1").openConnection();
            urlConnection.setRequestMethod(HttpPost.METHOD_NAME);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(4000);
            urlConnection.setConnectTimeout(4000);
            urlConnection.setRequestProperty("Accept", "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            urlConnection.setRequestProperty(HTTP.CONN_DIRECTIVE, "keep-alive");
            urlConnection.setRequestProperty(HTTP.CONTENT_TYPE, URLEncodedUtils.CONTENT_TYPE);
            urlConnection.setRequestProperty(HTTP.USER_AGENT, "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36");
            String data = "username=" + URLEncoder.encode(this.mEmail, HTTP.UTF_8) + "&password=" + URLEncoder.encode(this.mPassword, HTTP.UTF_8) + "&cookietime=2592000&quickforward=yes&handlekey=ls&referer=" + URLEncoder.encode("http://www.1point3acres.com/bbs/?mobile=2", HTTP.UTF_8) + "&formhash=" + formhashValue + "&fastloginfield=username&questionid=0";
            Log.d(this.LOG_TAG, "POST data:\n" + data);
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            Log.v(this.LOG_TAG, "Getting response code...");
            if (urlConnection.getResponseCode() == 200) {
                Log.v(this.LOG_TAG, "Response code 200.");
                InputStream is = urlConnection.getInputStream();
                Reader inputStreamReader = new InputStreamReader(is, "GBK");
                BufferedReader bufferReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String str = "";
                while (true) {
                    str = bufferReader.readLine();
                    if (str == null) {
                        break;
                    }
                    stringBuilder.append(str + "\n");
                }
                this.mResult = stringBuilder.toString();
                is.close();
                inputStreamReader.close();
                bufferReader.close();
                Log.v(this.LOG_TAG, "Finished reading Html from OutputStream.");
                if (this.mResult.contains("欢迎您回来，")) {
                    String[] userRankStrings = this.mResult.split("欢迎您回来，")[1].split("-");
                    this.mUserRank = userRankStrings[0];
                    if (userRankStrings.length > 1) {
                        this.mUserRankHint = userRankStrings[1];
                    }
                    Log.d(this.LOG_TAG, "UserRank:\n" + this.mUserRank);
                    Log.v(this.LOG_TAG, "Getting cookies...");
                    Map<String, List<String>> map = urlConnection.getHeaderFields();
                    boolean isSuccess = false;
                    for (String key : map.keySet()) {
                        if (key != null && key.equals(SM.SET_COOKIE)) {
                            isSuccess = true;
                            List<String> list = (List) map.get(key);
                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < list.size(); i++) {
                                String str2 = (String) list.get(i);
                                if (i == list.size() - 1) {
                                    builder.append(str2.split(";")[0]);
                                } else {
                                    builder.append(str2.split(";")[0] + "; ");
                                }
                            }
                            this.mCookie = builder.toString();
                            Log.d(HttpPost.METHOD_NAME, "Cookie:\n" + this.mCookie);
                            if (this.mCookie.contains("4Oaf_61d6_auth=")) {
                                Element targetA = new HttpGET("http://www.1point3acres.com/bbs/?mobile=2", this.mCookie).getJsoupDocument().select("div.footer").last().select("div").first().select("a").first();
                                this.mUserName = targetA.text();
                                Log.d(this.LOG_TAG, "UserName:\n" + this.mUserName);
                                this.mUserID = targetA.attr("href").split("uid=")[1].split("&")[0];
                                Log.d(this.LOG_TAG, "UserID:\n" + this.mUserID);
                            } else {
                                this.mErrorCode = HttpErrorType.ERROR_INCORRECT_PASSWORD;
                                return this.mErrorCode;
                            }
                        }
                    }
                    if (isSuccess) {
                        Log.v(this.LOG_TAG, "Cookie ready.");
                        this.mErrorCode = HttpErrorType.SUCCESS;
                        return this.mErrorCode;
                    }
                    Log.e(this.LOG_TAG, "Cannot find \"Set-Cookie\"!");
                    this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
                    return this.mErrorCode;
                }
                this.mErrorCode = HttpErrorType.ERROR_INCORRECT_PASSWORD;
                return this.mErrorCode;
            }
            Log.e(this.LOG_TAG, "UrlConnection ResponseCode not 200!");
            this.mErrorCode = HttpErrorType.ERROR_CONNECTION;
            return this.mErrorCode;
        } catch (UnknownHostException e2) {
            e2.printStackTrace();
            Log.e(this.LOG_TAG, "Unknown host!");
            this.mErrorCode = HttpErrorType.ERROR_NETWORK;
            return this.mErrorCode;
        } catch (SocketTimeoutException e3) {
            e3.printStackTrace();
            Log.e(this.LOG_TAG, "Socket Timeout!");
            this.mErrorCode = HttpErrorType.ERROR_TIMEOUT;
            return this.mErrorCode;
        } catch (Exception e4) {
            e4.printStackTrace();
            Log.e(this.LOG_TAG, "UNKNOWN EXCEPTION ***");
            this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
            return this.mErrorCode;
        }
    }

    public Bundle getLoginInfo() {
        if (this.mErrorCode != HttpErrorType.SUCCESS) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putString("username", this.mUserName);
        bundle.putString("userrank", this.mUserRank);
        bundle.putString("userid", this.mUserID);
        bundle.putString("cookie", this.mCookie);
        return bundle;
    }
}
