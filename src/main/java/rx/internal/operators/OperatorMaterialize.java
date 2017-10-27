package rx.internal.operators;

import java.util.concurrent.atomic.AtomicLong;
import rx.Notification;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.plugins.RxJavaPlugins;

public final class OperatorMaterialize<T> implements Operator<Notification<T>, T> {

    private static final class Holder {
        static final OperatorMaterialize<Object> INSTANCE = new OperatorMaterialize();

        private Holder() {
        }
    }

    private static class ParentSubscriber<T> extends Subscriber<T> {
        private boolean busy = false;
        private final Subscriber<? super Notification<T>> child;
        private boolean missed = false;
        private final AtomicLong requested = new AtomicLong();
        private volatile Notification<T> terminalNotification;

        ParentSubscriber(Subscriber<? super Notification<T>> child) {
            this.child = child;
        }

        public void onStart() {
            request(0);
        }

        void requestMore(long n) {
            BackpressureUtils.getAndAddRequest(this.requested, n);
            request(n);
            drain();
        }

        public void onCompleted() {
            this.terminalNotification = Notification.createOnCompleted();
            drain();
        }

        public void onError(Throwable e) {
            this.terminalNotification = Notification.createOnError(e);
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            drain();
        }

        public void onNext(T t) {
            this.child.onNext(Notification.createOnNext(t));
            decrementRequested();
        }

        private void decrementRequested() {
            AtomicLong localRequested = this.requested;
            long r;
            do {
                r = localRequested.get();
                if (r == Long.MAX_VALUE) {
                    return;
                }
            } while (!localRequested.compareAndSet(r, r - 1));
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void drain() {
            synchronized (this) {
                if (this.busy) {
                    this.missed = true;
                }
            }
        }
    }

    public static <T> OperatorMaterialize<T> instance() {
        return Holder.INSTANCE;
    }

    OperatorMaterialize() {
    }

    public Subscriber<? super T> call(Subscriber<? super Notification<T>> child) {
        final ParentSubscriber<T> parent = new ParentSubscriber(child);
        child.add(parent);
        child.setProducer(new Producer() {
            public void request(long n) {
                if (n > 0) {
                    parent.requestMore(n);
                }
            }
        });
        return parent;
    }
}
