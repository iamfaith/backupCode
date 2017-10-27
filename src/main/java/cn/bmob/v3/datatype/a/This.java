package cn.bmob.v3.datatype.a;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Looper;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConstants;
import cn.bmob.v3.b.thing;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.http.HttpHost;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

/* compiled from: AsyncRun */
public class This {
    public byte[] Code;
    public String I;
    public Map<String, String> V;

    public static void Code(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static String Code(Map<String, Object> map) {
        return thing.Code(new JSONObject(map).toString());
    }

    public static String Code(Map<String, Object> map, String str) {
        Object[] toArray = map.keySet().toArray();
        Arrays.sort(toArray);
        StringBuffer stringBuffer = new StringBuffer("");
        for (Object obj : toArray) {
            stringBuffer.append(obj).append(map.get(obj));
        }
        stringBuffer.append(str);
        return Code(stringBuffer.toString().getBytes());
    }

    public static String Code(byte[] bArr) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(bArr);
            StringBuilder stringBuilder = new StringBuilder(digest.length << 1);
            for (byte b : digest) {
                if ((b & 255) < 16) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(Integer.toHexString(b & 255));
            }
            return stringBuilder.toString();
        } catch (Throwable e) {
            throw new RuntimeException("MessageDigest不支持MD5Util", e);
        }
    }

    public static String Code(String str) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(str.getBytes(HTTP.UTF_8));
            StringBuilder stringBuilder = new StringBuilder(digest.length << 1);
            for (byte b : digest) {
                if ((b & 255) < 16) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(Integer.toHexString(b & 255));
            }
            return stringBuilder.toString();
        } catch (Throwable e) {
            throw new RuntimeException("UTF-8 is unsupported", e);
        } catch (Throwable e2) {
            throw new RuntimeException("MessageDigest不支持MD5Util", e2);
        }
    }

    public static String Code(File file) {
        try {
            int read;
            int fileBlockSize = Bmob.getFileBlockSize();
            MessageDigest instance = MessageDigest.getInstance("MD5");
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bArr = new byte[fileBlockSize];
            while (true) {
                read = fileInputStream.read(bArr);
                if (read <= 0) {
                    break;
                }
                instance.update(bArr, 0, read);
            }
            bArr = instance.digest();
            StringBuilder stringBuilder = new StringBuilder(bArr.length << 1);
            for (byte b : bArr) {
                if ((b & 255) < 16) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(Integer.toHexString(b & 255));
            }
            try {
                fileInputStream.close();
            } catch (Exception e) {
            }
            return stringBuilder.toString();
        } catch (Throwable e2) {
            throw new RuntimeException("file not found", e2);
        } catch (Throwable e22) {
            throw new RuntimeException("file get md5 failed", e22);
        } catch (Throwable e222) {
            throw new RuntimeException("MessageDigest不支持MD5Util", e222);
        }
    }

    public static int Code(File file, int i) {
        return (int) Math.ceil(((double) file.length()) / ((double) i));
    }

    public static byte[] Code(String str, String str2) {
        if (str == null) {
            return null;
        }
        try {
            return str.getBytes(str2);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(str2 + ": " + e);
        }
    }

    public static void Code(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public static void Code(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public static void Code(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static boolean Code() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static String V(byte[] bArr) {
        return new String(bArr, Charset.forName("utf-8"));
    }

    public static boolean Code(String str, int i, int i2) {
        return Pattern.compile("^[a-zA-Z]\\w{" + 1 + ",49" + "}$").matcher(str).matches();
    }

    public static boolean Code(Context context, String str) {
        try {
            String[] strArr = context.getPackageManager().getPackageInfo(context.getPackageName(), 4096).requestedPermissions;
            if (strArr == null || strArr.length <= 0) {
                return false;
            }
            for (String equals : strArr) {
                if (equals.equals(str)) {
                    return true;
                }
            }
            return false;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Object> Code(JSONArray jSONArray) {
        ArrayList<Object> arrayList = new ArrayList();
        if (jSONArray != null) {
            try {
                if (jSONArray.length() > 0) {
                    int length = jSONArray.length();
                    for (int i = 0; i < length; i++) {
                        arrayList.add(jSONArray.get(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    public static ArrayList Code(ArrayList arrayList) {
        ArrayList arrayList2 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (!arrayList2.contains(next)) {
                arrayList2.add(next);
            }
        }
        return arrayList2;
    }

    public static JSONArray V(ArrayList arrayList) {
        JSONArray jSONArray = new JSONArray();
        if (arrayList != null) {
            try {
                if (arrayList.size() > 0) {
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        jSONArray.put(arrayList.get(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jSONArray;
    }

    public static String Code(String[] strArr) {
        StringBuilder stringBuilder = new StringBuilder();
        if (strArr != null && strArr.length > 0) {
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                if (i != length - 1) {
                    stringBuilder.append(strArr[i]);
                    stringBuilder.append(",");
                } else {
                    stringBuilder.append(strArr[i]);
                }
            }
        }
        return stringBuilder.toString();
    }

    public static Map<String, Map<String, Object>> Code(JSONObject jSONObject) {
        Map<String, Map<String, Object>> hashMap = new HashMap();
        try {
            Iterator keys = jSONObject.keys();
            while (keys.hasNext()) {
                String str = (String) keys.next();
                JSONObject jSONObject2 = jSONObject.getJSONObject(str);
                Map hashMap2 = new HashMap();
                if (jSONObject2 != null) {
                    Iterator keys2 = jSONObject2.keys();
                    while (keys2.hasNext()) {
                        String str2 = (String) keys2.next();
                        hashMap2.put(str2, jSONObject2.get(str2));
                    }
                    hashMap.put(str, hashMap2);
                }
            }
        } catch (Exception e) {
        }
        return hashMap;
    }

    public static boolean V(File file) {
        try {
            if (file.exists()) {
                if (!file.isDirectory()) {
                    throw new IOException("File " + file + " exists and is not a directory. Unable to create directory.");
                }
            } else if (!(file.mkdirs() || file.isDirectory())) {
                throw new IOException("Unable to create directory " + file);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String V(String str) {
        if (str.startsWith(HttpHost.DEFAULT_SCHEME_NAME)) {
            try {
                str = str.replace("http://" + str.split("/")[2], "");
            } catch (Exception e) {
            }
        }
        return str;
    }

    public static String I(String str) {
        if (!(str == null || str.length() == 0)) {
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                gZIPOutputStream.write(str.getBytes());
                gZIPOutputStream.close();
                str = byteArrayOutputStream.toString("ISO-8859-1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String Z(String str) {
        OutputStream byteArrayOutputStream;
        InputStream byteArrayInputStream;
        IOException e;
        InputStream inputStream;
        InputStream inputStream2;
        Throwable th;
        OutputStream outputStream = null;
        if (str == null || str.length() == 0) {
            return str;
        }
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                byteArrayInputStream = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
            } catch (IOException e2) {
                e = e2;
                inputStream = null;
                try {
                    e.printStackTrace();
                    Code(inputStream);
                    Code(inputStream2);
                    Code(byteArrayOutputStream);
                    return r0.toString();
                } catch (Throwable th2) {
                    byteArrayInputStream = inputStream;
                    inputStream = inputStream2;
                    outputStream = byteArrayOutputStream;
                    th = th2;
                    Code(byteArrayInputStream);
                    Code(inputStream);
                    Code(outputStream);
                    throw th;
                }
            } catch (Throwable th22) {
                inputStream = null;
                byteArrayInputStream = null;
                outputStream = byteArrayOutputStream;
                th = th22;
                Code(byteArrayInputStream);
                Code(inputStream);
                Code(outputStream);
                throw th;
            }
            try {
                inputStream = new GZIPInputStream(byteArrayInputStream);
            } catch (IOException e3) {
                e = e3;
                inputStream = byteArrayInputStream;
                e.printStackTrace();
                Code(inputStream);
                Code(inputStream2);
                Code(byteArrayOutputStream);
                return r0.toString();
            } catch (Throwable th222) {
                inputStream = null;
                outputStream = byteArrayOutputStream;
                th = th222;
                Code(byteArrayInputStream);
                Code(inputStream);
                Code(outputStream);
                throw th;
            }
            try {
                byte[] bArr = new byte[256];
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read < 0) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr, 0, read);
                }
                Code(byteArrayInputStream);
                Code(inputStream);
                Code(byteArrayOutputStream);
            } catch (IOException e4) {
                e = e4;
                inputStream2 = inputStream;
                inputStream = byteArrayInputStream;
                e.printStackTrace();
                Code(inputStream);
                Code(inputStream2);
                Code(byteArrayOutputStream);
                return r0.toString();
            } catch (Throwable th2222) {
                outputStream = byteArrayOutputStream;
                th = th2222;
                Code(byteArrayInputStream);
                Code(inputStream);
                Code(outputStream);
                throw th;
            }
        } catch (IOException e5) {
            e = e5;
            inputStream = null;
            byteArrayOutputStream = null;
            e.printStackTrace();
            Code(inputStream);
            Code(inputStream2);
            Code(byteArrayOutputStream);
            return r0.toString();
        } catch (Throwable th3) {
            th = th3;
            inputStream = null;
            byteArrayInputStream = null;
            Code(byteArrayInputStream);
            Code(inputStream);
            Code(outputStream);
            throw th;
        }
        return r0.toString();
    }

    public static String I(byte[] bArr) {
        int i = 1;
        StringBuilder stringBuilder = new StringBuilder();
        if (bArr.length >= 2) {
            byte[] bArr2 = new byte[]{bArr[0], bArr[1]};
            if (((bArr2[1] & 255) | (bArr2[0] << 8)) != 8075) {
                i = 0;
            }
            try {
                InputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
                if (i != 0) {
                    byteArrayInputStream = new GZIPInputStream(byteArrayInputStream);
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream), BmobConstants.TIME_DELAY_RETRY);
                for (String readLine = bufferedReader.readLine(); readLine != null; readLine = bufferedReader.readLine()) {
                    stringBuilder.append(readLine);
                }
                byteArrayInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            stringBuilder.append(new String(bArr));
        }
        return stringBuilder.toString();
    }
}
