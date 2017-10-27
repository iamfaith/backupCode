package cn.bmob.v3.http.c;

import cn.bmob.v3.BmobConstants;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Func1;

/* compiled from: RetryWithDelay */
public final class This implements Func1<Observable<? extends Throwable>, Observable<?>> {
    private final int Code = 2;
    private int I;
    private final int V = BmobConstants.TIME_DELAY_RETRY;

    static /* synthetic */ int Code(This thisR) {
        int i = thisR.I + 1;
        thisR.I = i;
        return i;
    }

    public final /* synthetic */ Object call(Object obj) {
        return ((Observable) obj).flatMap(new Func1<Throwable, Observable<?>>(this) {
            private /* synthetic */ This Code;

            {
                this.Code = r1;
            }

            public final /* synthetic */ Object call(Object obj) {
                Throwable th = (Throwable) obj;
                if (This.Code(this.Code) <= this.Code.Code) {
                    return Observable.timer((long) this.Code.V, TimeUnit.MILLISECONDS);
                }
                return Observable.error(th);
            }
        });
    }

    public This(int i, int i2) {
    }
}
