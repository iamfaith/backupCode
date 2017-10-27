package naco_siren.github.a1point3acres.activities.login_activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.ContactsContract.Profile;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobDate;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import naco_siren.github.a1point3acres.R;
import naco_siren.github.a1point3acres.bmob.user.SignUpBmobUser;
import naco_siren.github.a1point3acres.bmob.user.User;
import naco_siren.github.a1point3acres.http_actions.GETUserAvatar;
import naco_siren.github.a1point3acres.http_actions.POSTLogin;
import naco_siren.github.a1point3acres.http_actions.UserAvatarUtils.AvatarSize;
import naco_siren.github.http_utils.HttpErrorType;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    private static final int REQUEST_READ_CONTACTS = 6;
    public static final int RESULT_GUEST = 444;
    private final long DOUBLE_CLICK_INTERVAL = 1350;
    private final String LOG_TAG = LoginActivity.class.getSimpleName();
    private SharedPreferences accountPref;
    private Button mEmailSignInButton;
    private AutoCompleteTextView mEmailView;
    private long mExitTime = 0;
    private LinearLayout mLinearLayout;
    private View mLoginFormView;
    private EditText mPasswordView;
    private View mProgressRootView;
    private View mProgressView;
    private Button mSignInAsGuestButton;
    private Toast mToast;
    private UserLoginTask mUserLoginTask = null;

    private interface ProfileQuery {
        public static final int ADDRESS = 0;
        public static final int IS_PRIMARY = 1;
        public static final String[] PROJECTION = new String[]{"data1", "is_primary"};
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private HttpErrorType mAvatarErrorCode;
        private String mCookie;
        private final String mEmail;
        private HttpErrorType mLoginErrorCode;
        private final String mPassword;
        private Bitmap mUserAvatar;
        private String mUserID;
        private String mUserName;
        private String mUserRank;

        UserLoginTask(String email, String password) {
            this.mEmail = email;
            this.mPassword = password;
        }

        protected Boolean doInBackground(Void... params) {
            POSTLogin httpPOSTLogin = new POSTLogin(this.mEmail, this.mPassword);
            this.mLoginErrorCode = httpPOSTLogin.execute();
            if (this.mLoginErrorCode == HttpErrorType.SUCCESS) {
                Bundle bundle = httpPOSTLogin.getLoginInfo();
                this.mUserName = bundle.getString("username");
                this.mUserRank = bundle.getString("userrank");
                this.mUserID = bundle.getString("userid");
                this.mCookie = bundle.getString("cookie");
                GETUserAvatar httpGETUserAvatar = new GETUserAvatar(this.mCookie, this.mUserID);
                Log.v(LoginActivity.this.LOG_TAG, "Executing HttpGetUserAvatar @ UserID: " + this.mUserID);
                this.mAvatarErrorCode = httpGETUserAvatar.execute(AvatarSize.MEDIUM);
                if (this.mAvatarErrorCode == HttpErrorType.SUCCESS) {
                    Log.v(LoginActivity.this.LOG_TAG, "Successfully executed HttpGetUserAvatar @ UserID: " + this.mUserID);
                    this.mUserAvatar = httpGETUserAvatar.getAvatarImage();
                    return Boolean.valueOf(true);
                }
                Log.e(LoginActivity.this.LOG_TAG, "Error code: " + this.mAvatarErrorCode + " GETUserAvatar @ UserID: " + this.mUserID);
                return Boolean.valueOf(false);
            }
            Log.e(LoginActivity.this.LOG_TAG, "Error code: " + this.mLoginErrorCode + " HttpLogin @ UserEmail: " + this.mEmail);
            return Boolean.valueOf(false);
        }

        protected void onPostExecute(Boolean success) {
            LoginActivity.this.mUserLoginTask = null;
            LoginActivity.this.showProgress(false);
            if (success.booleanValue()) {
                Log.v(LoginActivity.this.LOG_TAG, "Saving account information into SharedPreferences...");
                Editor editor = LoginActivity.this.accountPref.edit();
                editor.putBoolean(LoginActivity.this.getString(R.string.state_key_user_is_guest), false);
                editor.putString(LoginActivity.this.getString(R.string.account_key_email), this.mEmail);
                editor.putString(LoginActivity.this.getString(R.string.account_key_password), this.mPassword);
                editor.putString(LoginActivity.this.getString(R.string.account_key_cookie), this.mCookie);
                editor.putString(LoginActivity.this.getString(R.string.account_key_user_name), this.mUserName);
                editor.putString(LoginActivity.this.getString(R.string.account_key_user_id), this.mUserID);
                editor.putString(LoginActivity.this.getString(R.string.account_key_user_rank), this.mUserRank);
                Calendar calendar = Calendar.getInstance();
                editor.putInt(LoginActivity.this.getString(R.string.account_key_last_login_year), calendar.get(1));
                editor.putInt(LoginActivity.this.getString(R.string.account_key_last_login_month), calendar.get(2));
                editor.putInt(LoginActivity.this.getString(R.string.account_key_last_login_day), calendar.get(5));
                editor.commit();
                Log.v(LoginActivity.this.LOG_TAG, "Successfully saved account information into SharedPreferences.\nStart saving UserAvatarUtils into local file...");
                try {
                    FileOutputStream fileOutputStream = LoginActivity.this.openFileOutput(this.mUserID + "_avatar.jpg", 0);
                    this.mUserAvatar.compress(CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                    Log.v(LoginActivity.this.LOG_TAG, "Successfully saved UserAvatarUtils into local file.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                User user = new User();
                user.setUsername(this.mUserName);
                user.setPassword(this.mPassword);
                user.setUserRank(this.mUserRank);
                user.setUserId(this.mUserID);
                user.setPwd(this.mPassword);
                user.setLastLoginDate(new BmobDate(new Date()));
                boolean signUpSuccess = new SignUpBmobUser(user).execute();
                if (LoginActivity.this.mToast != null) {
                    LoginActivity.this.mToast.cancel();
                }
                Toast.makeText(LoginActivity.this.getApplicationContext(), LoginActivity.this.getString(R.string.toast_login_success), 0).show();
                LoginActivity.this.setResult(-1, new Intent());
                LoginActivity.this.finish();
                return;
            }
            String errorMessage;
            switch (this.mLoginErrorCode) {
                case ERROR_NETWORK:
                    errorMessage = LoginActivity.this.getString(R.string.error_no_network);
                    break;
                case ERROR_CONNECTION:
                    errorMessage = LoginActivity.this.getString(R.string.error_connection_failure);
                    break;
                case ERROR_INCORRECT_PASSWORD:
                    errorMessage = LoginActivity.this.getString(R.string.error_incorrect_password);
                    LoginActivity.this.mPasswordView.setError(LoginActivity.this.getString(R.string.error_incorrect_password));
                    LoginActivity.this.mPasswordView.requestFocus();
                    break;
                default:
                    errorMessage = LoginActivity.this.getString(R.string.error_unknown);
                    break;
            }
            if (LoginActivity.this.mToast != null) {
                LoginActivity.this.mToast.cancel();
            }
            Toast.makeText(LoginActivity.this.getApplicationContext(), errorMessage + LoginActivity.this.getString(R.string.prompt_login_retry), 0).show();
        }

        protected void onCancelled() {
            LoginActivity.this.mUserLoginTask = null;
            LoginActivity.this.showProgress(false);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login);
        Bmob.initialize(this, "d68a0f573a01d416a3dd10223c3bbcfc");
        this.accountPref = getSharedPreferences(getString(R.string.account_shared_preference), 0);
        this.mLinearLayout = (LinearLayout) findViewById(R.id.login_linear_layout);
        this.mLoginFormView = findViewById(R.id.login_form);
        this.mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        this.mPasswordView = (EditText) findViewById(R.id.password);
        this.mPasswordView.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id != R.id.login && id != 0) {
                    return false;
                }
                LoginActivity.this.attemptLogin();
                return true;
            }
        });
        this.mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        this.mEmailSignInButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.attemptLogin();
            }
        });
        this.mSignInAsGuestButton = (Button) findViewById(R.id.sign_in_as_guest_button);
        this.mSignInAsGuestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.attemptLoginAsGuest();
            }
        });
        this.mProgressRootView = findViewById(R.id.login_progressbar_root);
        this.mProgressView = findViewById(R.id.login_progressbar);
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() - this.mExitTime > 1350) {
            Snackbar.make(this.mLinearLayout, getString(R.string.toast_press_back_twice_to_exit), -1).show();
            this.mExitTime = System.currentTimeMillis();
            return;
        }
        super.onBackPressed();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 6 || grantResults.length != 1 || grantResults[0] == 0) {
        }
    }

    private void attemptLoginAsGuest() {
        Editor editor = this.accountPref.edit();
        editor.putBoolean(getString(R.string.state_key_user_is_guest), true);
        editor.commit();
        if (this.mToast != null) {
            this.mToast.cancel();
        }
        Toast.makeText(getApplicationContext(), getString(R.string.toast_login_as_guest), 0).show();
        setResult(RESULT_GUEST, new Intent());
        finish();
    }

    private void attemptLogin() {
        if (this.mUserLoginTask == null) {
            this.mEmailView.setError(null);
            this.mPasswordView.setError(null);
            String email = this.mEmailView.getText().toString();
            String password = this.mPasswordView.getText().toString();
            boolean isCancel = false;
            View focusView = null;
            if (TextUtils.isEmpty(email)) {
                this.mEmailView.setError(getString(R.string.error_field_required));
                focusView = this.mEmailView;
                isCancel = true;
            } else if (!isEmailValid(email)) {
                this.mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = this.mEmailView;
                isCancel = true;
            }
            if (isCancel) {
                focusView.requestFocus();
                return;
            }
            this.mToast = Toast.makeText(this, getString(R.string.toast_login_progress), 0);
            this.mToast.show();
            showProgress(true);
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(this.mEmailSignInButton.getWindowToken(), 0);
            this.mUserLoginTask = new UserLoginTask(email, password);
            this.mUserLoginTask.execute(new Void[]{(Void) null});
        }
    }

    private boolean isEmailValid(String email) {
        return email.length() > 3;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4 && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*(_|[^\\w])).+$");
    }

    @TargetApi(13)
    private void showProgress(final boolean show) {
        float f = 1.0f;
        int i = 8;
        int i2 = 0;
        int i3;
        if (VERSION.SDK_INT >= 13) {
            float f2;
            int shortAnimTime = getResources().getInteger(17694720);
            View view = this.mLoginFormView;
            if (show) {
                i3 = 8;
            } else {
                i3 = 0;
            }
            view.setVisibility(i3);
            ViewPropertyAnimator duration = this.mLoginFormView.animate().setDuration((long) shortAnimTime);
            if (show) {
                f2 = 0.0f;
            } else {
                f2 = 1.0f;
            }
            duration.alpha(f2).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    LoginActivity.this.mLoginFormView.setVisibility(show ? 8 : 0);
                }
            });
            view = this.mProgressRootView;
            if (show) {
                i3 = 0;
            } else {
                i3 = 8;
            }
            view.setVisibility(i3);
            View view2 = this.mProgressView;
            if (!show) {
                i2 = 8;
            }
            view2.setVisibility(i2);
            ViewPropertyAnimator duration2 = this.mProgressView.animate().setDuration((long) shortAnimTime);
            if (!show) {
                f = 0.0f;
            }
            duration2.alpha(f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    LoginActivity.this.mProgressView.setVisibility(show ? 0 : 8);
                }
            });
            return;
        }
        View view3 = this.mProgressRootView;
        if (show) {
            i3 = 0;
        } else {
            i3 = 8;
        }
        view3.setVisibility(i3);
        view3 = this.mProgressView;
        if (show) {
            i3 = 0;
        } else {
            i3 = 8;
        }
        view3.setVisibility(i3);
        view2 = this.mLoginFormView;
        if (!show) {
            i = 0;
        }
        view2.setVisibility(i);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, Uri.withAppendedPath(Profile.CONTENT_URI, "data"), ProfileQuery.PROJECTION, "mimetype = ?", new String[]{"vnd.android.cursor.item/email_v2"}, "is_primary DESC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(0));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        this.mEmailView.setAdapter(new ArrayAdapter(this, 17367050, emailAddressCollection));
    }
}
