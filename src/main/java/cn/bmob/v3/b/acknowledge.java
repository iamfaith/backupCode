package cn.bmob.v3.b;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import cn.bmob.v3.helper.NotificationCompat.Builder;
import cn.bmob.v3.update.a.This;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/* compiled from: DownloadApk */
public final class acknowledge {
    String B;
    String C;
    NotificationManager Code;
    @SuppressLint({"HandlerLeak"})
    private Handler F = new Handler(this) {
        private /* synthetic */ acknowledge Code;

        {
            this.Code = r1;
        }

        public final void handleMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(this.Code.S, msg.getData().getString("error"), 0).show();
                        if (this.Code.Code != null) {
                            this.Code.Code.cancel(0);
                            return;
                        }
                        return;
                    case 1:
                        acknowledge.Code(this.Code, msg.arg1);
                        return;
                    case 2:
                        Intent intent;
                        if (This.I()) {
                            this.Code.Code = (NotificationManager) this.Code.S.getSystemService("notification");
                            intent = new Intent("android.intent.action.VIEW");
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), mine.Code(this.Code.B) + ".apk")), "application/vnd.android.package-archive");
                            this.Code.Z = PendingIntent.getActivity(this.Code.S, 0, intent, 0);
                            String string = this.Code.S.getString(of.Code(this.Code.S).I("bmob_common_silent_download_finish"));
                            String str = this.Code.C;
                            String string2 = this.Code.S.getString(of.Code(this.Code.S).I("bmob_common_silent_download_finish"));
                            this.Code.I = this.Code.Code(16, 17301634, string, str, string2, this.Code.Z);
                            this.Code.V = this.Code.I.build();
                            this.Code.Code.notify(0, this.Code.V);
                            return;
                        }
                        this.Code.Code.cancel(0);
                        intent = new Intent("android.intent.action.VIEW");
                        intent.addFlags(268435456);
                        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), mine.Code(this.Code.B) + ".apk")), "application/vnd.android.package-archive");
                        this.Code.S.startActivity(intent);
                        return;
                    default:
                        return;
                }
            }
        }
    };
    Builder I;
    private Context S;
    Notification V;
    PendingIntent Z;

    public acknowledge(Context context, String str) {
        this.S = context.getApplicationContext();
        this.B = str;
        this.C = this.S.getApplicationInfo().loadLabel(this.S.getPackageManager()).toString();
    }

    public final void Code() {
        if (!This.I() && !TextUtils.isEmpty(this.B)) {
            this.Code = (NotificationManager) this.S.getSystemService("notification");
            Intent intent = new Intent();
            intent.addFlags(536870912);
            this.Z = PendingIntent.getActivity(this.S, 0, intent, 0);
            this.I = Code(2, 17301633, this.S.getString(of.Code(this.S).I("bmob_common_start_download_notification")), this.S.getString(of.Code(this.S).I("bmob_common_download_notification_prefix")) + this.C, "0%", this.Z);
            this.V = this.I.build();
            this.Code.notify(0, this.V);
        } else if (TextUtils.isEmpty(this.B)) {
            Toast.makeText(this.S, of.Code(this.S).I("bmob_common_download_failed"), 0).show();
            return;
        }
        final String str = this.B;
        final String charSequence = this.S.getApplicationInfo().loadLabel(this.S.getPackageManager()).toString();
        new Thread(this) {
            private /* synthetic */ acknowledge I;

            public final void run() {
                this.I.Code(str);
            }
        }.start();
    }

    private Builder Code(int i, int i2, String str, String str2, String str3, PendingIntent pendingIntent) {
        Builder contentIntent = new Builder(this.S).setTicker(str).setSmallIcon(i2).setContentTitle(str2).setContentText(str3).setContentIntent(pendingIntent);
        if (i == 2) {
            contentIntent.setOngoing(true);
        } else if (i == 16) {
            contentIntent.setAutoCancel(true);
        }
        return contentIntent;
    }

    public final void Code(String str) {
        try {
            HttpEntity entity = new DefaultHttpClient().execute(new HttpGet(str)).getEntity();
            float contentLength = (float) entity.getContentLength();
            InputStream content = entity.getContent();
            FileOutputStream fileOutputStream = null;
            if (content != null) {
                fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), mine.Code(this.B) + ".apk"));
                byte[] bArr = new byte[1024];
                float f = 0.0f;
                int i = 0;
                while (true) {
                    int read = content.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, read);
                    f += (float) read;
                    if (i == 0 || ((int) ((f * 100.0f) / contentLength)) - 10 > i) {
                        i += 10;
                        if (!This.I()) {
                            Code(1, (int) ((f * 100.0f) / contentLength));
                        }
                    }
                }
            }
            Code(2, 0);
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message message = new Message();
            message.what = -1;
            message.arg1 = 0;
            Bundle bundle = new Bundle();
            bundle.putString("error", e.toString());
            message.setData(bundle);
            this.F.sendMessage(message);
        }
    }

    private void Code(int i, int i2) {
        Message message = new Message();
        message.what = i;
        message.arg1 = i2;
        this.F.sendMessage(message);
    }

    static /* synthetic */ void Code(acknowledge cn_bmob_v3_b_acknowledge, int i) {
        CharSequence charSequence = cn_bmob_v3_b_acknowledge.S.getString(of.Code(cn_bmob_v3_b_acknowledge.S).I("bmob_common_download_notification_prefix")) + cn_bmob_v3_b_acknowledge.C;
        cn_bmob_v3_b_acknowledge.I.setContentTitle(charSequence).setContentText(i + "%").setContentIntent(cn_bmob_v3_b_acknowledge.Z);
        cn_bmob_v3_b_acknowledge.V = cn_bmob_v3_b_acknowledge.I.build();
        cn_bmob_v3_b_acknowledge.Code.notify(0, cn_bmob_v3_b_acknowledge.V);
    }
}
