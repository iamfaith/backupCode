package cn.bmob.v3.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.b.From;
import cn.bmob.v3.b.The;
import cn.bmob.v3.b.This;
import cn.bmob.v3.b.acknowledge;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobDialogButtonListener;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.FindListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

public class BmobUpdateAgent {
    private static BmobDialogButtonListener Code;
    private static BmobUpdateListener V = null;

    static /* synthetic */ void Code(Context context, UpdateResponse updateResponse, File file, boolean z) {
        if (From.Code() < updateResponse.version_i.intValue()) {
            Intent intent = new Intent(context, UpdateDialogActivity.class);
            intent.putExtra("response", updateResponse);
            if (file.exists()) {
                intent.putExtra("file", file.getPath());
            }
            intent.putExtra("showCheckBox", z);
            context.startActivity(intent);
        } else if (V != null) {
            V.onUpdateReturned(1, new UpdateResponse(1, "已经是最新版本了，无需更新。"));
        }
    }

    public static void initAppVersion() {
        AppVersion appVersion = new AppVersion();
        appVersion.setUpdate_log("");
        appVersion.setVersion("");
        appVersion.setVersion_i(Integer.valueOf(0));
        appVersion.setTarget_size("0");
        appVersion.setPath(BmobFile.createEmptyFile());
        appVersion.setAndroid_url("");
        appVersion.setChannel("");
        appVersion.setIos_url("");
        appVersion.setIsforce(Boolean.valueOf(false));
        appVersion.setPlatform("Android");
        final Observable map = appVersion.saveObservable().map(new Func1<String, Void>() {
            public final /* bridge */ /* synthetic */ Object call(Object obj) {
                return null;
            }
        });
        BmobQuery bmobQuery = new BmobQuery();
        bmobQuery.setLimit(1);
        bmobQuery.findObjectsObservable(AppVersion.class).concatMap(new Func1<List<AppVersion>, Observable<Void>>() {
            public final /* synthetic */ Object call(Object obj) {
                List list = (List) obj;
                if (list == null || list.size() <= 0) {
                    return map;
                }
                return Observable.error(new BmobException("AppVersion is exists,no need recreate"));
            }
        }).onErrorResumeNext(new Func1<Throwable, Observable<? extends Void>>() {
            public final /* synthetic */ Object call(Object obj) {
                Throwable th = (Throwable) obj;
                if ((th instanceof BmobException) && ((BmobException) th).getErrorCode() == 101) {
                    return map;
                }
                return Observable.error(th);
            }
        }).subscribe(new Observer<Void>() {
            public final void onError(Throwable throwable) {
                This.Code(throwable.getMessage());
            }

            public final void onCompleted() {
            }

            public final /* synthetic */ void onNext(Object obj) {
                This.V("AppVersion create success");
            }
        });
    }

    public static void update(Context context) {
        if ("3".equals(From.Code(context)) || !cn.bmob.v3.update.a.This.Code()) {
            cn.bmob.v3.update.a.This.Z(false);
            cn.bmob.v3.update.a.This.V(false);
            V(context);
        }
    }

    public static void forceUpdate(Context context) {
        cn.bmob.v3.update.a.This.Z(true);
        cn.bmob.v3.update.a.This.V(false);
        V(context);
    }

    public static void silentUpdate(Context context) {
        if ("3".equals(From.Code(context)) || !cn.bmob.v3.update.a.This.Code()) {
            cn.bmob.v3.update.a.This.Z(false);
            cn.bmob.v3.update.a.This.V(true);
            V(context);
        }
    }

    public static void setDefault() {
        setUpdateOnlyWifi(true);
        setUpdateListener(null);
        setDialogListener(null);
    }

    public static void setUpdateOnlyWifi(boolean updateOnlyWifi) {
        cn.bmob.v3.update.a.This.Code(updateOnlyWifi);
    }

    public static void setUpdateCheckConfig(boolean isCheck) {
        cn.bmob.v3.update.a.This.I(isCheck);
    }

    public static void setUpdateListener(BmobUpdateListener paramBmobUpdateListener) {
        V = paramBmobUpdateListener;
    }

    public static void setDialogListener(BmobDialogButtonListener buttonListener) {
        Code = buttonListener;
    }

    static void Code(int i, Context context, UpdateResponse updateResponse, File file) {
        switch (i) {
            case 6:
                if (!(cn.bmob.v3.update.a.This.I() || Code == null)) {
                    Code.onClick(6);
                }
                if (file == null || !file.exists()) {
                    Code(false, context, updateResponse, file);
                    return;
                }
                long length = file.length();
                long j = updateResponse.target_size;
                if (j <= 0) {
                    file.delete();
                    if (V != null) {
                        V.onUpdateReturned(2, new UpdateResponse(2, ""));
                        return;
                    }
                    return;
                } else if (length >= j) {
                    startInstall(context, file);
                    return;
                } else {
                    Code(true, context, updateResponse, file);
                    return;
                }
            case 7:
                if (Code != null) {
                    Code.onClick(7);
                    return;
                }
                return;
            case 8:
                if (Code != null) {
                    Code.onClick(8);
                    return;
                }
                return;
            default:
                return;
        }
    }

    private static void Code(boolean z, Context context, UpdateResponse updateResponse, File file) {
        if (z) {
            file.delete();
        }
        new acknowledge(context, updateResponse.path).Code();
    }

    private static boolean Code(Context context) {
        if (!cn.bmob.v3.update.a.This.Z()) {
            return true;
        }
        try {
            int i;
            boolean z;
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 4101);
            if (packageInfo.activities != null) {
                i = 0;
                z = false;
                while (i < packageInfo.activities.length) {
                    if ("cn.bmob.v3.update.UpdateDialogActivity".equals(packageInfo.activities[i].name)) {
                        z = true;
                    }
                    i++;
                }
            } else {
                i = 0;
                z = false;
            }
            if (z) {
                boolean z2;
                if (packageInfo.requestedPermissions != null) {
                    z = false;
                    z2 = false;
                    for (int i2 = 0; i2 < packageInfo.requestedPermissions.length; i2++) {
                        if ("android.permission.WRITE_EXTERNAL_STORAGE".equals(packageInfo.requestedPermissions[i2])) {
                            i = 1;
                        } else if ("android.permission.ACCESS_NETWORK_STATE".equals(packageInfo.requestedPermissions[i2])) {
                            z2 = true;
                        } else if ("android.permission.INTERNET".equals(packageInfo.requestedPermissions[i2])) {
                            z = true;
                        }
                    }
                } else {
                    z = false;
                    z2 = false;
                }
                if (i != 0 && r2 && r0) {
                    z = true;
                } else {
                    z = false;
                }
                if (z) {
                    return true;
                }
                Toast.makeText(context, "Please add Permission in AndroidManifest!", 0).show();
                return false;
            }
            Toast.makeText(context, "Please add Activity in AndroidManifest!", 0).show();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static void startInstall(Context paramContext, File paramFile) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(268435456);
        intent.setDataAndType(Uri.fromFile(paramFile), "application/vnd.android.package-archive");
        paramContext.startActivity(intent);
    }

    private static void V(final Context context) {
        if (Code(context)) {
            BmobQuery bmobQuery = new BmobQuery();
            bmobQuery.addWhereEqualTo("platform", "Android");
            CharSequence V = cn.bmob.v3.update.a.This.V();
            if (!TextUtils.isEmpty(V)) {
                bmobQuery.addWhereEqualTo("channel", V);
            }
            bmobQuery.addWhereGreaterThan("version_i", Integer.valueOf(From.Code()));
            bmobQuery.order("-version_i");
            bmobQuery.findObjects(new FindListener<AppVersion>() {
                public final void done(List<AppVersion> objects, BmobException e) {
                    if (e == null) {
                        if (objects != null && objects.size() > 0) {
                            UpdateResponse updateResponse = new UpdateResponse((AppVersion) objects.get(0));
                            if (updateResponse.target_size <= 0) {
                                if (BmobUpdateAgent.V != null) {
                                    BmobUpdateAgent.V.onUpdateReturned(2, new UpdateResponse(2, "target_size为空或格式不对，请填写apk文件大小(long类型)。"));
                                }
                            } else if (!TextUtils.isEmpty(updateResponse.path)) {
                                if (BmobUpdateAgent.V != null) {
                                    BmobUpdateAgent.V.onUpdateReturned(0, updateResponse);
                                }
                                File file = new File(Environment.getExternalStorageDirectory(), updateResponse.path_md5 + ".apk");
                                if (cn.bmob.v3.update.a.This.I()) {
                                    if (file.exists()) {
                                        BmobUpdateAgent.Code(context, updateResponse, file, false);
                                    } else {
                                        BmobUpdateAgent.Code(6, context, updateResponse, file);
                                    }
                                } else if (cn.bmob.v3.update.a.This.B()) {
                                    BmobUpdateAgent.Code(context, updateResponse, file, false);
                                } else if (!BmobUpdateAgent.isIgnored(String.valueOf(updateResponse.version_i))) {
                                    BmobUpdateAgent.Code(context, updateResponse, file, true);
                                } else if (BmobUpdateAgent.V != null) {
                                    BmobUpdateAgent.V.onUpdateReturned(3, new UpdateResponse(3, "该版本已被忽略更新"));
                                }
                            } else if (BmobUpdateAgent.V != null) {
                                BmobUpdateAgent.V.onUpdateReturned(2, new UpdateResponse(2, "path/android_url需填写其中之一。"));
                            }
                        } else if (BmobUpdateAgent.V != null) {
                            BmobUpdateAgent.V.onUpdateReturned(1, new UpdateResponse(1, "未查询到版本更新信息。"));
                        }
                    } else if (BmobUpdateAgent.V != null) {
                        BmobUpdateAgent.V.onUpdateReturned(-1, new UpdateResponse(e.getErrorCode(), e.getMessage()));
                    }
                }
            });
        }
    }

    public static boolean isIgnored(String version) {
        List V = V();
        if (V.size() <= 0 || !V.contains(version)) {
            return false;
        }
        return true;
    }

    private static List<String> V() {
        String V = new The().V("ignoreversions", "");
        if (V.equals("")) {
            return new ArrayList();
        }
        List<String> arrayList;
        if (V.contains("&")) {
            String[] split = V.split("&");
            arrayList = new ArrayList();
            for (Object add : split) {
                arrayList.add(add);
            }
            return arrayList;
        }
        arrayList = new ArrayList();
        arrayList.add(V);
        return arrayList;
    }

    public static void add2IgnoreVersion(String version) {
        List V = V();
        if (V.size() <= 0) {
            V.add(version);
        } else if (!V.contains(version)) {
            V.add(version);
        }
        Code(V);
    }

    public static void deleteIgnoreVersion(String version) {
        List V = V();
        if (V.size() > 0 && V.contains(version)) {
            V.remove(version);
        }
        Code(V);
    }

    private static void Code(List<String> list) {
        if (list == null || list.size() <= 0) {
            new The().Code("ignoreversions");
            return;
        }
        int size = list.size();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            stringBuilder.append((String) list.get(i));
            if (i != size - 1) {
                stringBuilder.append("&");
            }
        }
        new The().Code("ignoreversions", stringBuilder.toString());
    }
}
