package naco_siren.github.a1point3acres.http_actions;

import android.graphics.Bitmap;
import android.util.Log;
import naco_siren.github.a1point3acres.http_actions.UserAvatarUtils.AvatarSize;
import naco_siren.github.http_utils.HttpErrorType;
import naco_siren.github.http_utils.HttpGETImage;

public class GETUserAvatar {
    private final String LOG_TAG = GETUserAvatar.class.getSimpleName();
    private String mCookie;
    private HttpErrorType mErrorCode;
    private HttpGETImage mHttpGETImage;
    private boolean mIsExecuted;
    private String mUserID;

    public GETUserAvatar(String cookie, String userID) {
        this.mCookie = cookie;
        this.mUserID = userID;
        this.mIsExecuted = false;
    }

    public HttpErrorType execute(AvatarSize userAvatarSize) {
        this.mHttpGETImage = new HttpGETImage(UserAvatarUtils.getAvatarHrefByID(this.mUserID, userAvatarSize), this.mCookie);
        this.mErrorCode = this.mHttpGETImage.Execute();
        this.mIsExecuted = true;
        return this.mErrorCode;
    }

    public Bitmap getAvatarImage() {
        if (this.mIsExecuted) {
            return this.mHttpGETImage.getImage();
        }
        Log.e(this.LOG_TAG, "mHttpGETImage not executed!");
        return null;
    }
}
