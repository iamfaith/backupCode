package cn.bmob.v3;

import android.content.Context;
import cn.bmob.v3.helper.BmobNative;
import java.io.File;

public class BmobWrapper {
    private static byte[] Code = new byte[0];
    private static volatile BmobWrapper V;
    private final long B;
    private final int C;
    private File D;
    private File F;
    private final String I;
    private final long S;
    private final Context Z;

    static void Code(BmobConfig bmobConfig) {
        V(bmobConfig);
    }

    private static BmobWrapper V(BmobConfig bmobConfig) {
        if (V == null) {
            synchronized (Code) {
                if (V == null) {
                    V = new BmobWrapper(bmobConfig);
                }
            }
        }
        return V;
    }

    private BmobWrapper(BmobConfig config) {
        this.Z = config.context.getApplicationContext();
        this.I = config.applicationId;
        this.B = config.connectTimeout;
        this.C = config.uploadBlockSize;
        this.S = config.fileExpiration;
        BmobNative.init(this.Z, this.I);
    }

    public static BmobWrapper getInstance() {
        BmobWrapper bmobWrapper;
        synchronized (Code) {
            bmobWrapper = V;
        }
        return bmobWrapper;
    }

    final File Code() {
        File Code;
        synchronized (Code) {
            if (this.F == null) {
                this.F = new File(this.Z.getCacheDir(), "cn.bmob");
            }
            Code = Code(this.F);
        }
        return Code;
    }

    final File V() {
        File Code;
        synchronized (Code) {
            if (this.D == null) {
                this.D = new File(this.Z.getFilesDir(), "cn.bmob");
            }
            Code = Code(this.D);
        }
        return Code;
    }

    private static File Code(File file) {
        return (file.exists() || !file.mkdirs()) ? file : file;
    }

    public long getConnectTimeout() {
        if (this.B == 0) {
            return (long) BmobConstants.CONNECT_TIMEOUT;
        }
        return this.B;
    }

    public int getUploadBlockSize() {
        if (this.C == 0) {
            return BmobConstants.BLOCK_SIZE;
        }
        return this.C;
    }

    public long getFileExpiration() {
        if (this.S == 0) {
            return BmobConstants.EXPIRATION;
        }
        return this.S;
    }

    public Context getApplicationContext() {
        return this.Z;
    }
}
