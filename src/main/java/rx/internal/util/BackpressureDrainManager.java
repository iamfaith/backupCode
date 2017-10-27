package rx.internal.util;

import java.util.concurrent.atomic.AtomicLong;
import rx.Producer;
import rx.annotations.Experimental;

@Experimental
public final class BackpressureDrainManager extends AtomicLong implements Producer {
    private static final long serialVersionUID = 2826241102729529449L;
    protected final BackpressureQueueCallback actual;
    protected boolean emitting;
    protected Throwable exception;
    protected volatile boolean terminated;

    public interface BackpressureQueueCallback {
        boolean accept(Object obj);

        void complete(Throwable th);

        Object peek();

        Object poll();
    }

    public BackpressureDrainManager(BackpressureQueueCallback actual) {
        this.actual = actual;
    }

    public final boolean isTerminated() {
        return this.terminated;
    }

    public final void terminate() {
        this.terminated = true;
    }

    public final void terminate(Throwable error) {
        if (!this.terminated) {
            this.exception = error;
            this.terminated = true;
        }
    }

    public final void terminateAndDrain() {
        this.terminated = true;
        drain();
    }

    public final void terminateAndDrain(Throwable error) {
        if (!this.terminated) {
            this.exception = error;
            this.terminated = true;
            drain();
        }
    }

    public final void request(long n) {
        if (n != 0) {
            boolean mayDrain;
            long r;
            long u;
            do {
                r = get();
                mayDrain = r == 0;
                if (r == Long.MAX_VALUE) {
                    break;
                } else if (n == Long.MAX_VALUE) {
                    u = n;
                    mayDrain = true;
                } else if (r > Long.MAX_VALUE - n) {
                    u = Long.MAX_VALUE;
                } else {
                    u = r + n;
                }
            } while (!compareAndSet(r, u));
            if (mayDrain) {
                drain();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void drain() {
        synchronized (this) {
            if (this.emitting) {
            } else {
                this.emitting = true;
                boolean term = this.terminated;
            }
        }
    }
}
