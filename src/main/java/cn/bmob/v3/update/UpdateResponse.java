package cn.bmob.v3.update;

import android.content.Context;
import android.support.v4.media.session.PlaybackStateCompat;
import cn.bmob.v3.b.mine;
import cn.bmob.v3.b.of;
import cn.bmob.v3.exception.BmobException;
import java.io.Serializable;
import java.text.DecimalFormat;

public class UpdateResponse implements Serializable {
    private static final long serialVersionUID = 287172543706916699L;
    public BmobException exception;
    public Boolean isforce;
    public String path;
    public String path_md5;
    public long target_size;
    public String updateLog = null;
    public String version = null;
    public Integer version_i;

    public UpdateResponse(AppVersion app) {
        this.updateLog = app.getUpdate_log();
        this.version = app.getVersion();
        this.version_i = app.getVersion_i();
        if (this.version_i == null) {
            this.version_i = Integer.valueOf(0);
        }
        this.isforce = app.getIsforce();
        if (this.isforce == null) {
            this.isforce = Boolean.valueOf(false);
        }
        if (app.getPath() != null) {
            this.path = app.getPath().getFileUrl();
            this.path_md5 = mine.Code(this.path);
        } else if (app.getAndroid_url() != null) {
            this.path = app.getAndroid_url();
            this.path_md5 = mine.Code(this.path);
        }
        try {
            this.target_size = Long.parseLong(app.getTarget_size());
        } catch (Exception e) {
            this.target_size = 0;
        }
    }

    public UpdateResponse(int code, String error) {
        this.exception = new BmobException(code, error);
    }

    public BmobException getException() {
        return this.exception;
    }

    public String getUpdateInfo(Context paramContext, boolean isDownloaded) {
        String string = paramContext.getString(of.Code(paramContext).I("BMNewVersion"));
        String string2 = paramContext.getString(of.Code(paramContext).I("BMTargetSize"));
        String string3 = paramContext.getString(of.Code(paramContext).I("BMUpdateContent"));
        String string4 = paramContext.getString(of.Code(paramContext).I("BMDialog_InstallAPK"));
        if (isDownloaded) {
            return Code(4, string, string2, string3, string4, "", "");
        }
        String str;
        String str2 = "";
        long j = this.target_size;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        if (j <= 0) {
            str = "0B";
        } else if (j < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            str = decimalFormat.format((double) j) + "B";
        } else if (j < 1048576) {
            str = decimalFormat.format(((double) j) / 1024.0d) + "K";
        } else if (j < 1073741824) {
            str = decimalFormat.format(((double) j) / 1048576.0d) + "M";
        } else {
            str = decimalFormat.format(((double) j) / 1.073741824E9d) + "G";
        }
        return Code(6, string, string2, string3, string4, str2, str);
    }

    private String Code(int i, String str, String str2, String str3, String str4, String str5, String str6) {
        String str7 = "";
        Object[] objArr = null;
        if (this.updateLog.contains("；")) {
            String[] split = this.updateLog.split("；");
            int length = split.length;
            StringBuilder stringBuilder = new StringBuilder();
            objArr = new Object[(length + 6)];
            if (i == 4) {
                stringBuilder.append("%s %s\n%s\n\n%s");
                objArr[0] = str;
                objArr[1] = this.version;
                objArr[2] = str4;
                objArr[3] = str3;
            } else if (i == 6) {
                stringBuilder.append("%s %s\n%s %s%s\n\n%s");
                objArr[0] = str;
                objArr[1] = this.version;
                objArr[2] = str2;
                objArr[3] = str6;
                objArr[4] = str5;
                objArr[5] = str3;
            }
            for (int i2 = 0; i2 < length; i2++) {
                if (i2 != length - 1) {
                    stringBuilder.append("\n%s");
                    objArr[i + i2] = split[i2] + "；";
                } else {
                    stringBuilder.append("\n%s");
                    objArr[i + i2] = split[i2];
                }
            }
            str7 = stringBuilder.toString();
        } else if (i == 4) {
            str7 = "%s %s\n%s\n\n%s\n%s\n";
            objArr = new Object[]{str, this.version, str4, str3, this.updateLog};
        } else if (i == 6) {
            str7 = "%s %s\n%s %s%s\n\n%s\n%s\n";
            objArr = new Object[]{str, this.version, str2, str6, str5, str3, this.updateLog};
        }
        return String.format(str7, objArr);
    }
}
