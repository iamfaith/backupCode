package cn.bmob.v3.b;

import android.text.TextUtils;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConstants;
import cn.bmob.v3.a.b.This;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

/* compiled from: CacheHelper */
public final class darkness {
    private static int Code = 2097152;
    private static int V = BmobConstants.TIME_DELAY_RETRY;

    public static String Code(JSONObject jSONObject) {
        try {
            if (jSONObject.has("timestamp")) {
                jSONObject.remove("timestamp");
            }
            if (jSONObject.has("sessionToken")) {
                jSONObject.remove("sessionToken");
            }
            jSONObject.remove("client");
            jSONObject.remove("v");
            jSONObject.remove("appSign");
            JSONStringer jSONStringer = new JSONStringer();
            Code(jSONStringer, (Object) jSONObject);
            String jSONStringer2 = jSONStringer.toString();
            Object V = new The().V("sessionToken", "");
            if (!TextUtils.isEmpty(V)) {
                jSONStringer2 = jSONStringer2 + V;
            }
            return "RequestCommand.find.3." + This.Code(jSONStringer2);
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void Code(JSONStringer jSONStringer, Object obj) throws JSONException {
        if (obj instanceof JSONObject) {
            jSONStringer.object();
            JSONObject jSONObject = (JSONObject) obj;
            Iterator keys = jSONObject.keys();
            Object arrayList = new ArrayList();
            while (keys.hasNext()) {
                arrayList.add((String) keys.next());
            }
            Collections.sort(arrayList);
            keys = arrayList.iterator();
            while (keys.hasNext()) {
                String str = (String) keys.next();
                jSONStringer.key(str);
                Code(jSONStringer, jSONObject.opt(str));
            }
            jSONStringer.endObject();
        } else if (obj instanceof JSONArray) {
            JSONArray jSONArray = (JSONArray) obj;
            jSONStringer.array();
            for (int i = 0; i < jSONArray.length(); i++) {
                Code(jSONStringer, jSONArray.get(i));
            }
            jSONStringer.endArray();
        } else {
            jSONStringer.value(obj);
        }
    }

    public static void Code(String str, String str2) {
        int i = 0;
        File V = V(str);
        if (V != null) {
            V.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(V(), String.valueOf(new Date().getTime()) + '.' + str));
            fileOutputStream.write(str2.getBytes(HTTP.UTF_8));
            fileOutputStream.close();
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e2) {
        }
        File[] listFiles = V().listFiles();
        int length = listFiles.length;
        int i2 = 0;
        int i3 = 0;
        while (i2 < listFiles.length) {
            i2++;
            i3 = (int) (((long) i3) + listFiles[i2].length());
        }
        if (length > V || i3 > Code) {
            Arrays.sort(listFiles, new Comparator<Object>() {
                public final int compare(Object lhs, Object rhs) {
                    int compareTo = Long.valueOf(((File) lhs).lastModified()).compareTo(Long.valueOf(((File) rhs).lastModified()));
                    return compareTo != 0 ? compareTo : ((File) lhs).getName().compareTo(((File) rhs).getName());
                }
            });
            i2 = listFiles.length;
            while (i < i2) {
                File file = listFiles[i];
                length--;
                i3 = (int) (((long) i3) - file.length());
                file.delete();
                if (length > V || i3 > Code) {
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    private static File V(String str) {
        final String str2 = "." + str;
        File[] listFiles = V().listFiles(new FilenameFilter() {
            public final boolean accept(File file, String filename) {
                return filename.endsWith(str2);
            }
        });
        return listFiles.length == 0 ? null : listFiles[0];
    }

    private static synchronized File V() {
        File cacheDir;
        synchronized (darkness.class) {
            cacheDir = Bmob.getCacheDir("BmobKeyValueCache");
            if (cacheDir.isDirectory() || cacheDir.mkdir()) {
            } else {
                throw new RuntimeException("could not create Bmob cache directory");
            }
        }
        return cacheDir;
    }

    public static Object Code(String str, long j) {
        Object obj = null;
        String V = V(str, j);
        if (V != null) {
            try {
                obj = new JSONTokener(cn.bmob.v3.datatype.a.This.Z(V)).nextValue();
            } catch (JSONException e) {
                Code(str);
            }
        }
        return obj;
    }

    public static String V(String str, long j) {
        IOException e;
        Throwable th;
        File V = V(str);
        if (V == null) {
            return null;
        }
        Date date = new Date();
        if (Code(V) < Math.max(0, date.getTime() - j)) {
            return null;
        }
        V.setLastModified(date.getTime());
        Closeable randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(V, "r");
            try {
                byte[] bArr = new byte[((int) randomAccessFile.length())];
                randomAccessFile.readFully(bArr);
                randomAccessFile.close();
                String str2 = new String(bArr, HTTP.UTF_8);
                cn.bmob.v3.datatype.a.This.Code(randomAccessFile);
                return str2;
            } catch (IOException e2) {
                e = e2;
                try {
                    e.printStackTrace();
                    cn.bmob.v3.datatype.a.This.Code(randomAccessFile);
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    cn.bmob.v3.datatype.a.This.Code(randomAccessFile);
                    throw th;
                }
            }
        } catch (IOException e3) {
            e = e3;
            randomAccessFile = null;
            e.printStackTrace();
            cn.bmob.v3.datatype.a.This.Code(randomAccessFile);
            return null;
        } catch (Throwable th3) {
            randomAccessFile = null;
            th = th3;
            cn.bmob.v3.datatype.a.This.Code(randomAccessFile);
            throw th;
        }
    }

    private static long Code(File file) {
        String name = file.getName();
        try {
            return Long.parseLong(name.substring(0, name.indexOf(46)));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void Code(String str) {
        File V = V(str);
        if (V != null && V.exists()) {
            V.delete();
        }
    }

    public static void Code() {
        File[] listFiles = V().listFiles();
        if (listFiles != null) {
            for (File delete : listFiles) {
                delete.delete();
            }
        }
    }
}
