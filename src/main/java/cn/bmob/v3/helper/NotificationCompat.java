package cn.bmob.v3.helper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Iterator;

public class NotificationCompat {
    private static final thing Code;
    public static final int FLAG_HIGH_PRIORITY = 128;
    public static final int PRIORITY_DEFAULT = 0;
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_LOW = -1;
    public static final int PRIORITY_MAX = 2;
    public static final int PRIORITY_MIN = -2;

    public static class Action {
        public PendingIntent actionIntent;
        public int icon;
        public CharSequence title;

        public Action(int icon_, CharSequence title_, PendingIntent intent_) {
            this.icon = icon_;
            this.title = title_;
            this.actionIntent = intent_;
        }
    }

    public static class Builder {
        PendingIntent B;
        RemoteViews C;
        Context Code;
        int D;
        CharSequence F;
        CharSequence I;
        int L;
        Bitmap S;
        CharSequence V;
        PendingIntent Z;
        boolean a;
        Style b;
        CharSequence c;
        int d;
        int e;
        boolean f;
        ArrayList<Action> g = new ArrayList();
        Notification h = new Notification();

        public Builder(Context context) {
            this.Code = context;
            this.h.when = System.currentTimeMillis();
            this.h.audioStreamType = -1;
            this.L = 0;
        }

        public Builder setWhen(long when) {
            this.h.when = when;
            return this;
        }

        public Builder setUsesChronometer(boolean b) {
            this.a = b;
            return this;
        }

        public Builder setSmallIcon(int icon) {
            this.h.icon = icon;
            return this;
        }

        public Builder setSmallIcon(int icon, int level) {
            this.h.icon = icon;
            this.h.iconLevel = level;
            return this;
        }

        public Builder setContentTitle(CharSequence title) {
            this.V = title;
            return this;
        }

        public Builder setContentText(CharSequence text) {
            this.I = text;
            return this;
        }

        public Builder setSubText(CharSequence text) {
            this.c = text;
            return this;
        }

        public Builder setNumber(int number) {
            this.D = number;
            return this;
        }

        public Builder setContentInfo(CharSequence info) {
            this.F = info;
            return this;
        }

        public Builder setProgress(int max, int progress, boolean indeterminate) {
            this.d = max;
            this.e = progress;
            this.f = indeterminate;
            return this;
        }

        public Builder setContent(RemoteViews views) {
            this.h.contentView = views;
            return this;
        }

        public Builder setContentIntent(PendingIntent intent) {
            this.Z = intent;
            return this;
        }

        public Builder setDeleteIntent(PendingIntent intent) {
            this.h.deleteIntent = intent;
            return this;
        }

        public Builder setFullScreenIntent(PendingIntent intent, boolean highPriority) {
            this.B = intent;
            Code(128, highPriority);
            return this;
        }

        public Builder setTicker(CharSequence tickerText) {
            this.h.tickerText = tickerText;
            return this;
        }

        public Builder setTicker(CharSequence tickerText, RemoteViews views) {
            this.h.tickerText = tickerText;
            this.C = views;
            return this;
        }

        public Builder setLargeIcon(Bitmap icon) {
            this.S = icon;
            return this;
        }

        public Builder setSound(Uri sound) {
            this.h.sound = sound;
            this.h.audioStreamType = -1;
            return this;
        }

        public Builder setSound(Uri sound, int streamType) {
            this.h.sound = sound;
            this.h.audioStreamType = streamType;
            return this;
        }

        public Builder setVibrate(long[] pattern) {
            this.h.vibrate = pattern;
            return this;
        }

        public Builder setLights(int argb, int onMs, int offMs) {
            int i;
            int i2 = 1;
            this.h.ledARGB = argb;
            this.h.ledOnMS = onMs;
            this.h.ledOffMS = offMs;
            if (this.h.ledOnMS == 0 || this.h.ledOffMS == 0) {
                i = 0;
            } else {
                i = 1;
            }
            Notification notification = this.h;
            int i3 = this.h.flags & -2;
            if (i == 0) {
                i2 = 0;
            }
            notification.flags = i3 | i2;
            return this;
        }

        public Builder setOngoing(boolean ongoing) {
            Code(2, ongoing);
            return this;
        }

        public Builder setOnlyAlertOnce(boolean onlyAlertOnce) {
            Code(8, onlyAlertOnce);
            return this;
        }

        public Builder setAutoCancel(boolean autoCancel) {
            Code(16, autoCancel);
            return this;
        }

        public Builder setDefaults(int defaults) {
            this.h.defaults = defaults;
            if ((defaults & 4) != 0) {
                Notification notification = this.h;
                notification.flags |= 1;
            }
            return this;
        }

        private void Code(int i, boolean z) {
            if (z) {
                Notification notification = this.h;
                notification.flags |= i;
                return;
            }
            notification = this.h;
            notification.flags &= i ^ -1;
        }

        public Builder setPriority(int pri) {
            this.L = pri;
            return this;
        }

        public Builder addAction(int icon, CharSequence title, PendingIntent intent) {
            this.g.add(new Action(icon, title, intent));
            return this;
        }

        public Builder setStyle(Style style) {
            if (this.b != style) {
                this.b = style;
                if (this.b != null) {
                    this.b.setBuilder(this);
                }
            }
            return this;
        }

        @Deprecated
        public Notification getNotification() {
            return NotificationCompat.Code.Code(this);
        }

        public Notification build() {
            return NotificationCompat.Code.Code(this);
        }
    }

    public static abstract class Style {
        CharSequence B;
        boolean C = false;
        private Builder Code;
        CharSequence Z;

        public void setBuilder(Builder builder) {
            if (this.Code != builder) {
                this.Code = builder;
                if (this.Code != null) {
                    this.Code.setStyle(this);
                }
            }
        }

        public Notification build() {
            if (this.Code != null) {
                return this.Code.build();
            }
            return null;
        }
    }

    static class mine {
        private android.app.Notification.Builder Code;

        @SuppressLint({"NewApi"})
        public mine(Context context, Notification notification, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, RemoteViews remoteViews, int i, PendingIntent pendingIntent, PendingIntent pendingIntent2, Bitmap bitmap, int i2, int i3, boolean z, boolean z2, int i4, CharSequence charSequence4) {
            boolean z3;
            android.app.Notification.Builder lights = new android.app.Notification.Builder(context).setWhen(notification.when).setSmallIcon(notification.icon, notification.iconLevel).setContent(notification.contentView).setTicker(notification.tickerText, remoteViews).setSound(notification.sound, notification.audioStreamType).setVibrate(notification.vibrate).setLights(notification.ledARGB, notification.ledOnMS, notification.ledOffMS);
            if ((notification.flags & 2) != 0) {
                z3 = true;
            } else {
                z3 = false;
            }
            lights = lights.setOngoing(z3);
            if ((notification.flags & 8) != 0) {
                z3 = true;
            } else {
                z3 = false;
            }
            lights = lights.setOnlyAlertOnce(z3);
            if ((notification.flags & 16) != 0) {
                z3 = true;
            } else {
                z3 = false;
            }
            lights = lights.setAutoCancel(z3).setDefaults(notification.defaults).setContentTitle(charSequence).setContentText(charSequence2).setSubText(charSequence4).setContentInfo(charSequence3).setContentIntent(pendingIntent).setDeleteIntent(notification.deleteIntent);
            if ((notification.flags & 128) != 0) {
                z3 = true;
            } else {
                z3 = false;
            }
            this.Code = lights.setFullScreenIntent(pendingIntent2, z3).setLargeIcon(bitmap).setNumber(i).setUsesChronometer(z2).setPriority(i4).setProgress(i2, i3, z);
        }

        @SuppressLint({"NewApi"})
        public final void Code(int i, CharSequence charSequence, PendingIntent pendingIntent) {
            this.Code.addAction(i, charSequence, pendingIntent);
        }

        @SuppressLint({"NewApi"})
        public final void Code(CharSequence charSequence, boolean z, CharSequence charSequence2, CharSequence charSequence3) {
            android.app.Notification.BigTextStyle bigText = new android.app.Notification.BigTextStyle(this.Code).setBigContentTitle(charSequence).bigText(charSequence3);
            if (z) {
                bigText.setSummaryText(charSequence2);
            }
        }

        @SuppressLint({"NewApi"})
        public final void Code(CharSequence charSequence, boolean z, CharSequence charSequence2, Bitmap bitmap, Bitmap bitmap2, boolean z2) {
            android.app.Notification.BigPictureStyle bigPicture = new android.app.Notification.BigPictureStyle(this.Code).setBigContentTitle(charSequence).bigPicture(bitmap);
            if (z2) {
                bigPicture.bigLargeIcon(bitmap2);
            }
            if (z) {
                bigPicture.setSummaryText(charSequence2);
            }
        }

        @SuppressLint({"NewApi"})
        public final void Code(CharSequence charSequence, boolean z, CharSequence charSequence2, ArrayList<CharSequence> arrayList) {
            android.app.Notification.InboxStyle bigContentTitle = new android.app.Notification.InboxStyle(this.Code).setBigContentTitle(charSequence);
            if (z) {
                bigContentTitle.setSummaryText(charSequence2);
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                bigContentTitle.addLine((CharSequence) it.next());
            }
        }

        public final Notification Code() {
            return this.Code.build();
        }
    }

    interface thing {
        Notification Code(Builder builder);
    }

    public static class BigPictureStyle extends Style {
        Bitmap Code;
        boolean I;
        Bitmap V;

        public BigPictureStyle(Builder builder) {
            setBuilder(builder);
        }

        public BigPictureStyle setBigContentTitle(CharSequence title) {
            this.Z = title;
            return this;
        }

        public BigPictureStyle setSummaryText(CharSequence cs) {
            this.B = cs;
            this.C = true;
            return this;
        }

        public BigPictureStyle bigPicture(Bitmap b) {
            this.Code = b;
            return this;
        }

        public BigPictureStyle bigLargeIcon(Bitmap b) {
            this.V = b;
            this.I = true;
            return this;
        }
    }

    public static class BigTextStyle extends Style {
        CharSequence Code;

        public BigTextStyle(Builder builder) {
            setBuilder(builder);
        }

        public BigTextStyle setBigContentTitle(CharSequence title) {
            this.Z = title;
            return this;
        }

        public BigTextStyle setSummaryText(CharSequence cs) {
            this.B = cs;
            this.C = true;
            return this;
        }

        public BigTextStyle bigText(CharSequence cs) {
            this.Code = cs;
            return this;
        }
    }

    static class I implements thing {
        I() {
        }

        public final Notification Code(Builder builder) {
            boolean z;
            Context context = builder.Code;
            Notification notification = builder.h;
            CharSequence charSequence = builder.V;
            CharSequence charSequence2 = builder.I;
            CharSequence charSequence3 = builder.F;
            RemoteViews remoteViews = builder.C;
            int i = builder.D;
            PendingIntent pendingIntent = builder.Z;
            PendingIntent pendingIntent2 = builder.B;
            Bitmap bitmap = builder.S;
            int i2 = builder.d;
            int i3 = builder.e;
            boolean z2 = builder.f;
            android.app.Notification.Builder lights = new android.app.Notification.Builder(context).setWhen(notification.when).setSmallIcon(notification.icon, notification.iconLevel).setContent(notification.contentView).setTicker(notification.tickerText, remoteViews).setSound(notification.sound, notification.audioStreamType).setVibrate(notification.vibrate).setLights(notification.ledARGB, notification.ledOnMS, notification.ledOffMS);
            if ((notification.flags & 2) != 0) {
                z = true;
            } else {
                z = false;
            }
            lights = lights.setOngoing(z);
            if ((notification.flags & 8) != 0) {
                z = true;
            } else {
                z = false;
            }
            lights = lights.setOnlyAlertOnce(z);
            if ((notification.flags & 16) != 0) {
                z = true;
            } else {
                z = false;
            }
            android.app.Notification.Builder deleteIntent = lights.setAutoCancel(z).setDefaults(notification.defaults).setContentTitle(charSequence).setContentText(charSequence2).setContentInfo(charSequence3).setContentIntent(pendingIntent).setDeleteIntent(notification.deleteIntent);
            if ((notification.flags & 128) != 0) {
                z = true;
            } else {
                z = false;
            }
            return deleteIntent.setFullScreenIntent(pendingIntent2, z).setLargeIcon(bitmap).setNumber(i).setProgress(i2, i3, z2).getNotification();
        }
    }

    public static class InboxStyle extends Style {
        ArrayList<CharSequence> Code = new ArrayList();

        public InboxStyle(Builder builder) {
            setBuilder(builder);
        }

        public InboxStyle setBigContentTitle(CharSequence title) {
            this.Z = title;
            return this;
        }

        public InboxStyle setSummaryText(CharSequence cs) {
            this.B = cs;
            this.C = true;
            return this;
        }

        public InboxStyle addLine(CharSequence cs) {
            this.Code.add(cs);
            return this;
        }
    }

    @TargetApi(8)
    static class This implements thing {
        This() {
        }

        public Notification Code(Builder builder) {
            Notification notification = builder.h;
            notification.setLatestEventInfo(builder.Code, builder.V, builder.I, builder.Z);
            if (builder.L > 0) {
                notification.flags |= 128;
            }
            return notification;
        }
    }

    static class acknowledge implements thing {
        acknowledge() {
        }

        public final Notification Code(Builder builder) {
            mine cn_bmob_v3_helper_NotificationCompat_mine = new mine(builder.Code, builder.h, builder.V, builder.I, builder.F, builder.C, builder.D, builder.Z, builder.B, builder.S, builder.d, builder.e, builder.f, builder.a, builder.L, builder.c);
            Iterator it = builder.g.iterator();
            while (it.hasNext()) {
                Action action = (Action) it.next();
                cn_bmob_v3_helper_NotificationCompat_mine.Code(action.icon, action.title, action.actionIntent);
            }
            if (builder.b != null) {
                if (builder.b instanceof BigTextStyle) {
                    BigTextStyle bigTextStyle = (BigTextStyle) builder.b;
                    cn_bmob_v3_helper_NotificationCompat_mine.Code(bigTextStyle.Z, bigTextStyle.C, bigTextStyle.B, bigTextStyle.Code);
                } else if (builder.b instanceof InboxStyle) {
                    InboxStyle inboxStyle = (InboxStyle) builder.b;
                    cn_bmob_v3_helper_NotificationCompat_mine.Code(inboxStyle.Z, inboxStyle.C, inboxStyle.B, inboxStyle.Code);
                } else if (builder.b instanceof BigPictureStyle) {
                    BigPictureStyle bigPictureStyle = (BigPictureStyle) builder.b;
                    cn_bmob_v3_helper_NotificationCompat_mine.Code(bigPictureStyle.Z, bigPictureStyle.C, bigPictureStyle.B, bigPictureStyle.Code, bigPictureStyle.V, bigPictureStyle.I);
                }
            }
            return cn_bmob_v3_helper_NotificationCompat_mine.Code();
        }
    }

    static class darkness implements thing {
        darkness() {
        }

        public final Notification Code(Builder builder) {
            boolean z;
            Context context = builder.Code;
            Notification notification = builder.h;
            CharSequence charSequence = builder.V;
            CharSequence charSequence2 = builder.I;
            CharSequence charSequence3 = builder.F;
            RemoteViews remoteViews = builder.C;
            int i = builder.D;
            PendingIntent pendingIntent = builder.Z;
            PendingIntent pendingIntent2 = builder.B;
            Bitmap bitmap = builder.S;
            android.app.Notification.Builder lights = new android.app.Notification.Builder(context).setWhen(notification.when).setSmallIcon(notification.icon, notification.iconLevel).setContent(notification.contentView).setTicker(notification.tickerText, remoteViews).setSound(notification.sound, notification.audioStreamType).setVibrate(notification.vibrate).setLights(notification.ledARGB, notification.ledOnMS, notification.ledOffMS);
            if ((notification.flags & 2) != 0) {
                z = true;
            } else {
                z = false;
            }
            lights = lights.setOngoing(z);
            if ((notification.flags & 8) != 0) {
                z = true;
            } else {
                z = false;
            }
            lights = lights.setOnlyAlertOnce(z);
            if ((notification.flags & 16) != 0) {
                z = true;
            } else {
                z = false;
            }
            android.app.Notification.Builder deleteIntent = lights.setAutoCancel(z).setDefaults(notification.defaults).setContentTitle(charSequence).setContentText(charSequence2).setContentInfo(charSequence3).setContentIntent(pendingIntent).setDeleteIntent(notification.deleteIntent);
            if ((notification.flags & 128) != 0) {
                z = true;
            } else {
                z = false;
            }
            return deleteIntent.setFullScreenIntent(pendingIntent2, z).setLargeIcon(bitmap).setNumber(i).getNotification();
        }
    }

    @TargetApi(9)
    static class of extends This {
        of() {
        }

        public final Notification Code(Builder builder) {
            Notification notification = builder.h;
            notification.setLatestEventInfo(builder.Code, builder.V, builder.I, builder.Z);
            Context context = builder.Code;
            CharSequence charSequence = builder.V;
            CharSequence charSequence2 = builder.I;
            PendingIntent pendingIntent = builder.Z;
            PendingIntent pendingIntent2 = builder.B;
            notification.setLatestEventInfo(context, charSequence, charSequence2, pendingIntent);
            notification.fullScreenIntent = pendingIntent2;
            if (builder.L > 0) {
                notification.flags |= 128;
            }
            return notification;
        }
    }

    static {
        if (VERSION.SDK_INT >= 16) {
            Code = new acknowledge();
        } else if (VERSION.SDK_INT >= 14) {
            Code = new I();
        } else if (VERSION.SDK_INT >= 11) {
            Code = new darkness();
        } else if (VERSION.SDK_INT >= 9) {
            Code = new of();
        } else {
            Code = new This();
        }
    }
}
