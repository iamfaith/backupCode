package cn.bmob.v3.http;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import cn.bmob.v3.b.mine;
import cn.bmob.v3.helper.BmobNative;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.security.auth.x500.X500Principal;

/* compiled from: RequestUtils */
public final class I {
    private static final X500Principal Code = new X500Principal("CN=Android Debug,O=Android,C=US");

    public static String Code() {
        return BmobNative.getAcceptId();
    }

    public static String Code(String str, String str2) {
        return BmobNative.encrypt(str, str2);
    }

    public static String V(String str, String str2) {
        return BmobNative.decrypt(str, str2);
    }

    public static String Code(String str) {
        return BmobNative.encryptByKey(str);
    }

    public static String V(String str) {
        return BmobNative.decryptByKey(str);
    }

    public static String V() {
        return BmobNative.getAppId();
    }

    public static long I() {
        return (System.currentTimeMillis() / 1000) - ((long) Integer.parseInt(BmobNative.getInterval()));
    }

    public static String Code(Context context) {
        if (B(context)) {
            return Z(context) + "/0";
        }
        return Z(context) + "/1";
    }

    private static String Z(Context context) {
        String str = "";
        try {
            str = mine.Code(context.getPackageManager().getPackageInfo(context.getPackageName(), 64).signatures[0].toByteArray());
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return str;
    }

    private static boolean B(Context context) {
        boolean z = false;
        boolean equals;
        try {
            Signature[] signatureArr = context.getPackageManager().getPackageInfo(context.getPackageName(), 64).signatures;
            int i = 0;
            while (i < signatureArr.length) {
                equals = ((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(signatureArr[i].toByteArray()))).getSubjectX500Principal().equals(Code);
                if (equals) {
                    return equals;
                }
                i++;
                z = equals;
            }
            return z;
        } catch (Exception e) {
            Exception exception = e;
            equals = false;
            exception.printStackTrace();
            return equals;
        }
    }

    public static String V(Context context) {
        String Z = Z();
        String str = Z + S(context);
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes(), 0, str.length());
            byte[] digest = instance.digest();
            String str2 = new String();
            int i = 0;
            while (i < digest.length) {
                int i2 = digest[i] & 255;
                if (i2 <= 15) {
                    str2 = str2 + "0";
                }
                i++;
                str2 = str2 + Integer.toHexString(i2);
            }
            return str2.toUpperCase(Locale.CHINA);
        } catch (Exception e) {
            e.printStackTrace();
            return str.toUpperCase(Locale.CHINA);
        }
    }

    public static String I(Context context) {
        String str = C(context) + Z() + S(context) + F(context) + D(context);
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes(), 0, str.length());
            byte[] digest = instance.digest();
            String str2 = new String();
            for (byte b : digest) {
                int i = b & 255;
                if (i <= 15) {
                    str2 = str2 + "0";
                }
                str2 = str2 + Integer.toHexString(i);
            }
            return str2.toUpperCase(Locale.CHINA);
        } catch (Exception e) {
            e.printStackTrace();
            return str.toLowerCase(Locale.CHINA);
        }
    }

    private static String C(Context context) {
        if (!Code(context, "android.permission.READ_PHONE_STATE")) {
            return "";
        }
        try {
            return ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
        } catch (Exception e) {
            return "";
        }
    }

    private static String Z() {
        return "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.DISPLAY.length() % 10) + (Build.HOST.length() % 10) + (Build.ID.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10) + (Build.TAGS.length() % 10) + (Build.TYPE.length() % 10) + (Build.USER.length() % 10);
    }

    private static String S(Context context) {
        try {
            return Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception e) {
            return "";
        }
    }

    private static String F(Context context) {
        if (!Code(context, "android.permission.ACCESS_WIFI_STATE")) {
            return "";
        }
        try {
            return ((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getMacAddress();
        } catch (Throwable th) {
            return "";
        }
    }

    private static String D(Context context) {
        if (!Code(context, "android.permission.BLUETOOTH")) {
            return "";
        }
        try {
            return BluetoothAdapter.getDefaultAdapter().getAddress();
        } catch (Throwable th) {
            return "";
        }
    }

    private static boolean Code(Context context, String str) {
        String[] strArr = null;
        try {
            strArr = context.getPackageManager().getPackageInfo(context.getPackageName(), 4096).requestedPermissions;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        List arrayList = new ArrayList();
        if (strArr != null && strArr.length > 0) {
            for (Object add : strArr) {
                arrayList.add(add);
            }
        }
        if (arrayList.contains(str)) {
            return true;
        }
        return false;
    }
}
