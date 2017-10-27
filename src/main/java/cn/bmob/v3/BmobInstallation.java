package cn.bmob.v3;

import android.content.Context;
import android.util.Log;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.http.I;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

public class BmobInstallation extends BmobObject {
    private static final long serialVersionUID = 371823543247670010L;
    private static boolean subOrUnsub = true;
    private List<String> channels = new ArrayList();
    private String deviceToken;
    private String deviceType = "android";
    private String installationId;
    private String timeZone;

    static /* synthetic */ Observable Code(BmobInstallation bmobInstallation, BmobInstallation bmobInstallation2, List list) {
        if (subOrUnsub) {
            bmobInstallation2.addAllUnique("channels", list);
        } else {
            bmobInstallation2.removeAll("channels", list);
        }
        bmobInstallation2.setDeviceType(null);
        bmobInstallation2.setTimeZone(null);
        bmobInstallation2.setInstallationId(null);
        return bmobInstallation2.updateObservable();
    }

    public BmobInstallation() {
        this.channels.clear();
        setChannels(this.channels);
        setInstallationId(I.V(Bmob.getApplicationContext()));
        setTimeZone(TimeZone.getDefault().getID());
    }

    public String getTableName() {
        return "_Installation";
    }

    public static BmobInstallation getCurrentInstallation() {
        return new BmobInstallation();
    }

    public static <T> BmobQuery<T> getQuery() {
        return new BmobQuery();
    }

    public String getDeviceToken() {
        return this.deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public List<String> getChannels() {
        return Collections.unmodifiableList(this.channels);
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public String getInstallationId() {
        return this.installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void subscribe(String channel) {
        subOrUnsub = true;
        this.channels.add(channel);
    }

    public void unsubscribe(String channel) {
        subOrUnsub = false;
        this.channels.add(channel);
    }

    public static String getInstallationId(Context context) {
        return I.V(context);
    }

    public Subscription save() {
        return Code(getChannels()).subscribe(new Subscriber<Void>(this) {
            public final void onCompleted() {
            }

            public final void onError(Throwable e) {
                Log.i(BmobConstants.TAG, e.getMessage());
            }

            public final /* synthetic */ void onNext(Object obj) {
                Log.i(BmobConstants.TAG, "updatae or save success");
            }
        });
    }

    public Observable<Void> updateObservable() {
        return Code(getChannels());
    }

    private Observable<Void> Code(final List<String> list) {
        BmobQuery bmobQuery = new BmobQuery();
        bmobQuery.addWhereEqualTo("installationId", getInstallationId());
        return bmobQuery.findObjectsObservable(BmobInstallation.class).concatMap(new Func1<List<BmobInstallation>, Observable<Void>>(this) {
            private /* synthetic */ BmobInstallation V;

            public final /* synthetic */ Object call(Object obj) {
                List list = (List) obj;
                if (list == null || list.size() <= 0) {
                    return BmobInstallation.Code(this.V);
                }
                return BmobInstallation.Code(this.V, (BmobInstallation) list.get(0), list);
            }
        }).onErrorResumeNext(new Func1<Throwable, Observable<Void>>(this) {
            private /* synthetic */ BmobInstallation Code;

            {
                this.Code = r1;
            }

            public final /* synthetic */ Object call(Object obj) {
                Throwable th = (Throwable) obj;
                if ((th instanceof BmobException) && ((BmobException) th).getErrorCode() == 101) {
                    return BmobInstallation.Code(this.Code);
                }
                return Observable.error(th);
            }
        });
    }

    static /* synthetic */ Observable Code(BmobInstallation bmobInstallation) {
        BmobInstallation currentInstallation = getCurrentInstallation();
        if (subOrUnsub) {
            currentInstallation.addAllUnique("channels", bmobInstallation.channels);
        }
        return currentInstallation.saveObservable().map(new Func1<String, Void>(bmobInstallation) {
            public final /* bridge */ /* synthetic */ Object call(Object obj) {
                return null;
            }
        });
    }
}
