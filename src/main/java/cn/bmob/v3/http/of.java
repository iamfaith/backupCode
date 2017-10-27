package cn.bmob.v3.http;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import cn.bmob.v3.a.b.This;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.helper.ErrorCode;
import cn.bmob.v3.listener.DownloadFileListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Response;

/* compiled from: BmobFileDownloader */
public final class of extends AsyncTask<Void, Long, BmobException> {
    private File B;
    private Context C;
    private WakeLock Code;
    private DownloadFileListener I;
    private This V = new This();
    private String Z;

    protected final /* synthetic */ Object doInBackground(Object[] objArr) {
        return Code();
    }

    protected final /* synthetic */ void onPostExecute(Object obj) {
        BmobException bmobException = (BmobException) obj;
        super.onPostExecute(bmobException);
        this.Code.release();
        if (bmobException == null) {
            if (this.I != null) {
                this.I.done(this.B.getAbsolutePath(), null);
            }
        } else if (this.I != null) {
            this.I.done(null, new BmobException(bmobException.getErrorCode(), bmobException.getMessage()));
        }
        if (this.I != null) {
            this.I.onFinish();
        }
    }

    protected final /* synthetic */ void onProgressUpdate(Object[] objArr) {
        Long[] lArr = (Long[]) objArr;
        super.onProgressUpdate(lArr);
        if (this.I != null && lArr != null && lArr.length >= 2) {
            long longValue = lArr[0].longValue();
            long longValue2 = lArr[1].longValue();
            this.I.onProgress(Integer.valueOf((int) ((((float) longValue) * 100.0f) / ((float) longValue2))), longValue2);
        }
    }

    public of(Context context, String str, File file, DownloadFileListener downloadFileListener) {
        this.C = context;
        this.Z = str;
        this.I = downloadFileListener;
        this.B = file;
        cn.bmob.v3.datatype.a.This.V(file.getParentFile());
        if (file.exists()) {
            file.delete();
        }
    }

    protected final void onPreExecute() {
        super.onPreExecute();
        this.Code = ((PowerManager) this.C.getSystemService("power")).newWakeLock(1, getClass().getName());
        this.Code.acquire();
        if (this.I != null) {
            this.I.internalStart();
        }
    }

    private BmobException Code() {
        try {
            Response V = this.V.V(this.Z);
            long contentLength = V.body().contentLength();
            Code(V);
            return contentLength == this.B.length() ? null : null;
        } catch (IOException e) {
            IOException iOException = e;
            iOException.printStackTrace();
            return new BmobException((int) ErrorCode.E9015, iOException.getMessage());
        } catch (BmobException e2) {
            e2.printStackTrace();
            return e2;
        }
    }

    private String Code(Response response) throws IOException {
        FileOutputStream fileOutputStream;
        Throwable th;
        InputStream inputStream = null;
        byte[] bArr = new byte[1024];
        try {
            InputStream byteStream = response.body().byteStream();
            try {
                long contentLength = response.body().contentLength();
                long j = 0;
                cn.bmob.v3.datatype.a.This.V(this.B.getParentFile());
                fileOutputStream = new FileOutputStream(this.B);
                while (true) {
                    try {
                        int read = byteStream.read(bArr);
                        if (read == -1) {
                            break;
                        }
                        j += (long) read;
                        fileOutputStream.write(bArr, 0, read);
                        if (this.I != null) {
                            publishProgress(new Long[]{Long.valueOf(j), Long.valueOf(contentLength)});
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        inputStream = byteStream;
                    }
                }
                fileOutputStream.flush();
                String absolutePath = this.B.getAbsolutePath();
                if (byteStream != null) {
                    try {
                        byteStream.close();
                    } catch (IOException e) {
                    }
                }
                try {
                    fileOutputStream.close();
                } catch (IOException e2) {
                }
                return absolutePath;
            } catch (Throwable th3) {
                th = th3;
                fileOutputStream = null;
                inputStream = byteStream;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e3) {
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            fileOutputStream = null;
            if (inputStream != null) {
                inputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            throw th;
        }
    }
}
