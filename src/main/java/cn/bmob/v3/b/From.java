package cn.bmob.v3.b;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConstants;

/* compiled from: ManifestUtils */
public final class From {
    private static final Object Code = new Object();
    private static int V = -1;

    public static int Code() {
        synchronized (Code) {
            if (V == -1) {
                try {
                    V = Bmob.getApplicationContext().getPackageManager().getPackageInfo(Bmob.getApplicationContext().getPackageName(), 0).versionCode;
                } catch (Throwable e) {
                    Log.e(BmobConstants.TAG, "Couldn't find info about own package", e);
                }
            }
        }
        return V;
    }

    private static ApplicationInfo Code(Context context, int i) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static String Code(Context context) {
        String str = "4";
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            if ("WIFI".equals(activeNetworkInfo.getTypeName())) {
                return "3";
            }
            String extraInfo = activeNetworkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.equals("3gwap") || extraInfo.equals("3gnet")) {
                    return "2";
                }
                if (extraInfo.equals("uninet") || extraInfo.equals("uniwap") || extraInfo.equals("cmwap") || extraInfo.equals("cmnet") || extraInfo.equals("ctwap") || extraInfo.equals("ctnet")) {
                    return "1";
                }
                return "4";
            }
        }
        return str;
    }

    private static String I(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String[] strArr = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            strArr = packageManager.getPackageInfo(context.getPackageName(), 4096).requestedPermissions;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (strArr != null) {
            for (String append : strArr) {
                stringBuffer.append(append).append(",");
            }
        }
        return stringBuffer.toString();
    }

    public static boolean V(Context context) {
        if (I(context).contains("android.permission.ACCESS_NETWORK_STATE")) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService("connectivity");
            if (connectivityManager == null) {
                return false;
            }
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isAvailable()) {
                return false;
            }
            return true;
        }
        Log.w(BmobConstants.TAG, "No android.permission.ACCESS_NETWORK_STATE Privileges.");
        return false;
    }

    public static String Code(String str) {
        Bundle bundle;
        ApplicationInfo Code = Code(Bmob.getApplicationContext(), 128);
        if (Code != null) {
            bundle = Code.metaData;
        } else {
            bundle = null;
        }
        if (bundle != null) {
            return bundle.getString(str);
        }
        return null;
    }
}
