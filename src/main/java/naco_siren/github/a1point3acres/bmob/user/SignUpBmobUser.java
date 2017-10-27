package naco_siren.github.a1point3acres.bmob.user;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import org.apache.http.HttpStatus;

public class SignUpBmobUser extends SaveListener<User> {
    private static final String LOG_TAG = SignUpBmobUser.class.getSimpleName();
    private static final int SIGN_UP_RETRY_TIMES = 6;
    private boolean mSuccess;
    private int mTrial;
    private User mUser;

    public SignUpBmobUser(User user) {
        this.mUser = user;
    }

    public boolean execute() {
        this.mSuccess = false;
        this.mTrial = 0;
        while (!this.mSuccess && this.mTrial < 6) {
            this.mUser.signUp(this);
            this.mTrial++;
        }
        return this.mSuccess;
    }

    public void done(User user, BmobException e) {
        if (e == null || e.getErrorCode() == HttpStatus.SC_ACCEPTED || e.getErrorCode() == HttpStatus.SC_UNAUTHORIZED) {
            this.mSuccess = true;
        } else {
            this.mSuccess = false;
        }
    }
}
