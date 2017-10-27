package rx.internal.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.CompositeException;
import rx.functions.Action0;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.atomic.SpscLinkedArrayQueue;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public final class OperatorSwitch<T> implements Operator<T, Observable<? extends T>> {
    final boolean delayError;

    private static final class Holder {
        static final OperatorSwitch<Object> INSTANCE = new OperatorSwitch(false);

        private Holder() {
        }
    }

    private static final class HolderDelayError {
        static final OperatorSwitch<Object> INSTANCE = new OperatorSwitch(true);

        private HolderDelayError() {
        }
    }

    static final class InnerSubscriber<T> extends Subscriber<T> {
        private final long id;
        private final SwitchSubscriber<T> parent;

        InnerSubscriber(long id, SwitchSubscriber<T> parent) {
            this.id = id;
            this.parent = parent;
        }

        public void setProducer(Producer p) {
            this.parent.innerProducer(p, this.id);
        }

        public void onNext(T t) {
            this.parent.emit(t, this);
        }

        public void onError(Throwable e) {
            this.parent.error(e, this.id);
        }

        public void onCompleted() {
            this.parent.complete(this.id);
        }
    }

    private static final class SwitchSubscriber<T> extends Subscriber<Observable<? extends T>> {
        static final Throwable TERMINAL_ERROR = new Throwable("Terminal error");
        final Subscriber<? super T> child;
        final boolean delayError;
        boolean emitting;
        Throwable error;
        final AtomicLong index;
        boolean innerActive;
        volatile boolean mainDone;
        boolean missed;
        final NotificationLite<T> nl;
        Producer producer;
        final SpscLinkedArrayQueue<Object> queue;
        long requested;
        final SerialSubscription ssub = new SerialSubscription();

        SwitchSubscriber(Subscriber<? super T> child, boolean delayError) {
            this.child = child;
            this.delayError = delayError;
            this.index = new AtomicLong();
            this.queue = new SpscLinkedArrayQueue(RxRingBuffer.SIZE);
            this.nl = NotificationLite.instance();
        }

        void init() {
            this.child.add(this.ssub);
            this.child.add(Subscriptions.create(new Action0() {
                public void call() {
                    SwitchSubscriber.this.clearProducer();
                }
            }));
            this.child.setProducer(new Producer() {
                public void request(long n) {
                    if (n > 0) {
                        SwitchSubscriber.this.childRequested(n);
                    } else if (n < 0) {
                        throw new IllegalArgumentException("n >= 0 expected but it was " + n);
                    }
                }
            });
        }

        void clearProducer() {
            synchronized (this) {
                this.producer = null;
            }
        }

        public void onNext(Observable<? extends T> t) {
            InnerSubscriber<T> inner;
            long id = this.index.incrementAndGet();
            Subscription s = this.ssub.get();
            if (s != null) {
                s.unsubscribe();
            }
            synchronized (this) {
                inner = new InnerSubscriber(id, this);
                this.innerActive = true;
                this.producer = null;
            }
            this.ssub.set(inner);
            t.unsafeSubscribe(inner);
        }

        public void onError(Throwable e) {
            synchronized (this) {
                boolean success = updateError(e);
            }
            if (success) {
                this.mainDone = true;
                drain();
                return;
            }
            pluginError(e);
        }

        boolean updateError(Throwable next) {
            Throwable e = this.error;
            if (e == TERMINAL_ERROR) {
                return false;
            }
            if (e == null) {
                this.error = next;
            } else if (e instanceof CompositeException) {
                Collection list = new ArrayList(((CompositeException) e).getExceptions());
                list.add(next);
                this.error = new CompositeException(list);
            } else {
                this.error = new CompositeException(e, next);
            }
            return true;
        }

        public void onCompleted() {
            this.mainDone = true;
            drain();
        }

        void emit(T value, InnerSubscriber<T> inner) {
            synchronized (this) {
                if (this.index.get() != inner.id) {
                    return;
                }
                this.queue.offer(inner, this.nl.next(value));
                drain();
            }
        }

        void error(Throwable e, long id) {
            boolean success;
            synchronized (this) {
                if (this.index.get() == id) {
                    success = updateError(e);
                    this.innerActive = false;
                    this.producer = null;
                } else {
                    success = true;
                }
            }
            if (success) {
                drain();
            } else {
                pluginError(e);
            }
        }

        void complete(long id) {
            synchronized (this) {
                if (this.index.get() != id) {
                    return;
                }
                this.innerActive = false;
                this.producer = null;
                drain();
            }
        }

        void pluginError(Throwable e) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
        }

        void innerProducer(Producer p, long id) {
            synchronized (this) {
                if (this.index.get() != id) {
                    return;
                }
                long n = this.requested;
                this.producer = p;
                p.request(n);
            }
        }

        void childRequested(long n) {
            synchronized (this) {
                Producer p = this.producer;
                this.requested = BackpressureUtils.addCap(this.requested, n);
            }
            if (p != null) {
                p.request(n);
            }
            drain();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void drain() {
            boolean localMainDone = this.mainDone;
            synchronized (this) {
                if (this.emitting) {
                    this.missed = true;
                    return;
                }
                this.emitting = true;
                boolean localInnerActive = this.innerActive;
                long localRequested = this.requested;
                Throwable localError = this.error;
                if (!(localError == null || localError == TERMINAL_ERROR || this.delayError)) {
                    this.error = TERMINAL_ERROR;
                }
            }
        }

        protected boolean checkTerminated(boolean localMainDone, boolean localInnerActive, Throwable localError, SpscLinkedArrayQueue<Object> localQueue, Subscriber<? super T> localChild, boolean empty) {
            if (this.delayError) {
                if (localMainDone && !localInnerActive && empty) {
                    if (localError != null) {
                        localChild.onError(localError);
                        return true;
                    }
                    localChild.onCompleted();
                    return true;
                }
            } else if (localError != null) {
                localQueue.clear();
                localChild.onError(localError);
                return true;
            } else if (localMainDone && !localInnerActive && empty) {
                localChild.onCompleted();
                return true;
            }
            return false;
        }
    }

    public static <T> OperatorSwitch<T> instance(boolean delayError) {
        if (delayError) {
            return HolderDelayError.INSTANCE;
        }
        return Holder.INSTANCE;
    }

    OperatorSwitch(boolean delayError) {
        this.delayError = delayError;
    }

    public Subscriber<? super Observable<? extends T>> call(Subscriber<? super T> child) {
        SwitchSubscriber<T> sws = new SwitchSubscriber(child, this.delayError);
        child.add(sws);
        sws.init();
        return sws;
    }
}
