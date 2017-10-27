package naco_siren.github.a1point3acres.activities.main_activity;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobDate;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import naco_siren.github.a1point3acres.R;
import naco_siren.github.a1point3acres.activities.blog_activity.BlogActivity;
import naco_siren.github.a1point3acres.activities.login_activity.LoginActivity;
import naco_siren.github.a1point3acres.activities.settings_activity.SettingsActivity;
import naco_siren.github.a1point3acres.activities.thread_activity.ThreadActivity;
import naco_siren.github.a1point3acres.bmob.user.SignUpBmobUser;
import naco_siren.github.a1point3acres.bmob.user.User;
import naco_siren.github.a1point3acres.contents.BlogInfo;
import naco_siren.github.a1point3acres.contents.BlogInfoAdapter;
import naco_siren.github.a1point3acres.contents.BlogInfoAdapter.BlogInfoViewHolder;
import naco_siren.github.a1point3acres.contents.BlogInfoAdapter.IBlogInfoViewHolderOnClick;
import naco_siren.github.a1point3acres.contents.ThreadInfo;
import naco_siren.github.a1point3acres.contents.ThreadInfoAdapter;
import naco_siren.github.a1point3acres.contents.ThreadInfoAdapter.IThreadInfoViewHolderOnClick;
import naco_siren.github.a1point3acres.contents.ThreadInfoAdapter.ThreadInfoViewHolder;
import naco_siren.github.a1point3acres.contents.ThreadPin;
import naco_siren.github.a1point3acres.contents.ThreadStatus;
import naco_siren.github.a1point3acres.html_parsers.ParseBlogInfoList;
import naco_siren.github.a1point3acres.html_parsers.ParseThreadInfoList;
import naco_siren.github.a1point3acres.http_actions.GETUserAvatar;
import naco_siren.github.a1point3acres.http_actions.POSTLogin;
import naco_siren.github.a1point3acres.http_actions.POSTNewThread;
import naco_siren.github.a1point3acres.http_actions.UserAvatarUtils.AvatarSize;
import naco_siren.github.http_utils.HttpErrorType;
import naco_siren.github.http_utils.HttpGET;
import naco_siren.github.ui_utils.EndlessRecyclerOnScrollListener;
import org.apache.http.protocol.HTTP;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, OnItemSelectedListener, IThreadInfoViewHolderOnClick, IBlogInfoViewHolderOnClick {
    static final int REQUEST_BLOG = 4;
    static final int REQUEST_LOGIN = 1;
    static final int REQUEST_LOGOUT = 2;
    static final int REQUEST_SETTINGS = 0;
    static final int REQUEST_THREAD = 3;
    private final int AUTO_LOGIN_MAX_RETRY_TIMES = 3;
    private final long DOUBLE_CLICK_INTERVAL = 1350;
    private final int FETCH_BLOGINFOS_MAX_RETRY_TIMES = 2;
    private final int FETCH_SUBFORUM_THREADS_MAX_RETRY_TIMES = 2;
    private final int GET_AVATAR_MAX_RETRY_TIMES = 1;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    public final int MAX_LOGIN_WAIVING_SPAN = 14;
    private final boolean OUTPUT_DEBUG_INFO = false;
    public final int POST_NEW_THREAD_TASK_ALREADY_RUNNING = 1;
    public final int RELOAD_FAILURE_TASK_ALREADY_RUNNING = 1;
    private SharedPreferences mAccountPref;
    private AutoLoginTask mAutoLoginTask;
    private Bitmap mAvatar;
    private BlogInfoAdapter mBlogInfoAdapter;
    private ArrayList<BlogInfo> mBlogInfos;
    private BlogInfo mBrowsingBlogInfo;
    private ThreadInfo mBrowsingThreadInfo;
    private int mContentPageCount;
    private String mCookie;
    private CoordinatorLayout mCoordinatorLayout;
    private String mEmail;
    private long mExitTime = 0;
    private FetchBlogInfosTask mFetchBlogInfosTask;
    private FetchSubforumThreadInfosTask mFetchSubforumThreadInfosTask;
    private FloatingActionButton mFloatingActionButton;
    private FloatingActionButtonClickEvent mFloatingActionButtonClickEvent;
    private String mFormHash;
    private GetUserAvatarTask mGetUserAvatarTask;
    private boolean mIsFetchingBlogInfos;
    private boolean mIsFetchingSubforumThreadInfos;
    private boolean mIsGettingUserAvatar;
    private boolean mIsGuest;
    private boolean mIsLoadingUserAvatarFromCache;
    private boolean mIsPostingNewThread;
    private LinearLayoutManager mLinearLayoutLayoutManager;
    private LoadUserAvatarFromCacheTask mLoadUserAvatarFromCacheTask;
    private String mPassword;
    private PostNewThreadTask mPostNewThreadTask;
    private RecyclerView mRecyclerView;
    private int mSubforumGroupIndex;
    private int mSubforumID;
    private int[] mSubforumIDs;
    private int mSubforumIndex;
    private int mSubforumParsedPageCount;
    private String mSubforumTitle;
    private String[] mSubforumTitles;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ThreadInfoAdapter mThreadInfoAdapter;
    private int mThreadInfoIndex;
    private ArrayList<ThreadInfo> mThreadInfos;
    private Toast mToast;
    private Toolbar mToolbar;
    private Spinner mToolbarSpinner;
    private String mUserID;
    private CircularImageView mUserImageView;
    private UserLoginStatus mUserLoginStatus;
    private String mUserName;
    private TextView mUserNameTextView;
    private String mUserRank;
    private TextView mUserRankTextView;

    public class AutoLoginTask extends AsyncTask<Void, Void, Boolean> {
        private String mCookie;
        private String mEmail;
        private HttpErrorType mLoginErrorCode;
        private String mPassword;
        private String mUserID;
        private String mUserName;
        private String mUserRank;

        AutoLoginTask(String email, String password) {
            this.mEmail = email;
            this.mPassword = password;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(MainActivity.this.LOG_TAG, "AutoLoginTask instantiated.");
        }

        protected Boolean doInBackground(Void... params) {
            int i = 0;
            while (i < 3) {
                POSTLogin httpPOSTLogin = new POSTLogin(this.mEmail, this.mPassword);
                Log.v(MainActivity.this.LOG_TAG, "Executing POSTLogin @ UserID: " + this.mUserID + ", trial " + i);
                this.mLoginErrorCode = httpPOSTLogin.execute();
                if (this.mLoginErrorCode == HttpErrorType.SUCCESS) {
                    Bundle bundle = httpPOSTLogin.getLoginInfo();
                    this.mUserName = bundle.getString("username");
                    this.mUserRank = bundle.getString("userrank");
                    this.mUserID = bundle.getString("userid");
                    this.mCookie = bundle.getString("cookie");
                    return Boolean.valueOf(true);
                } else if (this.mLoginErrorCode == HttpErrorType.ERROR_INCORRECT_PASSWORD) {
                    Log.e(MainActivity.this.LOG_TAG, "INCORRECT PASSWORD: " + this.mLoginErrorCode + ", POSTLogin @ UserID: " + this.mUserID + ", trial " + i);
                    return Boolean.valueOf(false);
                } else {
                    Log.e(MainActivity.this.LOG_TAG, "Error code: " + this.mLoginErrorCode + ", POSTLogin @ UserID: " + this.mUserID + ", trial " + i);
                    i++;
                }
            }
            return Boolean.valueOf(false);
        }

        protected void onPostExecute(Boolean success) {
            MainActivity.this.mAutoLoginTask = null;
            MainActivity.this.showProgress(false);
            if (success.booleanValue()) {
                Log.v(MainActivity.this.LOG_TAG, "Successfully logged in automatically");
                MainActivity.this.mUserLoginStatus = UserLoginStatus.OLD_USER_ONLINE;
                Editor editor = MainActivity.this.mAccountPref.edit();
                editor.putBoolean(MainActivity.this.getString(R.string.state_key_user_is_guest), true);
                editor.putString(MainActivity.this.getString(R.string.account_key_email), this.mEmail);
                editor.putString(MainActivity.this.getString(R.string.account_key_password), this.mPassword);
                editor.putString(MainActivity.this.getString(R.string.account_key_cookie), this.mCookie);
                editor.putString(MainActivity.this.getString(R.string.account_key_user_name), this.mUserName);
                editor.putString(MainActivity.this.getString(R.string.account_key_user_id), this.mUserID);
                editor.putString(MainActivity.this.getString(R.string.account_key_user_rank), this.mUserRank);
                Calendar calendar = Calendar.getInstance();
                editor.putInt(MainActivity.this.getString(R.string.account_key_last_login_year), calendar.get(1));
                editor.putInt(MainActivity.this.getString(R.string.account_key_last_login_month), calendar.get(2));
                editor.putInt(MainActivity.this.getString(R.string.account_key_last_login_day), calendar.get(5));
                editor.commit();
                User user = new User();
                user.setUsername(this.mUserName);
                user.setPassword(this.mPassword);
                user.setUserRank(this.mUserRank);
                user.setUserId(this.mUserID);
                user.setPwd(this.mPassword);
                user.setLastLoginDate(new BmobDate(new Date()));
                boolean signUpSuccess = new SignUpBmobUser(user).execute();
                MainActivity.this.mUserName = this.mUserName;
                MainActivity.this.mPassword = this.mPassword;
                MainActivity.this.mCookie = this.mCookie;
                MainActivity.this.mUserID = this.mUserID;
                MainActivity.this.mUserName = this.mUserRank;
                if (MainActivity.this.mToast != null) {
                    MainActivity.this.mToast.cancel();
                }
                MainActivity.this.mToast = Toast.makeText(MainActivity.this.getApplicationContext(), MainActivity.this.getString(R.string.toast_welcome_back) + this.mUserName, 1);
                MainActivity.this.mToast.show();
                MainActivity.this.refreshLoginStatus();
                return;
            }
            String errorMessage;
            MainActivity.this.mUserLoginStatus = UserLoginStatus.OLD_USER_OFFLINE;
            switch (this.mLoginErrorCode) {
                case ERROR_NETWORK:
                    errorMessage = MainActivity.this.getString(R.string.error_no_network);
                    break;
                case ERROR_CONNECTION:
                    errorMessage = MainActivity.this.getString(R.string.error_connection_failure);
                    break;
                case ERROR_TIMEOUT:
                    errorMessage = MainActivity.this.getString(R.string.error_socket_timeout);
                    break;
                case ERROR_INCORRECT_PASSWORD:
                    errorMessage = MainActivity.this.getString(R.string.error_incorrect_password);
                    break;
                default:
                    errorMessage = MainActivity.this.getString(R.string.error_unknown);
                    break;
            }
            if (MainActivity.this.mToast != null) {
                MainActivity.this.mToast.cancel();
            }
            MainActivity.this.mToast = Toast.makeText(MainActivity.this.getApplicationContext(), errorMessage + "\n" + MainActivity.this.getString(R.string.prompt_login_retry_manually), 1);
            MainActivity.this.mToast.show();
            Snackbar.make(MainActivity.this.mCoordinatorLayout, MainActivity.this.getString(R.string.error_something_goes_wrong), 0).show();
            Log.e(MainActivity.this.LOG_TAG, "Auto login failed: " + errorMessage);
            MainActivity.this.logOut();
        }

        protected void onCancelled() {
            MainActivity.this.mAutoLoginTask = null;
            MainActivity.this.showProgress(false);
        }
    }

    public class FetchBlogInfosTask extends AsyncTask<Integer, Void, Boolean> {
        private HttpErrorType mErrorCode;
        private ParseBlogInfoList mParseBlogInfoList;
        private int mTargetSubforumPageIndex;

        public FetchBlogInfosTask() {
            this.mTargetSubforumPageIndex = MainActivity.this.mSubforumParsedPageCount + 1;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(MainActivity.this.LOG_TAG, "FetchBlogInfosTask instantiated, target page " + this.mTargetSubforumPageIndex);
            MainActivity.this.mIsFetchingBlogInfos = true;
            MainActivity.this.showProgress(true);
        }

        protected Boolean doInBackground(Integer... params) {
            for (int i = 0; i < 2; i++) {
                try {
                    String href = ParseBlogInfoList.getBlogInfoListHref(this.mTargetSubforumPageIndex);
                    Log.v(MainActivity.this.LOG_TAG, "Feteching blogs @ page " + this.mTargetSubforumPageIndex + ": " + href + ", trial " + i);
                    HttpGET httpGET = new HttpGET(href, MainActivity.this.mCookie, HTTP.UTF_8);
                    Document doc = httpGET.getJsoupDocument();
                    if (doc == null) {
                        HttpErrorType httpGetInnerErrorCode = httpGET.getErrorCode();
                        if (httpGetInnerErrorCode != HttpErrorType.SUCCESS) {
                            this.mErrorCode = httpGetInnerErrorCode;
                        } else {
                            this.mErrorCode = HttpErrorType.ERROR_JSOUP_PARSE_FAILURE;
                        }
                    } else {
                        Log.v(MainActivity.this.LOG_TAG, "Feteched blogs @ page " + this.mTargetSubforumPageIndex + " : " + href + "\nStart parsing.");
                        this.mParseBlogInfoList = new ParseBlogInfoList(this.mTargetSubforumPageIndex, doc, MainActivity.this.mBlogInfos.size());
                        this.mParseBlogInfoList.setOutputBlogInfo(false);
                        this.mErrorCode = this.mParseBlogInfoList.execute();
                        if (this.mErrorCode == HttpErrorType.SUCCESS) {
                            Log.v(MainActivity.this.LOG_TAG, "Successfully parsed blogs @ page " + this.mTargetSubforumPageIndex);
                            return Boolean.valueOf(true);
                        } else if (this.mErrorCode == HttpErrorType.ERROR_BLOGINFOLIST_PARSE_FAILURE) {
                            Log.e(MainActivity.this.LOG_TAG, "PARSE FAILURE! Blogs page " + this.mTargetSubforumPageIndex);
                            return Boolean.valueOf(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
                    Log.e(MainActivity.this.LOG_TAG, "UNKNOWN EXCEPTION in FetchBlogInfosTask, page " + this.mTargetSubforumPageIndex + " trial " + i);
                }
            }
            return Boolean.valueOf(false);
        }

        protected void onPostExecute(Boolean isSucess) {
            MainActivity.this.mFetchBlogInfosTask = null;
            MainActivity.this.mIsFetchingBlogInfos = false;
            MainActivity.this.showProgress(false);
            if (isSucess.booleanValue()) {
                ArrayList<BlogInfo> newBlogInfos = this.mParseBlogInfoList.getBlogInfos();
                MainActivity.this.mBlogInfos.addAll(newBlogInfos);
                MainActivity.this.mBlogInfoAdapter.notifyDataSetChanged();
                Snackbar.make(MainActivity.this.mCoordinatorLayout, MainActivity.this.getString(R.string.toast_load_bloginfos_sucess), -1).show();
                Log.d(MainActivity.this.LOG_TAG, "Added " + newBlogInfos.size() + " new BlogInfos from page " + this.mTargetSubforumPageIndex);
                Log.v(MainActivity.this.LOG_TAG, "Now altogether " + MainActivity.this.mBlogInfos.size() + " BlogInfos.");
                if (this.mTargetSubforumPageIndex == 1) {
                    MainActivity.this.mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                        public void onRefresh() {
                            MainActivity.this.reloadContentList(MainActivity.this.getString(R.string.toast_refresh_bloginfos_progress));
                        }
                    });
                    MainActivity.this.mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(MainActivity.this.mLinearLayoutLayoutManager, 2) {
                        public void onLoadMore(int current_page) {
                            if (!MainActivity.this.mIsFetchingBlogInfos) {
                                MainActivity.this.fetchContentList(MainActivity.this.getString(R.string.toast_load_more_bloginfos_progress));
                            }
                        }
                    });
                }
                MainActivity.this.mSubforumParsedPageCount = MainActivity.this.mSubforumParsedPageCount + 1;
                if (this.mParseBlogInfoList.hasNextPage()) {
                    MainActivity.this.mContentPageCount = MainActivity.this.mSubforumParsedPageCount + 1;
                    return;
                } else {
                    MainActivity.this.mContentPageCount = MainActivity.this.mSubforumParsedPageCount;
                    return;
                }
            }
            CharSequence errorMessage;
            Log.e(MainActivity.this.LOG_TAG, "Fetching BlogInfos failed! Page " + this.mTargetSubforumPageIndex);
            switch (this.mErrorCode) {
                case ERROR_NETWORK:
                    errorMessage = MainActivity.this.getString(R.string.error_no_network);
                    break;
                case ERROR_CONNECTION:
                    errorMessage = MainActivity.this.getString(R.string.error_connection_failure);
                    break;
                case ERROR_TIMEOUT:
                    errorMessage = MainActivity.this.getString(R.string.error_socket_timeout);
                    break;
                default:
                    errorMessage = MainActivity.this.getString(R.string.toast_load_bloginfos_failure);
                    break;
            }
            Snackbar.make(MainActivity.this.mCoordinatorLayout, errorMessage, 0).show();
            if (this.mTargetSubforumPageIndex == 1) {
                MainActivity.this.mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                    public void onRefresh() {
                        MainActivity.this.reloadContentList(MainActivity.this.getString(R.string.toast_load_bloginfos_progress));
                    }
                });
            }
        }

        protected void onCancelled() {
            super.onCancelled();
            MainActivity.this.mFetchBlogInfosTask = null;
            MainActivity.this.mIsFetchingBlogInfos = false;
            Log.v(MainActivity.this.LOG_TAG, "FetchBlogInfosTask cancelled, target page " + this.mTargetSubforumPageIndex);
        }
    }

    public class FetchSubforumThreadInfosTask extends AsyncTask<Integer, Void, Boolean> {
        private HttpErrorType mErrorCode;
        private ParseThreadInfoList mParseThreadInfoList;
        private int mTargetSubforumID;
        private int mTargetSubforumPageIndex;

        public FetchSubforumThreadInfosTask(int subforumID) {
            this.mTargetSubforumID = subforumID;
            this.mTargetSubforumPageIndex = MainActivity.this.mSubforumParsedPageCount + 1;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(MainActivity.this.LOG_TAG, "FetchSubforumThreadInfosTask instantiated, target subforum: " + this.mTargetSubforumID + ", page " + this.mTargetSubforumPageIndex);
            MainActivity.this.mIsFetchingSubforumThreadInfos = true;
            MainActivity.this.showProgress(true);
            if (MainActivity.this.mUserLoginStatus != UserLoginStatus.GUEST) {
                MainActivity.this.mFloatingActionButton.hide();
            }
        }

        protected Boolean doInBackground(Integer... params) {
            for (int i = 0; i < 2; i++) {
                try {
                    String href = ParseThreadInfoList.getThreadInfoListHref(this.mTargetSubforumID, this.mTargetSubforumPageIndex);
                    Log.v(MainActivity.this.LOG_TAG, "Feteching webpage @ Subforum #" + this.mTargetSubforumID + ", page " + this.mTargetSubforumID + ": " + href + ", trial " + i);
                    HttpGET httpGET = new HttpGET(href, MainActivity.this.mCookie, "GBK");
                    Document doc = httpGET.getJsoupDocument();
                    if (doc == null) {
                        HttpErrorType httpGetInnerErrorCode = httpGET.getErrorCode();
                        if (httpGetInnerErrorCode != HttpErrorType.SUCCESS) {
                            this.mErrorCode = httpGetInnerErrorCode;
                        } else {
                            this.mErrorCode = HttpErrorType.ERROR_JSOUP_PARSE_FAILURE;
                        }
                    } else {
                        Log.v(MainActivity.this.LOG_TAG, "Feteched webpage @ Subforum #" + this.mTargetSubforumID + ", page " + this.mTargetSubforumID + " : " + href + "\nStart parsing.");
                        this.mParseThreadInfoList = new ParseThreadInfoList(this.mTargetSubforumID, MainActivity.this.mSubforumTitle, this.mTargetSubforumPageIndex, doc, MainActivity.this.mThreadInfos.size());
                        this.mParseThreadInfoList.setOutputThreadInfo(false);
                        this.mErrorCode = this.mParseThreadInfoList.execute();
                        if (this.mErrorCode == HttpErrorType.SUCCESS) {
                            Log.v(MainActivity.this.LOG_TAG, "Successfully parsed webpage @ Subforum #" + this.mTargetSubforumID);
                            if (this.mTargetSubforumPageIndex == 1) {
                                MainActivity.this.mContentPageCount = this.mParseThreadInfoList.getSubforumPageCount();
                            }
                            return Boolean.valueOf(true);
                        } else if (this.mErrorCode == HttpErrorType.ERROR_THREADINFOLIST_PARSE_FAILURE) {
                            Log.e(MainActivity.this.LOG_TAG, "PARSE FAILURE! @ Subforum #" + this.mTargetSubforumID);
                            return Boolean.valueOf(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
                    Log.e(MainActivity.this.LOG_TAG, "UNKNOWN EXCEPTION in FetechSubforumThreadsTask, #" + this.mTargetSubforumID + ", page " + this.mTargetSubforumID + " trial " + i);
                }
            }
            return Boolean.valueOf(false);
        }

        protected void onPostExecute(Boolean isSucess) {
            MainActivity.this.mFetchSubforumThreadInfosTask = null;
            MainActivity.this.mIsFetchingSubforumThreadInfos = false;
            MainActivity.this.showProgress(false);
            if (isSucess.booleanValue()) {
                ArrayList<ThreadInfo> newThreadInfos = this.mParseThreadInfoList.getThreadInfos();
                MainActivity.this.mThreadInfos.addAll(newThreadInfos);
                MainActivity.this.mThreadInfoAdapter.notifyDataSetChanged();
                if (this.mTargetSubforumPageIndex == 1) {
                    MainActivity.this.mFormHash = this.mParseThreadInfoList.getFormHash();
                }
                Snackbar.make(MainActivity.this.mCoordinatorLayout, MainActivity.this.getString(R.string.toast_load_threadinfos_sucess), -1).show();
                Log.d(MainActivity.this.LOG_TAG, "Added " + newThreadInfos.size() + " new ThreadInfos from subforum #" + this.mTargetSubforumID + ": " + MainActivity.this.mSubforumTitle + ", page " + this.mTargetSubforumPageIndex);
                Log.v(MainActivity.this.LOG_TAG, "Now altogether " + MainActivity.this.mThreadInfos.size() + " ThreadInfos.");
                if (MainActivity.this.mUserLoginStatus != UserLoginStatus.GUEST) {
                    MainActivity.this.mFloatingActionButton.show();
                }
                if (this.mTargetSubforumPageIndex == 1) {
                    MainActivity.this.mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                        public void onRefresh() {
                            MainActivity.this.reloadContentList(MainActivity.this.getString(R.string.toast_refresh_threadinfos_progress));
                        }
                    });
                    MainActivity.this.mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(MainActivity.this.mLinearLayoutLayoutManager) {
                        public void onLoadMore(int current_page) {
                            if (!MainActivity.this.mIsFetchingSubforumThreadInfos) {
                                MainActivity.this.fetchContentList(MainActivity.this.getString(R.string.toast_load_more_threadinfos_progress));
                            }
                        }
                    });
                }
                MainActivity.this.mSubforumParsedPageCount = MainActivity.this.mSubforumParsedPageCount + 1;
                return;
            }
            CharSequence errorMessage;
            Log.e(MainActivity.this.LOG_TAG, "Fetching ThreadInfos failed! Subforum #" + this.mTargetSubforumID + ", page " + this.mTargetSubforumID);
            switch (this.mErrorCode) {
                case ERROR_NETWORK:
                    errorMessage = MainActivity.this.getString(R.string.error_no_network);
                    break;
                case ERROR_CONNECTION:
                    errorMessage = MainActivity.this.getString(R.string.error_connection_failure);
                    break;
                case ERROR_TIMEOUT:
                    errorMessage = MainActivity.this.getString(R.string.error_socket_timeout);
                    break;
                default:
                    errorMessage = MainActivity.this.getString(R.string.toast_load_threadinfos_failure);
                    break;
            }
            Snackbar.make(MainActivity.this.mCoordinatorLayout, errorMessage, 0).show();
            if (this.mTargetSubforumPageIndex > 1 && MainActivity.this.mFloatingActionButton != null) {
                MainActivity.this.mFloatingActionButton.show();
            }
            if (this.mTargetSubforumPageIndex == 1) {
                MainActivity.this.mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                    public void onRefresh() {
                        MainActivity.this.reloadContentList(MainActivity.this.getString(R.string.toast_load_threadinfos_progress));
                    }
                });
            }
        }

        protected void onCancelled() {
            MainActivity.this.mFetchSubforumThreadInfosTask = null;
            MainActivity.this.mIsFetchingSubforumThreadInfos = false;
            Log.v(MainActivity.this.LOG_TAG, "FetchSubforumThreadInfosTask cancelled, target subforum: " + this.mTargetSubforumID + ", page " + this.mTargetSubforumPageIndex);
        }
    }

    public class FloatingActionButtonClickEvent implements OnClickListener {
        private AlertDialog mAlertDialog;
        private EditText mBodyEditText;
        private String mBodyErrorMessage;
        private boolean mBodyTextQualified;
        private Context mFABContext;
        private String mLastInputBodyText;
        private String mLastInputTitleText;
        private Button mNegativeButton;
        private Button mPositiveButton;
        private EditText mTitleEditText;
        private String mTitleErrorMessage;
        private boolean mTitleTextQualified;

        public FloatingActionButtonClickEvent(Context context) {
            this.mFABContext = context;
        }

        public void onClick(View v) {
            if (v == MainActivity.this.mFloatingActionButton) {
                Builder builder = new Builder(this.mFABContext);
                builder.setTitle(MainActivity.this.getString(R.string.post_new_thread_input_title));
                builder.setView((int) R.layout.post_new_thread_input);
                builder.setPositiveButton(MainActivity.this.getString(R.string.post_new_thread_input_confirm_yes), null);
                builder.setNegativeButton(MainActivity.this.getString(R.string.post_new_thread_input_confirm_no), null);
                this.mAlertDialog = builder.show();
                this.mAlertDialog.setCanceledOnTouchOutside(false);
                this.mAlertDialog.setCancelable(false);
                this.mTitleEditText = (EditText) this.mAlertDialog.findViewById(R.id.post_new_thread_title_input);
                this.mBodyEditText = (EditText) this.mAlertDialog.findViewById(R.id.post_new_thread_body_input);
                this.mPositiveButton = this.mAlertDialog.getButton(-1);
                this.mPositiveButton.setOnClickListener(this);
                this.mNegativeButton = this.mAlertDialog.getButton(-2);
                this.mNegativeButton.setOnClickListener(this);
            } else if (v == this.mPositiveButton) {
                this.mLastInputTitleText = this.mTitleEditText.getText().toString();
                this.mLastInputBodyText = this.mBodyEditText.getText().toString();
                this.mTitleTextQualified = true;
                if (this.mLastInputTitleText == null || this.mLastInputTitleText.trim().length() == 0 || StringUtil.isBlank(this.mLastInputTitleText)) {
                    this.mTitleTextQualified = false;
                    this.mTitleErrorMessage = MainActivity.this.getString(R.string.post_new_thread_error_text_empty);
                }
                this.mBodyTextQualified = true;
                if (this.mLastInputBodyText == null || this.mLastInputBodyText.trim().length() == 0 || StringUtil.isBlank(this.mLastInputBodyText)) {
                    this.mBodyTextQualified = false;
                    this.mBodyErrorMessage = MainActivity.this.getString(R.string.post_new_thread_error_text_empty);
                } else if (this.mLastInputBodyText.length() < 8) {
                    this.mBodyTextQualified = false;
                    this.mBodyErrorMessage = MainActivity.this.getString(R.string.post_new_thread_error_too_short);
                }
                if (!this.mTitleTextQualified) {
                    this.mTitleEditText.setError(this.mTitleErrorMessage);
                }
                if (!this.mBodyTextQualified) {
                    this.mBodyEditText.setError(this.mBodyErrorMessage);
                }
                if (this.mTitleTextQualified && this.mBodyTextQualified) {
                    this.mLastInputTitleText = this.mLastInputTitleText.replaceAll("\n", " ");
                    this.mLastInputTitleText = this.mLastInputTitleText.replaceAll("\r", " ");
                    MainActivity.this.mPostNewThreadTask = new PostNewThreadTask(this.mLastInputTitleText, this.mLastInputBodyText);
                    MainActivity.this.mPostNewThreadTask.execute(new Integer[0]);
                    this.mAlertDialog.dismiss();
                }
            } else if (v == this.mNegativeButton) {
                resetStatus();
                this.mAlertDialog.cancel();
                Snackbar.make(MainActivity.this.mCoordinatorLayout, MainActivity.this.getString(R.string.toast_post_new_thread_cancelled), -1).show();
            }
        }

        public void onPostSuccessCallback() {
            resetStatus();
        }

        public void resetStatus() {
            this.mLastInputTitleText = null;
            this.mLastInputBodyText = null;
            this.mTitleTextQualified = false;
            this.mBodyTextQualified = false;
        }
    }

    public class GetUserAvatarTask extends AsyncTask<Void, Void, Boolean> {
        private HttpErrorType mAvatarErrorCode;
        private Bitmap mUserAvatar;

        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(MainActivity.this.LOG_TAG, "GetUserAvatarTask instantiated, user id: " + MainActivity.this.mUserID);
            MainActivity.this.mIsGettingUserAvatar = true;
        }

        protected Boolean doInBackground(Void... params) {
            for (int j = 0; j < 1; j++) {
                GETUserAvatar httpGETUserAvatar = new GETUserAvatar(MainActivity.this.mCookie, MainActivity.this.mUserID);
                Log.v(MainActivity.this.LOG_TAG, "Executing GetUserAvatarTask @ UserID: " + MainActivity.this.mUserID + ", trial " + j);
                this.mAvatarErrorCode = httpGETUserAvatar.execute(AvatarSize.MEDIUM);
                if (this.mAvatarErrorCode == HttpErrorType.SUCCESS) {
                    this.mUserAvatar = httpGETUserAvatar.getAvatarImage();
                    return Boolean.valueOf(true);
                }
                Log.e(MainActivity.this.LOG_TAG, "Error code: " + this.mAvatarErrorCode + ", GETUserAvatar @ UserID: " + MainActivity.this.mUserID + ", trial " + j);
            }
            return Boolean.valueOf(false);
        }

        protected void onPostExecute(Boolean aBoolean) {
            MainActivity.this.mGetUserAvatarTask = null;
            MainActivity.this.mIsGettingUserAvatar = false;
            if (aBoolean.booleanValue()) {
                Log.v(MainActivity.this.LOG_TAG, "Successfully get UserAvatar, UserID: " + MainActivity.this.mUserID + "\nStart saving UserAvatar to local...");
                try {
                    FileOutputStream fileOutputStream = MainActivity.this.openFileOutput(MainActivity.this.mUserID + "_avatar.jpg", 0);
                    this.mUserAvatar.compress(CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                    MainActivity.this.mUserImageView.setImageBitmap(this.mUserAvatar);
                    Log.v(MainActivity.this.LOG_TAG, "Successfully saved UserAvatar, UserID: " + MainActivity.this.mUserID);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(MainActivity.this.LOG_TAG, "Failed saving UserAvatar! UserID: " + MainActivity.this.mUserID);
                    return;
                }
            }
            Log.e(MainActivity.this.LOG_TAG, "Failed to get UserAvatar within 1 times, UserID: " + MainActivity.this.mUserID);
        }

        protected void onCancelled() {
            super.onCancelled();
            MainActivity.this.mGetUserAvatarTask = null;
            MainActivity.this.mIsGettingUserAvatar = false;
            Log.v(MainActivity.this.LOG_TAG, "GetUserAvatarTask cancelled, user id: " + MainActivity.this.mUserID);
        }
    }

    public class LoadUserAvatarFromCacheTask extends AsyncTask<Void, Void, Boolean> {
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity.this.mIsLoadingUserAvatarFromCache = true;
            Log.v(MainActivity.this.LOG_TAG, "LoadUserAvatarFromCacheTask instantiated, user id: " + MainActivity.this.mUserID);
        }

        protected Boolean doInBackground(Void... params) {
            MainActivity.this.mIsLoadingUserAvatarFromCache = true;
            Log.v(MainActivity.this.LOG_TAG, "Loading user avatar from cache, user id: " + MainActivity.this.mUserID);
            try {
                MainActivity.this.mAvatar = BitmapFactory.decodeStream(MainActivity.this.openFileInput(MainActivity.this.mUserID + "_avatar.jpg"));
                return Boolean.valueOf(true);
            } catch (Exception e) {
                Log.e(MainActivity.this.LOG_TAG, "Failed to load UserAvatarUtils from local storage!");
                e.printStackTrace();
                return Boolean.valueOf(false);
            }
        }

        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            MainActivity.this.mLoadUserAvatarFromCacheTask = null;
            MainActivity.this.mIsLoadingUserAvatarFromCache = false;
            if (aBoolean.booleanValue()) {
                MainActivity.this.mUserImageView.setImageBitmap(MainActivity.this.mAvatar);
            }
        }

        protected void onCancelled() {
            super.onCancelled();
            MainActivity.this.mLoadUserAvatarFromCacheTask = null;
            MainActivity.this.mIsLoadingUserAvatarFromCache = false;
            Log.v(MainActivity.this.LOG_TAG, "LoadUserAvatarFromCacheTask cancelled, user id: " + MainActivity.this.mUserID);
        }
    }

    public class PostNewThreadTask extends AsyncTask<Integer, Void, Boolean> {
        private HttpErrorType mErrorCode;
        private String mPostBody;
        private String mPostTitle;

        public PostNewThreadTask(String postTitle, String postBody) {
            this.mPostTitle = postTitle;
            this.mPostBody = postBody;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(MainActivity.this.LOG_TAG, "PostNewThreadTask instantiated:\n" + this.mPostTitle);
            MainActivity.this.mIsPostingNewThread = true;
            MainActivity.this.showProgress(true);
            MainActivity.this.mFloatingActionButton.hide();
        }

        protected Boolean doInBackground(Integer... params) {
            this.mErrorCode = new POSTNewThread(MainActivity.this.mCookie, MainActivity.this.mSubforumID, MainActivity.this.mFormHash, this.mPostTitle, this.mPostBody).execute();
            if (this.mErrorCode == HttpErrorType.SUCCESS) {
                return Boolean.valueOf(true);
            }
            return Boolean.valueOf(false);
        }

        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            MainActivity.this.mIsPostingNewThread = false;
            MainActivity.this.mPostNewThreadTask = null;
            MainActivity.this.showProgress(false);
            MainActivity.this.mFloatingActionButton.show();
            if (success.booleanValue()) {
                MainActivity.this.mFloatingActionButtonClickEvent.onPostSuccessCallback();
                Snackbar.make(MainActivity.this.mCoordinatorLayout, MainActivity.this.getString(R.string.toast_post_new_thread_sucess), -1).show();
                Log.d(MainActivity.this.LOG_TAG, "PostNewThreadTask succeeded! ");
                return;
            }
            CharSequence errorMessage;
            switch (this.mErrorCode) {
                case ERROR_NETWORK:
                    errorMessage = MainActivity.this.getString(R.string.error_no_network);
                    break;
                case ERROR_CONNECTION:
                    errorMessage = MainActivity.this.getString(R.string.error_connection_failure);
                    break;
                case ERROR_TIMEOUT:
                    errorMessage = MainActivity.this.getString(R.string.error_socket_timeout);
                    break;
                default:
                    errorMessage = MainActivity.this.getString(R.string.toast_post_new_thread_failure);
                    break;
            }
            Snackbar.make(MainActivity.this.mCoordinatorLayout, errorMessage, 0).show();
        }

        protected void onCancelled() {
            super.onCancelled();
            MainActivity.this.mIsPostingNewThread = false;
            MainActivity.this.mPostNewThreadTask = null;
            Log.v(MainActivity.this.LOG_TAG, "PostNewThreadTask cancelled! ");
            MainActivity.this.showProgress(false);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mAccountPref = getSharedPreferences(getString(R.string.account_shared_preference), 0);
        this.mIsGuest = this.mAccountPref.getBoolean(getString(R.string.state_key_user_is_guest), true);
        this.mEmail = this.mAccountPref.getString(getString(R.string.account_key_email), null);
        this.mPassword = this.mAccountPref.getString(getString(R.string.account_key_password), null);
        this.mCookie = this.mAccountPref.getString(getString(R.string.account_key_cookie), null);
        this.mUserName = this.mAccountPref.getString(getString(R.string.account_key_user_name), null);
        this.mUserRank = this.mAccountPref.getString(getString(R.string.account_key_user_rank), null);
        this.mUserID = this.mAccountPref.getString(getString(R.string.account_key_user_id), null);
        Bmob.initialize(this, "d68a0f573a01d416a3dd10223c3bbcfc");
        this.mAccountPref.edit().putInt(getString(R.string.account_key_main_startup_count), this.mAccountPref.getInt(getString(R.string.account_key_main_startup_count), 0) + 1).commit();
        initUI();
        DrawerLayout drawer;
        if (this.mIsGuest || !(this.mUserName == null || this.mUserRank == null)) {
            boolean shouldRelogin = false;
            if (this.mIsGuest) {
                this.mUserLoginStatus = UserLoginStatus.GUEST;
                refreshSubforumGroup(0);
                updateNavigationHeader(this.mIsGuest);
            } else {
                this.mUserLoginStatus = UserLoginStatus.OLD_USER_LOGGING_IN;
                int lastLoginYear = this.mAccountPref.getInt(getString(R.string.account_key_last_login_year), -1);
                int lastLoginMonth = this.mAccountPref.getInt(getString(R.string.account_key_last_login_month), -1);
                int lastLoginDay = this.mAccountPref.getInt(getString(R.string.account_key_last_login_day), -1);
                if (lastLoginYear == -1 || lastLoginMonth == -1 || lastLoginDay == -1) {
                    shouldRelogin = true;
                } else {
                    Calendar lastLoginDate = Calendar.getInstance();
                    lastLoginDate.set(lastLoginYear, lastLoginMonth, lastLoginDay);
                    Calendar currentDate = Calendar.getInstance();
                    long then = lastLoginDate.getTimeInMillis();
                    long now = currentDate.getTimeInMillis();
                    long daysLongInt = TimeUnit.MILLISECONDS.toDays(Math.abs(now - then));
                    Log.v(this.LOG_TAG, "Last login date: " + lastLoginYear + "-" + lastLoginMonth + "-" + lastLoginDay + "\nCurrent date: " + currentDate.get(1) + "-" + currentDate.get(2) + "-" + currentDate.get(5) + "\nDays diff: " + daysLongInt);
                    if (daysLongInt > 14) {
                        shouldRelogin = true;
                    } else {
                        this.mUserLoginStatus = UserLoginStatus.OLD_USER_ONLINE;
                        refreshSubforumGroup(0);
                        updateNavigationHeader(this.mIsGuest);
                        this.mGetUserAvatarTask = new GetUserAvatarTask();
                        this.mGetUserAvatarTask.execute(new Void[0]);
                    }
                }
            }
            if (shouldRelogin) {
                this.mToast = Toast.makeText(getApplicationContext(), getString(R.string.toast_auto_login_progress), 0);
                this.mToast.show();
                showProgress(true);
                this.mAutoLoginTask = new AutoLoginTask(this.mEmail, this.mPassword);
                this.mAutoLoginTask.execute(new Void[]{(Void) null});
                return;
            } else if (savedInstanceState != null) {
                boolean isBrowsingThread = savedInstanceState.getBoolean(getString(R.string.state_key_was_browsing_thread));
                boolean isBrowsingBlog = savedInstanceState.getBoolean(getString(R.string.state_key_was_browsing_blog));
                if (isBrowsingThread) {
                    ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
                    this.mSubforumGroupIndex = savedInstanceState.getInt(getString(R.string.state_key_subforum_group_index));
                    this.mSubforumIndex = savedInstanceState.getInt(getString(R.string.state_key_subforum_index));
                    this.mSubforumID = savedInstanceState.getInt(getString(R.string.state_key_subforum_id));
                    this.mSubforumTitle = savedInstanceState.getString(getString(R.string.state_key_subforum_title));
                    refreshSubforumGroup(this.mSubforumGroupIndex);
                    return;
                } else if (!isBrowsingBlog) {
                    this.mSubforumGroupIndex = savedInstanceState.getInt(getString(R.string.state_key_subforum_group_index));
                    Log.v(this.LOG_TAG, "Resuming from previous browsing status: subforum group #" + this.mSubforumGroupIndex);
                    refreshSubforumGroup(this.mSubforumGroupIndex);
                    return;
                } else {
                    return;
                }
            } else {
                drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                    return;
                }
                return;
            }
        }
        this.mUserLoginStatus = UserLoginStatus.NEW_USER;
        refreshSubforumGroup(0);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        }
        onClickUserImageView(null);
    }

    public void initUI() {
        this.mSubforumGroupIndex = 0;
        this.mSubforumID = 0;
        this.mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);
        this.mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(this.mToolbar);
        this.mFloatingActionButton = (FloatingActionButton) findViewById(R.id.main_fab);
        if (this.mIsGuest) {
            this.mFloatingActionButton.setVisibility(8);
        } else {
            this.mFloatingActionButtonClickEvent = new FloatingActionButtonClickEvent(this);
            this.mFloatingActionButton.setOnClickListener(this.mFloatingActionButtonClickEvent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, this.mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(this.mSubforumGroupIndex).setChecked(true);
        View navigationHeaderView = navigationView.getHeaderView(0);
        this.mUserImageView = (CircularImageView) navigationHeaderView.findViewById(R.id.nav_header_user_image);
        this.mUserImageView.setBorderWidth(0.0f);
        this.mUserImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.onClickUserImageView(v);
            }
        });
        this.mUserNameTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_username);
        this.mUserRankTextView = (TextView) navigationHeaderView.findViewById(R.id.nav_header_userrank);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe_refresh_layout);
        this.mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        this.mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);
        this.mLinearLayoutLayoutManager = new LinearLayoutManager(this);
        this.mRecyclerView.setLayoutManager(this.mLinearLayoutLayoutManager);
    }

    public void refreshLoginStatus() {
        if (this.mUserLoginStatus == UserLoginStatus.GUEST) {
            updateNavigationHeader(this.mIsGuest);
            initUI();
            refreshSubforumGroup(0);
        } else if (this.mUserLoginStatus == UserLoginStatus.OLD_USER_ONLINE) {
            this.mAccountPref = getSharedPreferences(getString(R.string.account_shared_preference), 0);
            this.mCookie = this.mAccountPref.getString(getString(R.string.account_key_cookie), null);
            this.mUserName = this.mAccountPref.getString(getString(R.string.account_key_user_name), null);
            this.mUserRank = this.mAccountPref.getString(getString(R.string.account_key_user_rank), null);
            this.mUserID = this.mAccountPref.getString(getString(R.string.account_key_user_id), null);
            updateNavigationHeader(this.mIsGuest);
            initUI();
            refreshSubforumGroup(0);
        }
    }

    private void updateNavigationHeader(boolean isGuest) {
        if (isGuest) {
            this.mUserNameTextView.setText(getString(R.string.header_placeholder_username_guest));
            this.mUserRankTextView.setText(getString(R.string.header_placeholder_click_to_login) + " ");
            this.mUserImageView.setImageResource(R.drawable.img_noavatar_circle);
            return;
        }
        this.mUserNameTextView.setText(this.mUserName);
        this.mUserRankTextView.setText(this.mUserRank + " ");
        loadUserAvatarFromLocalCache();
    }

    private int loadUserAvatarFromLocalCache() {
        if (this.mIsLoadingUserAvatarFromCache) {
            return 1;
        }
        this.mLoadUserAvatarFromCacheTask = new LoadUserAvatarFromCacheTask();
        this.mLoadUserAvatarFromCacheTask.execute(new Void[0]);
        return 0;
    }

    public void refreshSubforumGroup(int subforumGroupIndex) {
        int subforumGroupID = R.array.subforum0_application_titles;
        switch (subforumGroupIndex) {
            case 1:
                subforumGroupID = R.array.subforum0_application_titles;
                this.mSubforumIDs = getResources().getIntArray(R.array.subforum0_application_indexes);
                this.mSubforumTitles = getResources().getStringArray(R.array.subforum0_application_titles);
                break;
            case 2:
                subforumGroupID = R.array.subforum1_study_titles;
                this.mSubforumIDs = getResources().getIntArray(R.array.subforum1_study_indexes);
                this.mSubforumTitles = getResources().getStringArray(R.array.subforum1_study_titles);
                break;
            case 3:
                subforumGroupID = R.array.subforum2_work_titles;
                this.mSubforumIDs = getResources().getIntArray(R.array.subforum2_work_indexes);
                this.mSubforumTitles = getResources().getStringArray(R.array.subforum2_work_titles);
                break;
            case 4:
                subforumGroupID = R.array.subforum3_academic_titles;
                this.mSubforumIDs = getResources().getIntArray(R.array.subforum3_academic_indexes);
                this.mSubforumTitles = getResources().getStringArray(R.array.subforum3_academic_titles);
                break;
            case 5:
                subforumGroupID = R.array.subforum4_entertainment_titles;
                this.mSubforumIDs = getResources().getIntArray(R.array.subforum4_entertainment_indexes);
                this.mSubforumTitles = getResources().getStringArray(R.array.subforum4_entertainment_titles);
                break;
            case 6:
                subforumGroupID = R.array.subforum5_finance_titles;
                this.mSubforumIDs = getResources().getIntArray(R.array.subforum5_finance_indexes);
                this.mSubforumTitles = getResources().getStringArray(R.array.subforum5_finance_titles);
                break;
        }
        if (subforumGroupIndex == 0) {
            this.mToolbarSpinner = (Spinner) this.mToolbar.findViewById(R.id.main_toolbar_spinner);
            this.mToolbarSpinner.setVisibility(8);
            if (this.mUserLoginStatus != UserLoginStatus.GUEST) {
                this.mFloatingActionButton.hide();
            }
            reloadContentList(getString(R.string.toast_load_bloginfos_progress));
        } else if (subforumGroupIndex >= 1 && subforumGroupIndex <= 6) {
            this.mSubforumID = this.mSubforumIDs[0];
            if (this.mUserLoginStatus != UserLoginStatus.GUEST) {
                this.mFloatingActionButton.show();
            }
            this.mToolbarSpinner = (Spinner) this.mToolbar.findViewById(R.id.main_toolbar_spinner);
            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, subforumGroupID, R.layout.custom_dark_spinner_text_item);
            spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
            this.mToolbarSpinner.setVisibility(0);
            this.mToolbarSpinner.setAdapter(spinnerAdapter);
            this.mToolbarSpinner.setOnItemSelectedListener(this);
        }
    }

    public int reloadContentList(String notificationMessage) {
        if (this.mUserLoginStatus != UserLoginStatus.OLD_USER_ONLINE && this.mUserLoginStatus != UserLoginStatus.GUEST) {
            return 1;
        }
        this.mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);
        if (this.mIsFetchingSubforumThreadInfos) {
            this.mIsFetchingSubforumThreadInfos = false;
            this.mFetchSubforumThreadInfosTask.cancel(true);
            this.mFetchSubforumThreadInfosTask = null;
        }
        if (this.mIsFetchingBlogInfos) {
            this.mIsFetchingBlogInfos = false;
            this.mFetchBlogInfosTask.cancel(true);
            this.mFetchBlogInfosTask = null;
        }
        this.mContentPageCount = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mSubforumParsedPageCount = 0;
        if (this.mSubforumGroupIndex == 0) {
            this.mRecyclerView.clearOnScrollListeners();
            this.mBlogInfos = new ArrayList(100);
            this.mBlogInfoAdapter = new BlogInfoAdapter(getApplicationContext(), this.mBlogInfos, this);
            this.mRecyclerView.setAdapter(this.mBlogInfoAdapter);
            return fetchContentList(notificationMessage);
        }
        this.mRecyclerView.clearOnScrollListeners();
        this.mThreadInfos = new ArrayList(200);
        this.mThreadInfoAdapter = new ThreadInfoAdapter(getApplicationContext(), this.mThreadInfos, this);
        this.mRecyclerView.setAdapter(this.mThreadInfoAdapter);
        return fetchContentList(notificationMessage);
    }

    public int fetchContentList(String notificationMessage) {
        if (this.mSubforumParsedPageCount >= this.mContentPageCount) {
            Snackbar.make(this.mCoordinatorLayout, getString(R.string.toast_all_threadinfos_loaded), -1).show();
            return 1;
        } else if (this.mSubforumGroupIndex == 0) {
            if (this.mIsFetchingBlogInfos) {
                return 1;
            }
            if (notificationMessage != null) {
                Snackbar.make(this.mCoordinatorLayout, (CharSequence) notificationMessage, -1).show();
            }
            this.mFetchBlogInfosTask = new FetchBlogInfosTask();
            this.mFetchBlogInfosTask.execute(new Integer[0]);
            return 0;
        } else if (this.mIsFetchingSubforumThreadInfos) {
            return 1;
        } else {
            if (notificationMessage != null) {
                Snackbar.make(this.mCoordinatorLayout, (CharSequence) notificationMessage, -1).show();
            }
            showProgress(true);
            this.mFetchSubforumThreadInfosTask = new FetchSubforumThreadInfosTask(this.mSubforumID);
            this.mFetchSubforumThreadInfosTask.execute(new Integer[0]);
            return 0;
        }
    }

    public void onClickUserImageView(View v) {
        switch (this.mUserLoginStatus) {
            case NEW_USER:
            case GUEST:
            case OLD_USER_OFFLINE:
                startActivityForResult(new Intent("android.intent.action.LOGIN"), 1);
                return;
            case OLD_USER_ONLINE:
                new Builder(this).setTitle(getString(R.string.dialog_logout_confirm_title)).setMessage(getString(R.string.dialog_logout_confirm_content)).setPositiveButton(getString(R.string.dialog_logout_confirm_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.logOut();
                    }
                }).setNegativeButton(getString(R.string.dialog_logout_confirm_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (MainActivity.this.mToast != null) {
                            MainActivity.this.mToast.cancel();
                        }
                        MainActivity.this.mToast = Toast.makeText(MainActivity.this.getApplicationContext(), MainActivity.this.getString(R.string.toast_logout_cancelled), 0);
                        MainActivity.this.mToast.show();
                    }
                }).show();
                return;
            default:
                return;
        }
    }

    public void logOut() {
        this.mUserLoginStatus = UserLoginStatus.NEW_USER;
        if (this.mBlogInfos != null) {
            this.mBlogInfos.clear();
        }
        if (this.mBlogInfoAdapter != null) {
            this.mBlogInfoAdapter.notifyDataSetChanged();
        }
        if (this.mThreadInfos != null) {
            this.mThreadInfos.clear();
        }
        if (this.mThreadInfoAdapter != null) {
            this.mThreadInfoAdapter.notifyDataSetChanged();
        }
        Editor editor = this.mAccountPref.edit();
        editor.putString(getString(R.string.account_key_email), null);
        editor.putString(getString(R.string.account_key_password), null);
        editor.putString(getString(R.string.account_key_cookie), null);
        editor.putString(getString(R.string.account_key_user_name), null);
        editor.putString(getString(R.string.account_key_user_id), null);
        editor.putString(getString(R.string.account_key_user_rank), null);
        editor.commit();
        if (this.mToast != null) {
            this.mToast.cancel();
        }
        this.mToast = Toast.makeText(getApplicationContext(), getString(R.string.toast_logout_success), 0);
        this.mToast.show();
        startActivityForResult(new Intent("android.intent.action.LOGIN"), 2);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 0);
            return true;
        } else if (id != R.id.action_threadlist_refresh) {
            return super.onOptionsItemSelected(item);
        } else {
            if (this.mSubforumGroupIndex == 0) {
                if (this.mIsFetchingSubforumThreadInfos) {
                    Log.v(this.LOG_TAG, "Refresh action in options selected, but FetchBlogInfosTask is already running.");
                    return true;
                }
                Log.v(this.LOG_TAG, "Refresh action in options selected, start refreshing blogs.");
                reloadContentList(getString(R.string.toast_refresh_bloginfos_swipe_hint));
                return true;
            } else if (this.mIsFetchingSubforumThreadInfos) {
                Log.v(this.LOG_TAG, "Refresh action in options selected, but FetchSubforumThreadInfosTask is already running.");
                return true;
            } else {
                Log.v(this.LOG_TAG, "Refresh action in options selected, start refreshing subforum.");
                reloadContentList(getString(R.string.toast_refresh_threadinfos_swipe_hint));
                return true;
            }
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        int targetSubforumGroupIndex = -1;
        if (id == R.id.nav_blog) {
            targetSubforumGroupIndex = 0;
        } else if (id == R.id.nav_application) {
            targetSubforumGroupIndex = 1;
        } else if (id == R.id.nav_study) {
            targetSubforumGroupIndex = 2;
        } else if (id == R.id.nav_work) {
            targetSubforumGroupIndex = 3;
        } else if (id == R.id.nav_academic) {
            targetSubforumGroupIndex = 4;
        } else if (id == R.id.nav_entertainment) {
            targetSubforumGroupIndex = 5;
        } else if (id == R.id.nav_finance) {
            targetSubforumGroupIndex = 6;
        } else if (id == R.id.nav_share) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("【一亩三分地 安卓客户端】下载地址：\n");
            stringBuilder.append("https://github.com/naco-siren/1Point3Acres_public_release");
            Intent sendIntent = new Intent();
            sendIntent.setAction("android.intent.action.SEND");
            sendIntent.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_message) {
            Snackbar.make(this.mCoordinatorLayout, getString(R.string.toast_function_unavailable), 0).setAction((CharSequence) "Action", null).show();
        }
        if (targetSubforumGroupIndex > -1 && targetSubforumGroupIndex != this.mSubforumGroupIndex) {
            this.mSubforumGroupIndex = targetSubforumGroupIndex;
            refreshSubforumGroup(targetSubforumGroupIndex);
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer((int) GravityCompat.START);
        return true;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.main_toolbar_spinner) {
            int targetSubforumID = this.mSubforumIDs[position];
            if (this.mUserLoginStatus == UserLoginStatus.OLD_USER_ONLINE || this.mUserLoginStatus == UserLoginStatus.GUEST) {
                this.mSubforumIndex = position;
                this.mSubforumID = targetSubforumID;
                this.mSubforumTitle = this.mSubforumTitles[position];
                Log.v(this.LOG_TAG, "Subforum #" + this.mSubforumID + ": " + this.mSubforumTitle + " selected");
                reloadContentList(getString(R.string.toast_load_threadinfos_progress));
            }
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @TargetApi(13)
    private void showProgress(final boolean show) {
        Log.v(this.LOG_TAG, "Show Background Progress Animation: " + (show ? "GO!" : "STOP"));
        int shortAnimTime = getResources().getInteger(17694720);
        if (this.mSwipeRefreshLayout != null) {
            this.mSwipeRefreshLayout.post(new Runnable() {
                public void run() {
                    MainActivity.this.mSwipeRefreshLayout.setRefreshing(show);
                }
            });
        }
    }

    public void onClickThreadInfoContent(View caller, int position) {
        ThreadInfo threadInfo = (ThreadInfo) this.mThreadInfos.get(position);
        Log.v(this.LOG_TAG, "Content clicked @ ThreadInfo #" + position + ": " + threadInfo.getThreadTitle());
        this.mBrowsingThreadInfo = threadInfo;
        Intent intent = new Intent(this, ThreadActivity.class);
        Bundle bundle = new Bundle();
        putThreadInfoIntoBundle(threadInfo, bundle);
        ThreadInfoViewHolder threadInfoViewHolder = (ThreadInfoViewHolder) this.mRecyclerView.findViewHolderForAdapterPosition(position);
        ActivityOptions activityOptionsCompat = null;
        if (VERSION.SDK_INT >= 21) {
            activityOptionsCompat = ActivityOptions.makeSceneTransitionAnimation(this, new Pair[]{Pair.create(threadInfoViewHolder.mRootView, getString(R.string.transition_threadinfo_cardview))});
        }
        Drawable drawable = threadInfoViewHolder.mThreadAuthorUserAvatarImageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            bundle.putBoolean("is_thread_author_user_avatar_ready", true);
            intent.putExtra("thread_author_user_avatar", ((BitmapDrawable) drawable).getBitmap());
        } else {
            bundle.putBoolean("is_thread_author_user_avatar_ready", false);
        }
        intent.putExtras(bundle);
        Log.d(this.LOG_TAG, "Bundle put into Intent.\n" + threadInfo.toString() + "\nStarting ThreadActivity...");
        if (VERSION.SDK_INT >= 21) {
            ActivityCompat.startActivityForResult(this, intent, 3, activityOptionsCompat.toBundle());
        } else {
            startActivityForResult(intent, 3);
        }
    }

    public void putThreadInfoIntoBundle(ThreadInfo threadInfo, Bundle bundle) {
        boolean z;
        boolean z2 = true;
        bundle.putString(getString(R.string.state_key_cookie), this.mCookie);
        bundle.putString(getString(R.string.state_key_user_id), this.mUserID);
        bundle.putBoolean(getString(R.string.state_key_user_is_guest), this.mUserLoginStatus == UserLoginStatus.GUEST);
        bundle.putString(getString(R.string.state_key_subforum_title), this.mSubforumTitle);
        bundle.putInt(getString(R.string.state_key_subforum_id), this.mSubforumID);
        bundle.putString(getString(R.string.state_key_thread_id), threadInfo.getThreadID());
        bundle.putString(getString(R.string.state_key_thread_author_user_name), threadInfo.getThreadAuthorName());
        bundle.putString(getString(R.string.state_key_thread_author_user_id), threadInfo.getThreadAuthorID());
        bundle.putString(getString(R.string.state_key_thread_date), threadInfo.getThreadDateTime() + " ");
        String string = getString(R.string.state_key_thread_is_locked);
        if (threadInfo.getThreadStatus() == ThreadStatus.LOCKED) {
            z = true;
        } else {
            z = false;
        }
        bundle.putBoolean(string, z);
        bundle.putBoolean(getString(R.string.state_key_thread_is_attached), threadInfo.isThreadAttached());
        String string2 = getString(R.string.state_key_thread_is_pinned);
        if (threadInfo.getThreadPin() == ThreadPin.NORMAL) {
            z2 = false;
        }
        bundle.putBoolean(string2, z2);
        bundle.putBoolean(getString(R.string.state_key_thread_is_digest), threadInfo.isThreadDigest());
        bundle.putBoolean(getString(R.string.state_key_thread_is_recommended), threadInfo.isThreadRecommended());
        bundle.putBoolean(getString(R.string.state_key_thread_is_hot), threadInfo.isThreadHot());
        bundle.putBoolean(getString(R.string.state_key_thread_is_agreed), threadInfo.isThreadAgreed());
        bundle.putString(getString(R.string.state_key_thread_type), threadInfo.getThreadType());
        bundle.putString(getString(R.string.state_key_thread_title), threadInfo.getThreadTitle());
        bundle.putLong(getString(R.string.state_key_thread_read_count), threadInfo.getThreadReadCount());
        bundle.putLong(getString(R.string.state_key_thread_comment_count), threadInfo.getThreadCommentCount());
    }

    public void onClickThreadInfoUserAvatar(ImageView callerImage, int position) {
        ThreadInfo threadInfo = (ThreadInfo) this.mThreadInfos.get(position);
        Log.v(this.LOG_TAG, "UserAvatarUtils clicked @ ThreadInfo #" + position + ": " + threadInfo.getThreadAuthorName() + ", " + threadInfo.getThreadAuthorID());
    }

    public void onClickBlogInfoContent(View caller, int position) {
        BlogInfo blogInfo = (BlogInfo) this.mBlogInfos.get(position);
        this.mBrowsingBlogInfo = blogInfo;
        BlogInfoViewHolder blogInfoViewHolder = (BlogInfoViewHolder) this.mRecyclerView.findViewHolderForAdapterPosition(position);
        ActivityOptions activityOptionsCompat = null;
        if (VERSION.SDK_INT >= 21) {
            activityOptionsCompat = ActivityOptions.makeSceneTransitionAnimation(this, new Pair[]{Pair.create(blogInfoViewHolder.mRootView, getString(R.string.transition_bloginfo_cardview))});
        }
        Intent intent = new Intent(this, BlogActivity.class);
        Bundle bundle = new Bundle();
        putBlogInfoIntoBundle(blogInfo, bundle);
        intent.putExtras(bundle);
        Log.d(this.LOG_TAG, "Bundle loaded into Intent.\n" + blogInfo.toString() + "\nStarting BlogActivity...");
        if (VERSION.SDK_INT >= 21) {
            ActivityCompat.startActivityForResult(this, intent, 4, activityOptionsCompat.toBundle());
        } else {
            startActivityForResult(intent, 4);
        }
    }

    public void putBlogInfoIntoBundle(BlogInfo blogInfo, Bundle bundle) {
        bundle.putString(getString(R.string.state_key_cookie), this.mCookie);
        bundle.putString(getString(R.string.state_key_user_id), this.mUserID);
        bundle.putString(getString(R.string.state_key_blog_href), blogInfo.getBlogHref());
        bundle.putString(getString(R.string.state_key_blog_title), blogInfo.getBlogTitle());
        bundle.putString(getString(R.string.state_key_blog_intro), blogInfo.getBlogIntro());
        bundle.putString(getString(R.string.state_key_blog_date_month), blogInfo.getBlogDateMonth());
        bundle.putString(getString(R.string.state_key_blog_date_day), blogInfo.getBlogDateDay());
        bundle.putString(getString(R.string.state_key_blog_date_year), blogInfo.getBlogDateYear());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode != -1 && resultCode == 1) {
                logOut();
            }
        } else if (requestCode == 1 || requestCode == 2) {
            if (resultCode == -1) {
                this.mUserLoginStatus = UserLoginStatus.OLD_USER_ONLINE;
                this.mIsGuest = false;
                refreshLoginStatus();
            } else if (resultCode == LoginActivity.RESULT_GUEST) {
                this.mUserLoginStatus = UserLoginStatus.GUEST;
                this.mIsGuest = true;
                refreshLoginStatus();
            } else {
                if (this.mToast != null) {
                    this.mToast.cancel();
                }
                this.mToast = Toast.makeText(getApplicationContext(), getString(R.string.toast_login_cancelled), 1);
                this.mToast.show();
                finish();
            }
        } else if (requestCode == 3) {
            this.mBrowsingThreadInfo = null;
            if (resultCode == 1) {
                logOut();
            }
        } else if (requestCode == 4) {
            this.mBrowsingBlogInfo = null;
            if (resultCode == 1) {
                logOut();
            }
        }
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen((int) GravityCompat.START)) {
            drawer.closeDrawer((int) GravityCompat.START);
        } else if (System.currentTimeMillis() - this.mExitTime > 1350) {
            Snackbar.make(this.mCoordinatorLayout, getString(R.string.toast_press_back_twice_to_exit), -1).show();
            this.mExitTime = System.currentTimeMillis();
        } else {
            if (this.mUserLoginStatus != UserLoginStatus.GUEST) {
                this.mFloatingActionButton.hide();
            }
            if (this.mAutoLoginTask != null) {
                this.mAutoLoginTask.cancel(true);
            }
            if (this.mGetUserAvatarTask != null) {
                this.mGetUserAvatarTask.cancel(true);
            }
            if (this.mFetchSubforumThreadInfosTask != null) {
                this.mFetchSubforumThreadInfosTask.cancel(true);
            }
            if (this.mFetchBlogInfosTask != null) {
                this.mFetchBlogInfosTask.cancel(true);
            }
            super.onBackPressed();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        if (this.mBrowsingThreadInfo != null) {
            Log.i(this.LOG_TAG, "onSaveInstanceState(): Saving current Thread browsing status into Bundle...");
            outState.putBoolean(getString(R.string.state_key_was_browsing_thread), true);
            putThreadInfoIntoBundle(this.mBrowsingThreadInfo, outState);
            outState.putBoolean(getString(R.string.state_key_is_thread_author_user_avatar_ready), false);
            outState.putInt(getString(R.string.state_key_subforum_group_index), this.mSubforumGroupIndex);
            outState.putInt(getString(R.string.state_key_subforum_index), this.mSubforumIndex);
            outState.putInt(getString(R.string.state_key_subforum_id), this.mSubforumID);
            outState.putString(getString(R.string.state_key_subforum_title), this.mSubforumTitle);
        } else if (this.mBrowsingBlogInfo != null) {
            Log.i(this.LOG_TAG, "onSaveInstanceState(): Saving current Blog browsing status into Bundle...");
            outState.putBoolean(getString(R.string.state_key_was_browsing_blog), true);
            putBlogInfoIntoBundle(this.mBrowsingBlogInfo, outState);
        } else {
            outState.putBoolean(getString(R.string.state_key_was_browsing_thread), false);
            outState.putBoolean(getString(R.string.state_key_was_browsing_blog), false);
            outState.putInt(getString(R.string.state_key_subforum_group_index), this.mSubforumGroupIndex);
        }
        super.onSaveInstanceState(outState);
    }

    protected void onStop() {
        super.onStop();
        if (this.mIsLoadingUserAvatarFromCache) {
            this.mLoadUserAvatarFromCacheTask.cancel(false);
            this.mLoadUserAvatarFromCacheTask = null;
            this.mIsLoadingUserAvatarFromCache = false;
        }
        if (this.mIsGettingUserAvatar) {
            this.mGetUserAvatarTask.cancel(false);
            this.mGetUserAvatarTask = null;
            this.mIsGettingUserAvatar = false;
        }
        if (this.mIsFetchingSubforumThreadInfos) {
            this.mFetchSubforumThreadInfosTask.cancel(false);
            this.mFetchSubforumThreadInfosTask = null;
            this.mIsFetchingSubforumThreadInfos = false;
        }
        if (this.mIsFetchingBlogInfos) {
            this.mFetchBlogInfosTask.cancel(false);
            this.mFetchBlogInfosTask = null;
            this.mIsFetchingBlogInfos = false;
        }
        if (this.mIsPostingNewThread) {
            this.mPostNewThreadTask.cancel(false);
            this.mPostNewThreadTask = null;
            this.mIsPostingNewThread = false;
        }
    }
}
