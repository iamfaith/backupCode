package naco_siren.github.a1point3acres.activities.thread_activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.v3.Bmob;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import naco_siren.github.a1point3acres.R;
import naco_siren.github.a1point3acres.activities.settings_activity.SettingsActivity;
import naco_siren.github.a1point3acres.activities.thread_activity.fragments.ThreadCommentFragment;
import naco_siren.github.a1point3acres.activities.thread_activity.fragments.ThreadCommentFragment.OnCommentFragmentInteractionListener;
import naco_siren.github.a1point3acres.activities.thread_activity.fragments.ThreadContentFragment;
import naco_siren.github.a1point3acres.activities.thread_activity.fragments.ThreadContentFragment.OnContentFragmentInteractionListener;
import naco_siren.github.a1point3acres.bmob.record.UpdateRecordTask;
import naco_siren.github.a1point3acres.bmob.record.UpdateType;
import naco_siren.github.a1point3acres.contents.ThreadComment;
import naco_siren.github.a1point3acres.html_parsers.ParseThread;
import naco_siren.github.a1point3acres.http_actions.POSTReply;
import naco_siren.github.http_utils.HttpErrorType;
import naco_siren.github.http_utils.HttpGET;
import naco_siren.github.ui_utils.VpSwipeRefreshLayout;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;

public class ThreadActivity extends AppCompatActivity implements OnContentFragmentInteractionListener, OnCommentFragmentInteractionListener, OnPageChangeListener {
    static final int REQUEST_SETTINGS = 0;
    private final long DOUBLE_CLICK_INTERVAL = 1350;
    private final int FETCH_THREAD_MAX_RETRY_TIMES = 2;
    private final String LOG_TAG = ThreadActivity.class.getSimpleName();
    private final boolean OUTPUT_DEBUG_INFO = false;
    public final int POST_REPLY_TASK_ALREADY_RUNNING = 1;
    public final int RELOAD_FAILURE_TASK_ALREADY_RUNNING = 1;
    private AlertDialog mAlertDialog;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private String mCookie;
    private CoordinatorLayout mCoordinatorLayout;
    private EditText mEditText;
    private String mErrorMessage;
    private long mExitTime = 0;
    private Context mFABContext;
    private FetchThreadTask mFetchThreadTask;
    private FloatingActionButton mFloatingActionButton;
    private FloatingActionButtonClickEvent mFloatingActionButtonClickEvent;
    private String mFormHash;
    private FragmentManager mFragmentManager;
    private boolean mIsFetchingThread;
    private boolean mIsGuest;
    private boolean mIsPostingReply;
    private String mLastInputText;
    private Button mNegativeButton;
    private Button mPositiveButton;
    private PostReplyTask mPostReplyTask;
    private ThreadComment mReplyThreadComment;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int mSubforumID;
    private String mSubforumTitle;
    private TabLayout mTabLayout;
    private boolean mTextQualified;
    private ImageView mThreadAgreedImageView;
    private ImageView mThreadAttachedImageView;
    private String mThreadAuthorID;
    private String mThreadAuthorName;
    private CircularImageView mThreadAuthorUserAvatarImageView;
    private TextView mThreadAuthorUserNameTextView;
    private long mThreadCommentCount;
    private TextView mThreadCommentCountTextView;
    private ThreadCommentFragment mThreadCommentFragment;
    private ArrayList<ThreadComment> mThreadComments;
    private ThreadContentFragment mThreadContentFragment;
    private ArrayList<String> mThreadContentImgHrefs;
    private String mThreadContentPureText;
    private String mThreadDate;
    private TextView mThreadDateTextView;
    private ImageView mThreadDigestImageView;
    private ImageView mThreadHotImageView;
    private String mThreadID;
    private View mThreadInfoRootView;
    private boolean mThreadIsAgreed;
    private boolean mThreadIsAttached;
    private boolean mThreadIsDigest;
    private boolean mThreadIsHot;
    private boolean mThreadIsLocked;
    private boolean mThreadIsPinned;
    private boolean mThreadIsRecommended;
    private ImageView mThreadLockedImageView;
    private ImageView mThreadNewImageView;
    private int mThreadPageCount;
    private int mThreadParsedPageCount;
    private ImageView mThreadPinnedImageView;
    private long mThreadReadCount;
    private TextView mThreadReadCountTextView;
    private ImageView mThreadRecommendedImageView;
    private String mThreadTitle;
    private TextView mThreadTitleTextView;
    private String mThreadType;
    private TextView mThreadTypeTextView;
    private Toolbar mToolbar;
    private String mUserID;
    private ViewPager mViewPager;
    private VpSwipeRefreshLayout mVpSwipeRefreshLayout;

    public class FetchThreadTask extends AsyncTask<Integer, Void, Boolean> {
        private HttpErrorType mErrorCode;
        private ParseThread mParseThread;
        private int mTargetThreadPageIndex;

        protected void onPreExecute() {
            super.onPreExecute();
            Log.v(ThreadActivity.this.LOG_TAG, "FetchThreadTask instantiated, target thread id:" + ThreadActivity.this.mThreadID);
            ThreadActivity.this.mIsFetchingThread = true;
            if (!ThreadActivity.this.mIsGuest) {
                ThreadActivity.this.mFloatingActionButton.hide();
            }
        }

        protected Boolean doInBackground(Integer... params) {
            this.mTargetThreadPageIndex = ThreadActivity.this.mThreadParsedPageCount + 1;
            for (int i = 0; i < 2; i++) {
                try {
                    String href = ParseThread.getThreadHref(ThreadActivity.this.mThreadID, this.mTargetThreadPageIndex);
                    Log.v(ThreadActivity.this.LOG_TAG, "Feteching webpage @ Thread #" + ThreadActivity.this.mThreadID + ": " + href + ", trial " + i);
                    HttpGET httpGET = new HttpGET(href, ThreadActivity.this.mCookie, "GBK");
                    Document doc = httpGET.getJsoupDocument();
                    if (doc == null) {
                        HttpErrorType httpGetInnerErrorCode = httpGET.getErrorCode();
                        if (httpGetInnerErrorCode != HttpErrorType.SUCCESS) {
                            this.mErrorCode = httpGetInnerErrorCode;
                        } else {
                            this.mErrorCode = HttpErrorType.ERROR_JSOUP_PARSE_FAILURE;
                        }
                    } else {
                        Log.v(ThreadActivity.this.LOG_TAG, "Feteched webpage @ Thread #" + ThreadActivity.this.mThreadID + ": " + href + "\nStart parsing.");
                        this.mParseThread = new ParseThread(ThreadActivity.this.mThreadID, this.mTargetThreadPageIndex, doc, ThreadActivity.this.mThreadComments.size());
                        this.mParseThread.setOutputThreadContent(false);
                        this.mParseThread.setOutputThreadComment(false);
                        this.mErrorCode = this.mParseThread.execute();
                        if (this.mErrorCode == HttpErrorType.SUCCESS) {
                            Log.v(ThreadActivity.this.LOG_TAG, "Successfully parsed webpage @ Thread #" + ThreadActivity.this.mThreadID + ": " + href);
                            if (this.mTargetThreadPageIndex == 1) {
                                ThreadActivity.this.mThreadContentPureText = this.mParseThread.getThreadContentPureText();
                                ThreadActivity.this.mThreadContentImgHrefs = this.mParseThread.getThreadContentImgHrefs();
                                ThreadActivity.this.mThreadPageCount = this.mParseThread.getThreadPageCount();
                                ThreadActivity.this.mFormHash = this.mParseThread.getThreadFormHash();
                            }
                            return Boolean.valueOf(true);
                        } else if (this.mErrorCode == HttpErrorType.ERROR_THREADINFOLIST_PARSE_FAILURE) {
                            Log.e(ThreadActivity.this.LOG_TAG, "PARSE FAILURE! @ Thread #" + ThreadActivity.this.mThreadID + ": " + href);
                            return Boolean.valueOf(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.mErrorCode = HttpErrorType.ERROR_UNKNOWN;
                    Log.e(ThreadActivity.this.LOG_TAG, "UNKNOWN EXCEPTION in FetchThreadTask, #" + ThreadActivity.this.mThreadID + ", trial " + i);
                }
            }
            return Boolean.valueOf(false);
        }

        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            ThreadActivity.this.mIsFetchingThread = false;
            ThreadActivity.this.mFetchThreadTask = null;
            ThreadActivity.this.showProgress(false, false);
            if (success.booleanValue()) {
                ThreadActivity.this.mThreadContentFragment.refreshUI(ThreadActivity.this.mThreadContentPureText, ThreadActivity.this.mThreadContentImgHrefs);
                ArrayList<ThreadComment> newThreadComments = this.mParseThread.getThreadComments();
                ThreadActivity.this.mThreadComments.addAll(newThreadComments);
                if (this.mTargetThreadPageIndex == 1) {
                    ThreadActivity.this.mThreadCommentFragment.resetData(ThreadActivity.this.mThreadComments);
                    ThreadActivity.this.mThreadCommentFragment.addOnScrollListener();
                }
                ThreadActivity.this.mThreadCommentFragment.notifyDataChanged();
                if (ThreadActivity.this.mViewPager.getCurrentItem() == 1 && !ThreadActivity.this.mIsGuest) {
                    ThreadActivity.this.mFloatingActionButton.show();
                }
                ThreadActivity.this.mVpSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                    public void onRefresh() {
                        ThreadActivity.this.reloadThread(ThreadActivity.this.getString(R.string.toast_refresh_thread_progress));
                    }
                });
                Snackbar.make(ThreadActivity.this.mCoordinatorLayout, ThreadActivity.this.getString(R.string.toast_load_thread_sucess), -1).show();
                Log.d(ThreadActivity.this.LOG_TAG, "Added " + newThreadComments.size() + " new ThreadComments from thread #" + ThreadActivity.this.mThreadID + ": " + ThreadActivity.this.mThreadTitle + ", page " + this.mTargetThreadPageIndex);
                Log.v(ThreadActivity.this.LOG_TAG, "Now altogether " + ThreadActivity.this.mThreadComments.size() + " ThreadComments.");
                ThreadActivity.this.mThreadParsedPageCount = ThreadActivity.this.mThreadParsedPageCount + 1;
                return;
            }
            CharSequence errorMessage;
            Log.e(ThreadActivity.this.LOG_TAG, "Fetching & parsing Thread failed! Thread #" + ThreadActivity.this.mThreadID + ", page " + this.mTargetThreadPageIndex);
            ThreadActivity.this.mVpSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                public void onRefresh() {
                    ThreadActivity.this.reloadThread(ThreadActivity.this.getString(R.string.toast_load_thread_progress));
                }
            });
            switch (this.mErrorCode) {
                case ERROR_NETWORK:
                    errorMessage = ThreadActivity.this.getString(R.string.error_no_network);
                    break;
                case ERROR_CONNECTION:
                    errorMessage = ThreadActivity.this.getString(R.string.error_connection_failure);
                    break;
                case ERROR_TIMEOUT:
                    errorMessage = ThreadActivity.this.getString(R.string.error_socket_timeout);
                    break;
                default:
                    errorMessage = ThreadActivity.this.getString(R.string.toast_load_thread_failure);
                    break;
            }
            Snackbar.make(ThreadActivity.this.mCoordinatorLayout, errorMessage, 0).show();
        }

        protected void onCancelled() {
            ThreadActivity.this.mIsFetchingThread = false;
            ThreadActivity.this.mFetchThreadTask = null;
            Log.v(ThreadActivity.this.LOG_TAG, "FetchThreadTask cancelled, target thread id: " + ThreadActivity.this.mThreadID + ", page " + this.mTargetThreadPageIndex);
            ThreadActivity.this.showProgress(false, false);
            super.onCancelled();
        }
    }

    public class FloatingActionButtonClickEvent implements OnClickListener {
        public FloatingActionButtonClickEvent(Context context) {
            ThreadActivity.this.mFABContext = context;
        }

        public void onClick(View v) {
            if (v == ThreadActivity.this.mFloatingActionButton) {
                if (!ThreadActivity.this.mIsPostingReply) {
                    ThreadActivity.this.mReplyThreadComment = null;
                    Builder builder = new Builder(ThreadActivity.this.mFABContext);
                    builder.setTitle(ThreadActivity.this.getString(R.string.post_thread_reply_content_input_title));
                    builder.setView((int) R.layout.post_thread_reply_input);
                    builder.setPositiveButton(ThreadActivity.this.getString(R.string.post_thread_reply_input_confirm_yes), null);
                    builder.setNegativeButton(ThreadActivity.this.getString(R.string.post_thread_reply_input_confirm_no), null);
                    ThreadActivity.this.mAlertDialog = builder.show();
                    ThreadActivity.this.mAlertDialog.setCanceledOnTouchOutside(false);
                    ThreadActivity.this.mAlertDialog.setCancelable(false);
                    ThreadActivity.this.mEditText = (EditText) ThreadActivity.this.mAlertDialog.findViewById(R.id.post_thread_reply_message_input);
                    ThreadActivity.this.mPositiveButton = ThreadActivity.this.mAlertDialog.getButton(-1);
                    ThreadActivity.this.mPositiveButton.setOnClickListener(this);
                    ThreadActivity.this.mNegativeButton = ThreadActivity.this.mAlertDialog.getButton(-2);
                    ThreadActivity.this.mNegativeButton.setOnClickListener(this);
                }
            } else if (v == ThreadActivity.this.mPositiveButton) {
                ThreadActivity.this.mLastInputText = ThreadActivity.this.mEditText.getText().toString();
                ThreadActivity.this.mTextQualified = true;
                if (ThreadActivity.this.mLastInputText == null || ThreadActivity.this.mLastInputText.trim().length() == 0 || StringUtil.isBlank(ThreadActivity.this.mLastInputText)) {
                    ThreadActivity.this.mTextQualified = false;
                    ThreadActivity.this.mErrorMessage = ThreadActivity.this.getString(R.string.post_thread_reply_error_text_empty);
                } else if (ThreadActivity.this.mLastInputText.length() < 8) {
                    ThreadActivity.this.mTextQualified = false;
                    ThreadActivity.this.mErrorMessage = ThreadActivity.this.getString(R.string.post_thread_reply_error_too_short);
                }
                if (ThreadActivity.this.mTextQualified) {
                    ThreadActivity.this.mPostReplyTask = new PostReplyTask(ThreadActivity.this.mLastInputText, ThreadActivity.this.mReplyThreadComment);
                    ThreadActivity.this.mPostReplyTask.execute(new Void[0]);
                    ThreadActivity.this.mAlertDialog.dismiss();
                    return;
                }
                ThreadActivity.this.mEditText.setError(ThreadActivity.this.mErrorMessage);
            } else if (v == ThreadActivity.this.mNegativeButton) {
                resetStatus();
                ThreadActivity.this.mAlertDialog.cancel();
                Snackbar.make(ThreadActivity.this.mCoordinatorLayout, ThreadActivity.this.getString(R.string.toast_reply_cancelled), -1).show();
            }
        }

        public void onPostSuccessCallback() {
            resetStatus();
        }

        public void resetStatus() {
            ThreadActivity.this.mReplyThreadComment = null;
            ThreadActivity.this.mLastInputText = null;
            ThreadActivity.this.mTextQualified = false;
        }
    }

    public class PostReplyTask extends AsyncTask<Void, Void, Boolean> {
        private HttpErrorType mErrorCode;
        private String mReplyMessage;
        private ThreadComment mThreadComment;

        public PostReplyTask(String replyMessage, ThreadComment threadComment) {
            this.mReplyMessage = replyMessage;
            this.mThreadComment = threadComment;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(ThreadActivity.this.LOG_TAG, "PostReplyTask instantiated" + (this.mThreadComment == null ? ". " : ", target post id:" + this.mThreadComment));
            ThreadActivity.this.mIsPostingReply = true;
            ThreadActivity.this.showProgress(true, false);
            ThreadActivity.this.mFloatingActionButton.hide();
        }

        protected Boolean doInBackground(Void... params) {
            this.mErrorCode = new POSTReply(ThreadActivity.this.mCookie, ThreadActivity.this.mSubforumID, ThreadActivity.this.mThreadID, ThreadActivity.this.mFormHash, this.mReplyMessage, this.mThreadComment).execute();
            if (this.mErrorCode == HttpErrorType.SUCCESS) {
                return Boolean.valueOf(true);
            }
            return Boolean.valueOf(false);
        }

        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            ThreadActivity.this.mIsPostingReply = false;
            ThreadActivity.this.mPostReplyTask = null;
            ThreadActivity.this.showProgress(false, false);
            if (ThreadActivity.this.mViewPager.getCurrentItem() == 1) {
                ThreadActivity.this.mFloatingActionButton.show();
            }
            if (success.booleanValue()) {
                String str;
                ThreadActivity.this.mFloatingActionButtonClickEvent.onPostSuccessCallback();
                Snackbar.make(ThreadActivity.this.mCoordinatorLayout, ThreadActivity.this.getString(R.string.toast_reply_sucess), -1).show();
                String access$1600 = ThreadActivity.this.LOG_TAG;
                StringBuilder append = new StringBuilder().append("PostReplyTask succeeded! ");
                if (this.mThreadComment == null) {
                    str = ". ";
                } else {
                    str = ", target post id:" + this.mThreadComment;
                }
                Log.d(access$1600, append.append(str).toString());
                return;
            }
            CharSequence errorMessage;
            switch (this.mErrorCode) {
                case ERROR_NETWORK:
                    errorMessage = ThreadActivity.this.getString(R.string.error_no_network);
                    break;
                case ERROR_CONNECTION:
                    errorMessage = ThreadActivity.this.getString(R.string.error_connection_failure);
                    break;
                case ERROR_TIMEOUT:
                    errorMessage = ThreadActivity.this.getString(R.string.error_socket_timeout);
                    break;
                default:
                    errorMessage = ThreadActivity.this.getString(R.string.toast_reply_failure);
                    break;
            }
            Snackbar.make(ThreadActivity.this.mCoordinatorLayout, errorMessage, 0).show();
        }

        protected void onCancelled() {
            super.onCancelled();
            ThreadActivity.this.mIsPostingReply = false;
            ThreadActivity.this.mPostReplyTask = null;
            Log.v(ThreadActivity.this.LOG_TAG, "PostReplyTask cancelled! " + (this.mThreadComment == null ? ". " : ", target post id:" + this.mThreadComment));
            ThreadActivity.this.showProgress(false, false);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    ThreadActivity.this.mThreadContentFragment = ThreadContentFragment.newInstance();
                    return ThreadActivity.this.mThreadContentFragment;
                case 1:
                    ThreadActivity.this.mThreadCommentFragment = ThreadCommentFragment.newInstance();
                    return ThreadActivity.this.mThreadCommentFragment;
                default:
                    return null;
            }
        }

        public Object instantiateItem(ViewGroup container, int position) {
            Object fragment = super.instantiateItem(container, position);
            switch (position) {
                case 0:
                    ThreadActivity.this.mThreadContentFragment = (ThreadContentFragment) fragment;
                    break;
                case 1:
                    ThreadActivity.this.mThreadCommentFragment = (ThreadCommentFragment) fragment;
                    break;
            }
            return fragment;
        }

        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return ThreadActivity.this.getString(R.string.section_thread_tab_content);
                case 1:
                    return ThreadActivity.this.getString(R.string.section_thread_tab_comment);
                default:
                    return null;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_thread);
        this.mIsFetchingThread = false;
        SharedPreferences mAccountPref = getSharedPreferences(getString(R.string.account_shared_preference), 0);
        mAccountPref.edit().putInt(getString(R.string.account_key_thread_startup_count), mAccountPref.getInt(getString(R.string.account_key_thread_startup_count), 0) + 1).commit();
        initUI(savedInstanceState);
        reloadThread(getString(R.string.toast_load_thread_progress));
        Bmob.initialize(this, "d68a0f573a01d416a3dd10223c3bbcfc");
        if (this.mUserID == null) {
            this.mUserID = getSharedPreferences(getString(R.string.account_shared_preference), 0).getString(getString(R.string.account_key_user_id), null);
        }
        new UpdateRecordTask(this.mUserID, UpdateType.THREAD, this.mThreadID).execute(new Void[0]);
    }

    public void initUI(Bundle savedInstanceState) {
        Bundle bundle;
        int i;
        this.mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.thread_coordinator_layout);
        this.mAppBarLayout = (AppBarLayout) findViewById(R.id.thread_appbar);
        this.mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.thread_collapsing_toolbar_layout);
        this.mCollapsingToolbarLayout.setTitleEnabled(false);
        this.mToolbar = (Toolbar) findViewById(R.id.thread_toolbar);
        setSupportActionBar(this.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        } else {
            Intent intent = getIntent();
            if (intent == null) {
                Log.e(this.LOG_TAG, "Intent expected from MainActivity not found!");
                Snackbar.make(this.mAppBarLayout, getString(R.string.error_unknown), 0).show();
                return;
            }
            Log.v(this.LOG_TAG, "Retrieving info from Intent...");
            bundle = intent.getExtras();
        }
        this.mIsGuest = bundle.getBoolean(getString(R.string.state_key_user_is_guest), false);
        this.mCookie = bundle.getString(getString(R.string.state_key_cookie));
        this.mUserID = bundle.getString(getString(R.string.state_key_user_id));
        this.mSubforumID = bundle.getInt(getString(R.string.state_key_subforum_id));
        this.mSubforumTitle = bundle.getString(getString(R.string.state_key_subforum_title));
        this.mThreadID = bundle.getString(getString(R.string.state_key_thread_id));
        this.mThreadAuthorName = bundle.getString(getString(R.string.state_key_thread_author_user_name));
        this.mThreadAuthorID = bundle.getString(getString(R.string.state_key_thread_author_user_id));
        this.mThreadDate = bundle.getString(getString(R.string.state_key_thread_date));
        this.mThreadTitle = bundle.getString(getString(R.string.state_key_thread_title));
        this.mThreadType = bundle.getString(getString(R.string.state_key_thread_type));
        this.mThreadReadCount = bundle.getLong(getString(R.string.state_key_thread_read_count));
        this.mThreadCommentCount = bundle.getLong(getString(R.string.state_key_thread_comment_count));
        this.mThreadIsLocked = bundle.getBoolean(getString(R.string.state_key_thread_is_locked));
        this.mThreadIsAttached = bundle.getBoolean(getString(R.string.state_key_thread_is_attached));
        this.mThreadIsPinned = bundle.getBoolean(getString(R.string.state_key_thread_is_pinned));
        this.mThreadIsDigest = bundle.getBoolean(getString(R.string.state_key_thread_is_digest));
        this.mThreadIsRecommended = bundle.getBoolean(getString(R.string.state_key_thread_is_recommended));
        this.mThreadIsHot = bundle.getBoolean(getString(R.string.state_key_thread_is_hot));
        this.mThreadIsAgreed = bundle.getBoolean(getString(R.string.state_key_thread_is_agreed));
        this.mThreadInfoRootView = findViewById(R.id.thread_threadinfo_root);
        this.mThreadAuthorUserAvatarImageView = (CircularImageView) findViewById(R.id.thread_threadinfo_author_useravatar);
        this.mThreadAuthorUserAvatarImageView.setBorderWidth(0.0f);
        this.mThreadAuthorUserNameTextView = (TextView) findViewById(R.id.thread_threadinfo_author_username);
        this.mThreadDateTextView = (TextView) findViewById(R.id.thread_threadinfo_date);
        this.mThreadLockedImageView = (ImageView) findViewById(R.id.thread_threadinfo_locked);
        this.mThreadNewImageView = (ImageView) findViewById(R.id.thread_threadinfo_newcomment);
        this.mThreadAttachedImageView = (ImageView) findViewById(R.id.thread_threadinfo_attached);
        this.mThreadPinnedImageView = (ImageView) findViewById(R.id.thread_threadinfo_pin);
        this.mThreadDigestImageView = (ImageView) findViewById(R.id.thread_threadinfo_digest);
        this.mThreadRecommendedImageView = (ImageView) findViewById(R.id.thread_threadinfo_recommend);
        this.mThreadHotImageView = (ImageView) findViewById(R.id.thread_threadinfo_hot);
        this.mThreadAgreedImageView = (ImageView) findViewById(R.id.thread_threadinfo_agree);
        this.mThreadTitleTextView = (TextView) findViewById(R.id.thread_threadinfo_title);
        this.mThreadTypeTextView = (TextView) findViewById(R.id.thread_threadinfo_type);
        this.mThreadTypeTextView.setSelected(true);
        this.mThreadReadCountTextView = (TextView) findViewById(R.id.thread_threadinfo_read);
        this.mThreadCommentCountTextView = (TextView) findViewById(R.id.thread_threadinfo_comment);
        ImageView imageView = this.mThreadLockedImageView;
        if (this.mThreadIsLocked) {
            i = 0;
        } else {
            i = 8;
        }
        imageView.setVisibility(i);
        imageView = this.mThreadAttachedImageView;
        if (this.mThreadIsAttached) {
            i = 0;
        } else {
            i = 8;
        }
        imageView.setVisibility(i);
        imageView = this.mThreadPinnedImageView;
        if (this.mThreadIsPinned) {
            i = 0;
        } else {
            i = 8;
        }
        imageView.setVisibility(i);
        imageView = this.mThreadDigestImageView;
        if (this.mThreadIsDigest) {
            i = 0;
        } else {
            i = 8;
        }
        imageView.setVisibility(i);
        imageView = this.mThreadRecommendedImageView;
        if (this.mThreadIsRecommended) {
            i = 0;
        } else {
            i = 8;
        }
        imageView.setVisibility(i);
        imageView = this.mThreadHotImageView;
        if (this.mThreadIsHot) {
            i = 0;
        } else {
            i = 8;
        }
        imageView.setVisibility(i);
        imageView = this.mThreadAgreedImageView;
        if (this.mThreadIsAgreed) {
            i = 0;
        } else {
            i = 8;
        }
        imageView.setVisibility(i);
        this.mThreadAuthorUserNameTextView.setText(this.mThreadAuthorName);
        this.mThreadDateTextView.setText(this.mThreadDate);
        this.mThreadTitleTextView.setText(this.mThreadTitle);
        this.mThreadTypeTextView.setText(this.mThreadType);
        this.mThreadReadCountTextView.setText(this.mThreadReadCount > 0 ? String.valueOf(this.mThreadReadCount) : "-");
        this.mThreadCommentCountTextView.setText(this.mThreadCommentCount > 0 ? String.valueOf(this.mThreadCommentCount) : "-");
        if (savedInstanceState == null || !bundle.getBoolean("is_thread_author_user_avatar_ready")) {
            Log.v(this.LOG_TAG, "Author #" + this.mThreadAuthorID + " avatar not ready, starting Picasso...");
            Picasso.with(this).load("http://www.1point3acres.com/bbs/uc_server/avatar.php?uid=" + this.mThreadAuthorID + "&size=medium").placeholder((int) R.drawable.ic_threadinfo_account_circle).error((int) R.drawable.ic_threadinfo_account_circle).into(this.mThreadAuthorUserAvatarImageView);
        } else {
            this.mThreadAuthorUserAvatarImageView.setImageBitmap((Bitmap) getIntent().getParcelableExtra("thread_author_user_avatar"));
        }
        this.mFragmentManager = getSupportFragmentManager();
        this.mSectionsPagerAdapter = new SectionsPagerAdapter(this.mFragmentManager);
        this.mViewPager = (ViewPager) findViewById(R.id.thread_viewpager);
        this.mViewPager.setAdapter(this.mSectionsPagerAdapter);
        this.mViewPager.addOnPageChangeListener(this);
        this.mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        this.mTabLayout.setupWithViewPager(this.mViewPager);
        this.mVpSwipeRefreshLayout = (VpSwipeRefreshLayout) findViewById(R.id.thread_swipe_refresh_layout);
        this.mVpSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        this.mVpSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh() {
                ThreadActivity.this.reloadThread(ThreadActivity.this.getString(R.string.toast_refresh_thread_progress));
            }
        });
        this.mFloatingActionButton = (FloatingActionButton) findViewById(R.id.thread_fab);
        if (this.mIsGuest) {
            this.mFloatingActionButton.setVisibility(8);
            return;
        }
        this.mFloatingActionButtonClickEvent = new FloatingActionButtonClickEvent(this);
        this.mFloatingActionButton.setOnClickListener(this.mFloatingActionButtonClickEvent);
        this.mFloatingActionButton.hide();
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageSelected(int position) {
        if (position == 0) {
            if (!this.mIsGuest) {
                this.mFloatingActionButton.hide();
            }
        } else if (position == 1 && !this.mIsGuest) {
            this.mFloatingActionButton.show();
        }
    }

    public void onPageScrollStateChanged(int state) {
    }

    public int reloadThread(String notificationMessage) {
        if (this.mIsFetchingThread) {
            return 1;
        }
        this.mThreadPageCount = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mThreadParsedPageCount = 0;
        this.mThreadComments = new ArrayList(100);
        return fetchThread(notificationMessage, true, true);
    }

    public int fetchThread(String notificationMessage, boolean showProgress, boolean needHidingExistingContent) {
        if (this.mThreadParsedPageCount >= this.mThreadPageCount) {
            Snackbar.make(this.mCoordinatorLayout, getString(R.string.toast_all_thread_comment_loaded), -1).show();
            return 1;
        } else if (this.mIsFetchingThread) {
            return 1;
        } else {
            showProgress(showProgress, needHidingExistingContent);
            if (this.mThreadParsedPageCount == 0) {
                Snackbar.make(this.mCoordinatorLayout, (CharSequence) notificationMessage, -1).show();
            } else if (this.mThreadParsedPageCount > 0) {
                Snackbar.make(this.mCoordinatorLayout, getString(R.string.toast_load_more_thread_comment_progress), -1).show();
            }
            this.mFetchThreadTask = new FetchThreadTask();
            this.mFetchThreadTask.execute(new Integer[0]);
            return 0;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_thread, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == 16908332) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 0);
            return true;
        } else if (id == R.id.action_thread_refresh) {
            if (this.mIsFetchingThread) {
                Log.v(this.LOG_TAG, "Refresh action in options selected, but FetchThreadTask is already running.");
                return true;
            }
            Log.v(this.LOG_TAG, "Refresh action in options selected, start refreshing thread.");
            reloadThread(getString(R.string.toast_refresh_thread_swipe_hint));
            return true;
        } else if (id != R.id.action_share_thread) {
            return super.onOptionsItemSelected(item);
        } else {
            Intent sendIntent = new Intent();
            sendIntent.setAction("android.intent.action.SEND");
            sendIntent.putExtra("android.intent.extra.TEXT", ParseThread.generateThreadShareText(this.mSubforumTitle, this.mThreadTitle, this.mThreadAuthorName, this.mThreadID));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        }
    }

    @TargetApi(13)
    private void showProgress(final boolean showProgress, boolean needHidingExistingContent) {
        Log.v(this.LOG_TAG, "Show Background Progress Animation: " + (showProgress ? "GO!" : "STOP"));
        if (this.mVpSwipeRefreshLayout != null) {
            this.mVpSwipeRefreshLayout.post(new Runnable() {
                public void run() {
                    ThreadActivity.this.mVpSwipeRefreshLayout.setRefreshing(showProgress);
                }
            });
        }
        if (this.mThreadContentFragment != null && this.mThreadContentFragment.isDetached()) {
            this.mThreadContentFragment.showProgress(showProgress, needHidingExistingContent);
        }
        if (this.mThreadCommentFragment != null && this.mThreadCommentFragment.isDetached()) {
            this.mThreadCommentFragment.showProgress(showProgress, needHidingExistingContent);
        }
    }

    public void onClickThreadCommentContent(ThreadComment threadComment) {
        if (!this.mIsGuest) {
            this.mReplyThreadComment = threadComment;
            Builder builder = new Builder(this.mFABContext);
            builder.setTitle(getString(R.string.post_thread_reply_comment_input_title) + this.mReplyThreadComment.getCommentAuthorName());
            builder.setMessage(getString(R.string.post_thread_reply_input_message));
            builder.setView((int) R.layout.post_thread_reply_input);
            builder.setPositiveButton(getString(R.string.post_thread_reply_input_confirm_yes), null);
            builder.setNegativeButton(getString(R.string.post_thread_reply_input_confirm_no), null);
            this.mAlertDialog = builder.show();
            this.mAlertDialog.setCanceledOnTouchOutside(false);
            this.mAlertDialog.setCancelable(false);
            this.mEditText = (EditText) this.mAlertDialog.findViewById(R.id.post_thread_reply_message_input);
            this.mPositiveButton = this.mAlertDialog.getButton(-1);
            this.mPositiveButton.setOnClickListener(this.mFloatingActionButtonClickEvent);
            this.mNegativeButton = this.mAlertDialog.getButton(-2);
            this.mNegativeButton.setOnClickListener(this.mFloatingActionButtonClickEvent);
        }
    }

    public void onContentFragmentInteraction(Uri uri) {
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == 1) {
            setResult(1);
            finish();
        }
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() - this.mExitTime > 1350) {
            Snackbar.make(this.mCoordinatorLayout, getString(R.string.toast_press_back_twice_to_quit_thread), -1).show();
            this.mExitTime = System.currentTimeMillis();
            return;
        }
        if (!this.mIsGuest) {
            this.mFloatingActionButton.hide();
        }
        setResult(-1);
        ActivityCompat.finishAfterTransition(this);
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(getString(R.string.state_key_cookie), this.mCookie);
        outState.putString(getString(R.string.state_key_user_id), this.mUserID);
        outState.putString(getString(R.string.state_key_subforum_title), this.mSubforumTitle);
        outState.putString(getString(R.string.state_key_thread_id), this.mThreadID);
        outState.putString(getString(R.string.state_key_thread_author_user_name), this.mThreadAuthorName);
        outState.putString(getString(R.string.state_key_thread_author_user_id), this.mThreadAuthorID);
        outState.putString(getString(R.string.state_key_thread_date), this.mThreadDate);
        outState.putBoolean(getString(R.string.state_key_thread_is_locked), this.mThreadIsLocked);
        outState.putBoolean(getString(R.string.state_key_thread_is_attached), this.mThreadIsAttached);
        outState.putBoolean(getString(R.string.state_key_thread_is_pinned), this.mThreadIsPinned);
        outState.putBoolean(getString(R.string.state_key_thread_is_digest), this.mThreadIsDigest);
        outState.putBoolean(getString(R.string.state_key_thread_is_recommended), this.mThreadIsRecommended);
        outState.putBoolean(getString(R.string.state_key_thread_is_hot), this.mThreadIsHot);
        outState.putBoolean(getString(R.string.state_key_thread_is_agreed), this.mThreadIsAgreed);
        outState.putString(getString(R.string.state_key_thread_type), this.mThreadType);
        outState.putString(getString(R.string.state_key_thread_title), this.mThreadTitle);
        outState.putLong(getString(R.string.state_key_thread_read_count), this.mThreadReadCount);
        outState.putLong(getString(R.string.state_key_thread_comment_count), this.mThreadCommentCount);
        Log.i(this.LOG_TAG, "Saved all Thread states into outState.");
        super.onSaveInstanceState(outState);
    }

    protected void onStop() {
        super.onStop();
        if (this.mIsFetchingThread) {
            this.mIsFetchingThread = false;
            this.mFetchThreadTask.cancel(true);
            this.mFetchThreadTask = null;
        }
        if (this.mIsPostingReply) {
            this.mIsPostingReply = false;
            this.mPostReplyTask.cancel(true);
            this.mPostReplyTask = null;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Deprecated
    public void debugOK(int sectionIndex) {
        Log.d("DEBUG", "Fragment " + sectionIndex + "OK!");
    }
}
