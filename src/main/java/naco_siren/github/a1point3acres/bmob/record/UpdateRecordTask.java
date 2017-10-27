package naco_siren.github.a1point3acres.bmob.record;

import android.os.AsyncTask;
import android.util.Log;

public class UpdateRecordTask extends AsyncTask<Void, Void, Boolean> {
    private static final String LOG_TAG = UpdateRecordTask.class.getSimpleName();
    private String mTargetValueString;
    private UpdateType mUpdateType;
    private String mUserID;

    public UpdateRecordTask(String userId, UpdateType updateType, String targetValueString) {
        this.mUserID = userId;
        this.mUpdateType = updateType;
        this.mTargetValueString = targetValueString;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        Log.v(LOG_TAG, "Updating " + this.mUpdateType.toString() + (this.mTargetValueString != null ? ": " + this.mTargetValueString : ""));
    }

    protected Boolean doInBackground(Void... params) {
        return Boolean.valueOf(new AddRecord(this.mUserID, this.mUpdateType, this.mTargetValueString).execute());
    }
}
