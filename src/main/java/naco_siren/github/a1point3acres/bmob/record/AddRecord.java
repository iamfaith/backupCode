package naco_siren.github.a1point3acres.bmob.record;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class AddRecord extends SaveListener<String> {
    private static final String LOG_TAG = AddRecord.class.getSimpleName();
    private static final int UPDATE_RETRY_TIMES = 1;
    private boolean mSuccess;
    private String mTargetValueString;
    private int mTrial;
    private UpdateType mUpdateType;
    private String mUserID;

    public AddRecord(String userID, UpdateType updateType, String targetValueString) {
        this.mUserID = userID;
        this.mTargetValueString = targetValueString;
        this.mUpdateType = updateType;
    }

    public boolean execute() {
        this.mSuccess = false;
        switch (this.mUpdateType) {
            case MAIN:
                this.mTrial = 0;
                while (!this.mSuccess && this.mTrial < 1) {
                    MainRecord record = new MainRecord();
                    record.setUserId(this.mUserID);
                    record.save(this);
                    this.mTrial++;
                }
                return this.mSuccess;
            case THREAD:
                this.mTrial = 0;
                while (!this.mSuccess && this.mTrial < 1) {
                    ThreadRecord record2 = new ThreadRecord();
                    record2.setUserId(this.mUserID);
                    record2.setThreadId(this.mTargetValueString);
                    record2.save(this);
                    this.mTrial++;
                }
                return this.mSuccess;
            case BLOG:
                this.mTrial = 0;
                while (!this.mSuccess && this.mTrial < 1) {
                    BlogRecord record3 = new BlogRecord();
                    record3.setUserId(this.mUserID);
                    record3.setBlogTitle(this.mTargetValueString);
                    record3.save(this);
                    this.mTrial++;
                }
                return this.mSuccess;
            case SETTINGS:
                this.mTrial = 0;
                while (!this.mSuccess && this.mTrial < 1) {
                    SettingsRecord record4 = new SettingsRecord();
                    record4.setUserId(this.mUserID);
                    record4.save(this);
                    this.mTrial++;
                }
                return this.mSuccess;
            default:
                return false;
        }
    }

    public void done(String objectId, BmobException e) {
        if (e == null) {
            this.mSuccess = true;
        } else {
            this.mSuccess = false;
        }
    }
}
