package cn.bmob.v3.datatype;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConstants;
import cn.bmob.v3.b.From;
import cn.bmob.v3.b.The;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.helper.ErrorCode;
import cn.bmob.v3.http.I;
import cn.bmob.v3.http.This;
import cn.bmob.v3.http.acknowledge;
import cn.bmob.v3.http.bean.Upyun;
import cn.bmob.v3.http.of;
import cn.bmob.v3.http.thing;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.BmobErrorCallback;
import cn.bmob.v3.listener.DeleteBatchListener;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.ProgressCallback;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpHost;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

public class BmobFile implements Serializable {
    private static final long serialVersionUID = -9145726747813570773L;
    private String __type = "File";
    private transient Context context = Bmob.getApplicationContext();
    private transient of downloader;
    private String filename = null;
    private String group = null;
    private transient File localFile;
    private transient cn.bmob.v3.datatype.a.of uploader;
    protected String url = null;

    public BmobFile(File file) {
        this.localFile = file;
    }

    public BmobFile(String fileName, String group, String url) {
        this.filename = fileName;
        this.group = group;
        this.url = url;
    }

    public void obtain(String fileName, String group, String url) {
        this.context = Bmob.getApplicationContext();
        this.filename = fileName;
        this.group = group;
        this.url = url;
    }

    public static BmobFile createEmptyFile() {
        BmobFile bmobFile = new BmobFile(new File(""));
        bmobFile.setFilename("test.apk");
        return bmobFile;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGroup() {
        return this.group;
    }

    protected void setGroup(String group) {
        this.group = group;
    }

    public String getFilename() {
        return this.filename;
    }

    protected void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileUrl() {
        if (this.url.startsWith(HttpHost.DEFAULT_SCHEME_NAME)) {
            return this.url;
        }
        return new The().V("file", "http://file.bmob.cn") + "/" + this.url;
    }

    public File getLocalFile() {
        return this.localFile;
    }

    public Subscription upload(UploadFileListener listener) {
        return uploadblock(listener);
    }

    public Observable<Void> uploadObservable(final ProgressCallback listener) {
        if (Code(null) && Code(this.localFile, null)) {
            return Observable.create(new OnSubscribe<String>(this) {
                public final /* synthetic */ void call(Object obj) {
                    Subscriber subscriber = (Subscriber) obj;
                    The the = new The();
                    int V = the.V("upyunVer", -1);
                    CharSequence V2 = the.V(BmobConstants.TYPE_CDN, "");
                    if (V == -1 || V < This.V || TextUtils.isEmpty(V2)) {
                        subscriber.onNext(null);
                    } else {
                        subscriber.onNext(V2);
                    }
                }
            }).concatMap(new Func1<String, Observable<? extends Void>>(this) {
                final /* synthetic */ BmobFile V;

                public final /* synthetic */ Object call(Object obj) {
                    String str = (String) obj;
                    if (TextUtils.isEmpty(str)) {
                        return thing.Code().V().Code().concatMap(new Func1<Upyun, Observable<? extends Void>>(this) {
                            private /* synthetic */ AnonymousClass2 Code;

                            {
                                this.Code = r1;
                            }

                            public final /* synthetic */ Object call(Object obj) {
                                return Observable.create(new OnSubscribe<Void>(this.Code.V, (Upyun) obj, listener) {
                                    final /* synthetic */ BmobFile V;

                                    public final /* synthetic */ void call(Object obj) {
                                        final Subscriber subscriber = (Subscriber) obj;
                                        if (subscriber.isUnsubscribed()) {
                                            cn.bmob.v3.b.This.V("uploadObservable:subcriber is cancel");
                                        } else {
                                            BmobFile.Code(this.V, r3, new UploadFileListener(this) {
                                                private /* synthetic */ AnonymousClass5 V;

                                                public final void done(BmobException e) {
                                                    if (e == null) {
                                                        subscriber.onNext(null);
                                                    } else {
                                                        subscriber.onError(e);
                                                    }
                                                }

                                                public final void onProgress(Integer value) {
                                                    super.onProgress(value);
                                                    if (r4 != null) {
                                                        r4.onProgress(value, this.V.V.getLocalFile().length());
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }).concatMap(new Func1<Void, Observable<? extends Void>>(this.Code.V) {
                                    private /* synthetic */ BmobFile Code;

                                    {
                                        this.Code = r1;
                                    }

                                    public final /* synthetic */ Object call(Object obj) {
                                        return thing.Code().Code(this.Code.getFileUrl(), this.Code.getFilename(), this.Code.getLocalFile().length());
                                    }
                                });
                            }
                        });
                    }
                    return Observable.create(/* anonymous class already generated */).concatMap(/* anonymous class already generated */);
                }
            });
        }
        return null;
    }

    public void cancel() {
        Code();
        V();
    }

    private void Code() {
        if (this.uploader != null) {
            this.uploader.Code(true);
        }
    }

    private void V() {
        if (this.downloader != null) {
            this.downloader.cancel(true);
        }
    }

    public Subscription delete() {
        return delete(null);
    }

    public Subscription delete(UpdateListener listener) {
        String url = getUrl();
        thing.Code();
        return thing.Code(thing.Code((Object) url, "url can't be empty"), url, listener).V();
    }

    public Observable<Void> deleteObservable() {
        String url = getUrl();
        thing.Code();
        return thing.Code(thing.Code((Object) url, "url can't be empty"), url, null).Code();
    }

    public static Observable<BmobReturn<String[]>> deleteBatchObservable(String[] urls) {
        thing.Code();
        return thing.Code(urls, null).Code();
    }

    public static Subscription deleteBatch(String[] urls, DeleteBatchListener listener) {
        thing.Code();
        return thing.Code(urls, listener).V();
    }

    private boolean Code(BmobErrorCallback bmobErrorCallback) {
        if (this.context == null && bmobErrorCallback != null) {
            bmobErrorCallback.doneError(ErrorCode.E9012, ErrorCode.E9012S);
            return false;
        } else if (TextUtils.isEmpty(I.V())) {
            if (bmobErrorCallback != null) {
                bmobErrorCallback.doneError(ErrorCode.E9001, ErrorCode.E9001S);
                return false;
            }
            Log.e(BmobConstants.TAG, ErrorCode.E9001S);
            return false;
        } else if (From.V(this.context)) {
            if (cn.bmob.v3.datatype.a.This.Code(this.context, "android.permission.WAKE_LOCK")) {
                return true;
            }
            if (bmobErrorCallback != null) {
                bmobErrorCallback.doneError(ErrorCode.E9021, ErrorCode.E9021S);
                return false;
            }
            Log.e(BmobConstants.TAG, ErrorCode.E9021S);
            return false;
        } else if (bmobErrorCallback != null) {
            bmobErrorCallback.doneError(ErrorCode.E9016, ErrorCode.E9016S);
            return false;
        } else {
            Log.e(BmobConstants.TAG, ErrorCode.E9016S);
            return false;
        }
    }

    private static boolean Code(File file, BmobErrorCallback bmobErrorCallback) {
        if (file == null || !file.exists()) {
            if (bmobErrorCallback != null) {
                bmobErrorCallback.doneError(ErrorCode.E9008, "the file does not exist.");
                return false;
            }
            Log.e(BmobConstants.TAG, "the file does not exist(9008)");
            return false;
        } else if (file.length() == 0) {
            if (bmobErrorCallback != null) {
                bmobErrorCallback.doneError(ErrorCode.E9007, "the file length must be greater than zero.");
                return false;
            }
            Log.e(BmobConstants.TAG, "the file length must be greater than zero(9007)");
            return false;
        } else if (file.getAbsolutePath().contains(".")) {
            return true;
        } else {
            if (bmobErrorCallback != null) {
                bmobErrorCallback.doneError(ErrorCode.E9007, "the file must have suffix.");
                return false;
            }
            Log.e(BmobConstants.TAG, "the file must have suffix(9007)");
            return false;
        }
    }

    public void download(DownloadFileListener listener) {
        if (this.context != null) {
            download(new File(this.context.getApplicationContext().getCacheDir() + "/bmob/", getFilename()), listener);
        } else if (listener != null) {
            listener.doneError(ErrorCode.E9012, "context is null.");
        }
    }

    public void download(File savePath, DownloadFileListener listener) {
        if (Code(listener)) {
            Object fileUrl = getFileUrl();
            if (TextUtils.isEmpty(fileUrl)) {
                if (listener != null) {
                    listener.doneError(ErrorCode.E9018, "fileUrl can't be empty");
                }
            } else if (savePath != null) {
                V();
                this.downloader = new of(this.context, fileUrl, savePath, listener);
                this.downloader.execute(new Void[0]);
            } else if (listener != null) {
                listener.doneError(ErrorCode.E9018, "savePath must not be null");
            }
        }
    }

    public Observable<String> downloadObservable(ProgressCallback listener) {
        return downloadObservable(new File(this.context.getApplicationContext().getCacheDir() + "/bmob/", getFilename()), listener);
    }

    public Observable<String> downloadObservable(final File savePath, final ProgressCallback listener) {
        return Observable.create(new OnSubscribe<String>(this) {
            private /* synthetic */ BmobFile I;

            public final /* synthetic */ void call(Object obj) {
                final Subscriber subscriber = (Subscriber) obj;
                if (subscriber.isUnsubscribed()) {
                    cn.bmob.v3.b.This.V("downloadObservable:subcriber is cancel");
                } else {
                    this.I.download(savePath, new DownloadFileListener(this) {
                        private /* synthetic */ AnonymousClass6 V;

                        public final void done(String s, BmobException e) {
                            if (e == null) {
                                subscriber.onNext(s);
                            } else {
                                subscriber.onError(e);
                            }
                            subscriber.onCompleted();
                        }

                        public final void onProgress(Integer value, long total) {
                            if (listener != null) {
                                listener.onProgress(value, total);
                            }
                        }
                    });
                }
            }
        });
    }

    public static void uploadBatch(String[] filePaths, UploadBatchListener listener) {
        if (filePaths != null && filePaths.length != 0) {
            V(0, filePaths, new ArrayList(), new ArrayList(), listener);
        } else if (listener != null) {
            listener.onError(ErrorCode.E9008, "the files does not exist.");
        }
    }

    private static void V(int i, String[] strArr, List<BmobFile> list, List<String> list2, UploadBatchListener uploadBatchListener) {
        int length = strArr.length;
        if (i + 1 < length) {
            Code(false, length, i, strArr, list, list2, uploadBatchListener);
        } else {
            Code(true, length, i, strArr, list, list2, uploadBatchListener);
        }
    }

    private static void Code(boolean z, int i, int i2, String[] strArr, List<BmobFile> list, List<String> list2, UploadBatchListener uploadBatchListener) {
        final BmobFile bmobFile = new BmobFile(new File(strArr[i2]));
        final List<BmobFile> list3 = list;
        final List<String> list4 = list2;
        final UploadBatchListener uploadBatchListener2 = uploadBatchListener;
        final boolean z2 = z;
        final int i3 = i2;
        final String[] strArr2 = strArr;
        final int i4 = i;
        bmobFile.uploadblock(new UploadFileListener() {
            public final void done(BmobException e) {
                if (e == null) {
                    String fileUrl = bmobFile.getFileUrl();
                    list3.add(bmobFile);
                    list4.add(fileUrl);
                    uploadBatchListener2.onSuccess(list3, list4);
                    if (!z2) {
                        BmobFile.V(i3 + 1, strArr2, list3, list4, uploadBatchListener2);
                        return;
                    }
                    return;
                }
                uploadBatchListener2.onError(e.getErrorCode(), e.getMessage());
            }

            public final void onProgress(Integer percent) {
                UploadBatchListener uploadBatchListener = uploadBatchListener2;
                int i = i3 + 1;
                int intValue = percent.intValue();
                int i2 = i4;
                int i3 = i3 + 1;
                int i4 = i4;
                int i5 = 100;
                if (i3 < i4) {
                    i5 = (int) ((((double) i3) / ((double) i4)) * 100.0d);
                }
                uploadBatchListener.onProgress(i, intValue, i2, i5);
            }
        });
    }

    public Subscription uploadblock(final UploadFileListener listener) {
        if (!Code(listener) || !Code(this.localFile, listener)) {
            return null;
        }
        if (listener != null) {
            listener.internalStart();
        }
        return new acknowledge.This().Code(uploadObservable(new ProgressCallback(this) {
            public final void onProgress(Integer value, long j) {
                if (listener != null) {
                    listener.onProgress(value);
                }
            }
        })).V((BmobCallback) listener).C().V();
    }

    static /* synthetic */ void Code(BmobFile bmobFile, Upyun upyun, UploadFileListener uploadFileListener) {
        bmobFile.Code();
        bmobFile.uploader = new cn.bmob.v3.datatype.a.From(bmobFile.context, upyun, bmobFile, uploadFileListener);
        bmobFile.uploader.V();
    }
}
