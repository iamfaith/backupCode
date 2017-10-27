package naco_siren.github.a1point3acres.activities.blog_activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import cn.bmob.v3.Bmob;
import naco_siren.github.a1point3acres.R;
import naco_siren.github.a1point3acres.activities.settings_activity.SettingsActivity;
import naco_siren.github.a1point3acres.bmob.record.UpdateRecordTask;
import naco_siren.github.a1point3acres.bmob.record.UpdateType;
import naco_siren.github.a1point3acres.html_parsers.ParseBlog;
import naco_siren.github.http_utils.HttpErrorType;
import naco_siren.github.http_utils.HttpGET;
import org.apache.http.protocol.HTTP;
import org.jsoup.nodes.Document;

public class BlogActivity extends AppCompatActivity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final String LOG_TAG = BlogActivity.class.getSimpleName();
    static final int REQUEST_SETTINGS = 0;
    private static final int UI_ANIMATION_DELAY = 300;
    private final int FETCH_BLOG_MAX_RETRY_TIMES = 2;
    private final boolean OUTPUT_DEBUG_INFO = false;
    public final int RELOAD_FAILURE_TASK_ALREADY_RUNNING = 1;
    private String mBlogBody;
    private TextView mBlogBodyTextView;
    private View mBlogContentView;
    private String mBlogDateDay;
    private TextView mBlogDateDayTextView;
    private String mBlogDateMonth;
    private TextView mBlogDateMonthTextView;
    private String mBlogDateYear;
    private TextView mBlogDateYearTextView;
    private String mBlogHref;
    private String mBlogIntro;
    private String mBlogTitle;
    private TextView mBlogTitleTextView;
    private String mCookie;
    private final OnTouchListener mDelayHideTouchListener = new OnTouchListener() {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            BlogActivity.this.delayedHide(BlogActivity.AUTO_HIDE_DELAY_MILLIS);
            return false;
        }
    };
    private FetchBlogTask mFetchBlogTask;
    private FrameLayout mFrameLayout;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint({"InlinedApi"})
        public void run() {
            BlogActivity.this.mBlogContentView.setSystemUiVisibility(4871);
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        public void run() {
            BlogActivity.this.hide();
        }
    };
    private boolean mIsFetchingBlog;
    private final Runnable mShowPart2Runnable = new Runnable() {
        public void run() {
            ActionBar actionBar = BlogActivity.this.getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mUserID;
    private boolean mVisible;

    private class FetchBlogTask extends AsyncTask<Void, Void, Boolean> {
        private HttpErrorType mErrorCode;
        private ParseBlog mParseBlog;

        private FetchBlogTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(BlogActivity.LOG_TAG, "FetchBlogTask instantiated, blog href: " + BlogActivity.this.mBlogHref);
            BlogActivity.this.mIsFetchingBlog = BlogActivity.AUTO_HIDE;
        }

        protected Boolean doInBackground(Void... params) {
            for (int i = 0; i < 2; i++) {
                try {
                    String href = BlogActivity.this.mBlogHref;
                    Log.v(BlogActivity.LOG_TAG, "Feteching blog : " + href + ", trial " + i);
                    HttpGET httpGET = new HttpGET(href, BlogActivity.this.mCookie, HTTP.UTF_8);
                    Document doc = httpGET.getJsoupDocument();
                    if (doc == null) {
                        HttpErrorType httpGetInnerErrorCode = httpGET.getErrorCode();
                        if (httpGetInnerErrorCode != HttpErrorType.SUCCESS) {
                            this.mErrorCode = httpGetInnerErrorCode;
                        } else {
                            this.mErrorCode = HttpErrorType.ERROR_JSOUP_PARSE_FAILURE;
                        }
                    } else {
                        Log.v(BlogActivity.LOG_TAG, "Feteched webpage @ blog: " + href + "\nStart parsing.");
                        this.mParseBlog = new ParseBlog(doc);
                        this.mParseBlog.setOutputBlogContent(false);
                        this.mErrorCode = this.mParseBlog.execute();
                        if (this.mErrorCode == HttpErrorType.SUCCESS) {
                            Log.v(BlogActivity.LOG_TAG, "Successfully parsed webpage @ blog: " + href);
                            return Boolean.valueOf(BlogActivity.AUTO_HIDE);
                        } else if (this.mErrorCode == HttpErrorType.ERROR_BLOG_PARSE_FAILURE) {
                            Log.e(BlogActivity.LOG_TAG, "PARSE FAILURE! @ blog: " + href);
                            return Boolean.valueOf(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
                    Log.e(BlogActivity.LOG_TAG, "UNKNOWN EXCEPTION in FetchBlogTask @ blog:" + BlogActivity.this.mBlogHref + ", trial " + i);
                }
            }
            return Boolean.valueOf(false);
        }

        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            BlogActivity.this.mIsFetchingBlog = false;
            BlogActivity.this.mFetchBlogTask = null;
            super.onPostExecute(success);
            BlogActivity.this.showProgress(false);
            if (success.booleanValue()) {
                BlogActivity.this.mBlogBody = this.mParseBlog.getBlogContentPureText();
                if (BlogActivity.this.mBlogBody != null) {
                    BlogActivity.this.mBlogBodyTextView.setText(BlogActivity.this.mBlogBody);
                }
                BlogActivity.this.mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                    public void onRefresh() {
                        BlogActivity.this.reloadBlog(BlogActivity.this.getString(R.string.toast_refresh_blog_progress));
                    }
                });
                Snackbar.make(BlogActivity.this.mFrameLayout, BlogActivity.this.getString(R.string.toast_load_blog_sucess), -1).show();
                return;
            }
            CharSequence errorMessage;
            Log.e(BlogActivity.LOG_TAG, "Fetching & parsing blog failed! Blog href: " + BlogActivity.this.mBlogHref);
            BlogActivity.this.mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                public void onRefresh() {
                    BlogActivity.this.reloadBlog(BlogActivity.this.getString(R.string.toast_load_blog_progress));
                }
            });
            switch (this.mErrorCode) {
                case ERROR_NETWORK:
                    errorMessage = BlogActivity.this.getString(R.string.error_no_network);
                    break;
                case ERROR_CONNECTION:
                    errorMessage = BlogActivity.this.getString(R.string.error_connection_failure);
                    break;
                case ERROR_TIMEOUT:
                    errorMessage = BlogActivity.this.getString(R.string.error_socket_timeout);
                    break;
                default:
                    errorMessage = BlogActivity.this.getString(R.string.toast_load_blog_failure);
                    break;
            }
            Snackbar.make(BlogActivity.this.mFrameLayout, errorMessage, 0).show();
        }

        protected void onCancelled() {
            super.onCancelled();
            BlogActivity.this.mIsFetchingBlog = false;
            BlogActivity.this.mFetchBlogTask = null;
            Log.v(BlogActivity.LOG_TAG, "FetchBlogTask cancelled, target blog href: " + BlogActivity.this.mBlogHref);
            BlogActivity.this.showProgress(false);
            super.onCancelled();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_blog);
        this.mVisible = AUTO_HIDE;
        this.mIsFetchingBlog = false;
        SharedPreferences mAccountPref = getSharedPreferences(getString(R.string.account_shared_preference), 0);
        mAccountPref.edit().putInt(getString(R.string.account_key_blog_startup_count), mAccountPref.getInt(getString(R.string.account_key_blog_startup_count), 0) + 1).commit();
        initUI(savedInstanceState);
        reloadBlog(getString(R.string.toast_load_blog_progress));
        Bmob.initialize(this, "d68a0f573a01d416a3dd10223c3bbcfc");
        if (this.mUserID == null) {
            this.mUserID = getSharedPreferences(getString(R.string.account_shared_preference), 0).getString(getString(R.string.account_key_user_id), null);
        }
        new UpdateRecordTask(this.mUserID, UpdateType.BLOG, this.mBlogTitle).execute(new Void[0]);
    }

    public void initUI(Bundle savedInstanceState) {
        Bundle bundle;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(AUTO_HIDE);
        }
        this.mFrameLayout = (FrameLayout) findViewById(R.id.blog_framelayout);
        this.mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.blog_swipe_refresh_layout);
        this.mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        this.mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                BlogActivity.this.reloadBlog(BlogActivity.this.getString(R.string.toast_load_blog_progress));
            }
        });
        this.mBlogContentView = findViewById(R.id.blog_content);
        this.mBlogContentView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                BlogActivity.this.toggle();
            }
        });
        this.mBlogTitleTextView = (TextView) findViewById(R.id.blog_title);
        this.mBlogDateMonthTextView = (TextView) findViewById(R.id.blog_date_month);
        this.mBlogDateDayTextView = (TextView) findViewById(R.id.blog_date_day);
        this.mBlogDateYearTextView = (TextView) findViewById(R.id.blog_date_year);
        this.mBlogBodyTextView = (TextView) findViewById(R.id.blog_body);
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        } else {
            Intent intent = getIntent();
            if (intent == null) {
                Log.e(LOG_TAG, "Intent expected from MainActivity not found!");
                Snackbar.make(this.mFrameLayout, getString(R.string.error_unknown), 0).show();
                return;
            }
            bundle = intent.getExtras();
        }
        this.mCookie = bundle.getString(getString(R.string.state_key_cookie));
        this.mUserID = bundle.getString(getString(R.string.state_key_user_id));
        this.mBlogHref = bundle.getString(getString(R.string.state_key_blog_href));
        this.mBlogTitle = bundle.getString(getString(R.string.state_key_blog_title));
        this.mBlogIntro = bundle.getString(getString(R.string.state_key_blog_intro));
        this.mBlogDateMonth = bundle.getString(getString(R.string.state_key_blog_date_month));
        this.mBlogDateDay = bundle.getString(getString(R.string.state_key_blog_date_day));
        this.mBlogDateYear = bundle.getString(getString(R.string.state_key_blog_date_year));
        this.mBlogTitleTextView.setText(this.mBlogTitle);
        this.mBlogBodyTextView.setText(this.mBlogIntro);
        this.mBlogDateMonthTextView.setText(this.mBlogDateMonth);
        this.mBlogDateDayTextView.setText(this.mBlogDateDay);
        this.mBlogDateYearTextView.setText(this.mBlogDateYear);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    public int reloadBlog(String notificationMessage) {
        if (this.mIsFetchingBlog) {
            return 1;
        }
        showProgress(AUTO_HIDE);
        Snackbar.make(this.mFrameLayout, (CharSequence) notificationMessage, -1).show();
        this.mFetchBlogTask = new FetchBlogTask();
        this.mFetchBlogTask.execute(new Void[0]);
        return 0;
    }

    @TargetApi(13)
    private void showProgress(final boolean showProgress) {
        Log.v(LOG_TAG, "Show Background Progress Animation: " + (showProgress ? "GO!" : "STOP"));
        if (this.mSwipeRefreshLayout != null) {
            this.mSwipeRefreshLayout.post(new Runnable() {
                public void run() {
                    BlogActivity.this.mSwipeRefreshLayout.setRefreshing(showProgress);
                }
            });
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blog, menu);
        return AUTO_HIDE;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == 16908332) {
            onBackPressed();
            return AUTO_HIDE;
        } else if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 0);
            return AUTO_HIDE;
        } else if (id == R.id.action_thread_refresh) {
            if (this.mIsFetchingBlog) {
                toggle();
                Log.v(LOG_TAG, "Refresh action in options selected, but FetchBlogTask is already running.");
                return AUTO_HIDE;
            }
            toggle();
            Log.v(LOG_TAG, "Refresh action in options selected, start refreshing blog.");
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    BlogActivity.this.reloadBlog(BlogActivity.this.getString(R.string.toast_refresh_blog_swipe_hint));
                }
            }, 300);
            return AUTO_HIDE;
        } else if (id != R.id.action_share_blog) {
            return super.onOptionsItemSelected(item);
        } else {
            Intent sendIntent = new Intent();
            sendIntent.setAction("android.intent.action.SEND");
            sendIntent.putExtra("android.intent.extra.TEXT", ParseBlog.generateBlogShareText(this.mBlogTitle, this.mBlogHref));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return AUTO_HIDE;
        }
    }

    private void toggle() {
        if (this.mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        this.mVisible = false;
        this.mHideHandler.removeCallbacks(this.mShowPart2Runnable);
        this.mHideHandler.postDelayed(this.mHidePart2Runnable, 300);
    }

    @SuppressLint({"InlinedApi"})
    private void show() {
        this.mBlogContentView.setSystemUiVisibility(1536);
        this.mVisible = AUTO_HIDE;
        this.mHideHandler.removeCallbacks(this.mHidePart2Runnable);
        this.mHideHandler.postDelayed(this.mShowPart2Runnable, 300);
    }

    private void delayedHide(int delayMillis) {
        this.mHideHandler.removeCallbacks(this.mHideRunnable);
        this.mHideHandler.postDelayed(this.mHideRunnable, (long) delayMillis);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == 1) {
            setResult(1);
            finish();
        }
    }

    public void onBackPressed() {
        setResult(-1);
        ActivityCompat.finishAfterTransition(this);
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(getString(R.string.state_key_cookie), this.mCookie);
        outState.putString(getString(R.string.state_key_user_id), this.mUserID);
        outState.putString(getString(R.string.state_key_blog_href), this.mBlogHref);
        outState.putString(getString(R.string.state_key_blog_title), this.mBlogTitle);
        outState.putString(getString(R.string.state_key_blog_intro), this.mBlogIntro);
        outState.putString(getString(R.string.state_key_blog_date_month), this.mBlogDateMonth);
        outState.putString(getString(R.string.state_key_blog_date_day), this.mBlogDateDay);
        outState.putString(getString(R.string.state_key_blog_date_year), this.mBlogDateYear);
        Log.i(LOG_TAG, "Saved all Blog states into outState.");
        super.onSaveInstanceState(outState);
    }

    protected void onStop() {
        super.onStop();
        if (this.mIsFetchingBlog == AUTO_HIDE) {
            this.mIsFetchingBlog = false;
            this.mFetchBlogTask.cancel(false);
            this.mFetchBlogTask = null;
        }
    }
}
