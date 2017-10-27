package cn.bmob.v3.datatype.a;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.helper.ErrorCode;
import cn.bmob.v3.listener.UploadFileListener;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressLint({"NewApi"})
/* compiled from: BmobUploader */
public abstract class of {
    private static final int B;
    private static final int C;
    private static ThreadPoolExecutor F = new ThreadPoolExecutor(C, S, 1, TimeUnit.SECONDS, new LinkedBlockingQueue());
    private static final int S = ((B << 1) + 1);
    protected final BmobFile Code;
    private volatile boolean I = false;
    protected UploadFileListener V;
    private volatile Future Z;

    abstract void Code();

    static {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        B = availableProcessors;
        C = availableProcessors + 1;
        if (VERSION.SDK_INT >= 9) {
            F.allowCoreThreadTimeOut(true);
        }
    }

    protected of(BmobFile bmobFile, UploadFileListener uploadFileListener) {
        this.Code = bmobFile;
        this.V = uploadFileListener;
        this.I = false;
    }

    public final void V() {
        this.Z = F.submit(new Runnable(this) {
            private /* synthetic */ of Code;

            {
                this.Code = r1;
            }

            public final void run() {
                this.Code.Code();
            }
        });
    }

    public final boolean Code(boolean z) {
        if (this.I) {
            return false;
        }
        this.I = true;
        if (this.Z != null) {
            this.Z.cancel(true);
        }
        if (this.V == null) {
            return true;
        }
        this.V.done(new BmobException((int) ErrorCode.E9015, "your uploading task is canceled."));
        return true;
    }
}
