package rx.subjects;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.annotations.Experimental;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.internal.operators.BackpressureUtils;
import rx.internal.operators.NotificationLite;
import rx.internal.util.atomic.SpscLinkedAtomicQueue;
import rx.internal.util.atomic.SpscUnboundedAtomicArrayQueue;
import rx.internal.util.unsafe.SpscLinkedQueue;
import rx.internal.util.unsafe.SpscUnboundedArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;

@Experimental
public final class UnicastSubject<T> extends Subject<T, T> {
    final State<T> state;

    static final class State<T> extends AtomicLong implements Producer, Observer<T>, OnSubscribe<T>, Subscription {
        private static final long serialVersionUID = -9044104859202255786L;
        volatile boolean caughtUp;
        volatile boolean done;
        boolean emitting;
        Throwable error;
        boolean missed;
        final NotificationLite<T> nl = NotificationLite.instance();
        final Queue<Object> queue;
        final AtomicReference<Subscriber<? super T>> subscriber = new AtomicReference();
        final AtomicReference<Action0> terminateOnce;

        public State(int capacityHint, Action0 onTerminated) {
            this.terminateOnce = onTerminated != null ? new AtomicReference(onTerminated) : null;
            Queue<Object> q = capacityHint > 1 ? UnsafeAccess.isUnsafeAvailable() ? new SpscUnboundedArrayQueue(capacityHint) : new SpscUnboundedAtomicArrayQueue(capacityHint) : UnsafeAccess.isUnsafeAvailable() ? new SpscLinkedQueue() : new SpscLinkedAtomicQueue();
            this.queue = q;
        }

        public void onNext(T t) {
            if (!this.done) {
                if (!this.caughtUp) {
                    boolean stillReplay = false;
                    synchronized (this) {
                        if (!this.caughtUp) {
                            this.queue.offer(this.nl.next(t));
                            stillReplay = true;
                        }
                    }
                    if (stillReplay) {
                        replay();
                        return;
                    }
                }
                Subscriber<? super T> s = (Subscriber) this.subscriber.get();
                try {
                    s.onNext(t);
                } catch (Throwable ex) {
                    Exceptions.throwOrReport(ex, s, t);
                }
            }
        }

        public void onError(Throwable e) {
            if (!this.done) {
                doTerminate();
                this.error = e;
                this.done = true;
                if (!this.caughtUp) {
                    boolean stillReplay;
                    synchronized (this) {
                        stillReplay = !this.caughtUp;
                    }
                    if (stillReplay) {
                        replay();
                        return;
                    }
                }
                ((Subscriber) this.subscriber.get()).onError(e);
            }
        }

        public void onCompleted() {
            if (!this.done) {
                doTerminate();
                this.done = true;
                if (!this.caughtUp) {
                    boolean stillReplay;
                    synchronized (this) {
                        stillReplay = !this.caughtUp;
                    }
                    if (stillReplay) {
                        replay();
                        return;
                    }
                }
                ((Subscriber) this.subscriber.get()).onCompleted();
            }
        }

        public void request(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n >= 0 required");
            } else if (n > 0) {
                BackpressureUtils.getAndAddRequest(this, n);
                replay();
            } else if (this.done) {
                replay();
            }
        }

        public void call(Subscriber<? super T> subscriber) {
            if (this.subscriber.compareAndSet(null, subscriber)) {
                subscriber.add(this);
                subscriber.setProducer(this);
                return;
            }
            subscriber.onError(new IllegalStateException("Only a single subscriber is allowed"));
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void replay() {
            synchronized (this) {
                if (this.emitting) {
                    this.missed = true;
                    return;
                }
                this.emitting = true;
            }
        }

        public void unsubscribe() {
            doTerminate();
            this.done = true;
            synchronized (this) {
                if (this.emitting) {
                    return;
                }
                this.emitting = true;
                this.queue.clear();
            }
        }

        public boolean isUnsubscribed() {
            return this.done;
        }

        boolean checkTerminated(boolean done, boolean empty, Subscriber<? super T> s) {
            if (s.isUnsubscribed()) {
                this.queue.clear();
                return true;
            }
            if (done) {
                Throwable e = this.error;
                if (e != null) {
                    this.queue.clear();
                    s.onError(e);
                    return true;
                } else if (empty) {
                    s.onCompleted();
                    return true;
                }
            }
            return false;
        }

        void doTerminate() {
            AtomicReference<Action0> ref = this.terminateOnce;
            if (ref != null) {
                Action0 a = (Action0) ref.get();
                if (a != null && ref.compareAndSet(a, null)) {
                    a.call();
                }
            }
        }
    }

    public static <T> UnicastSubject<T> create() {
        return create(16);
    }

    public static <T> UnicastSubject<T> create(int capacityHint) {
        return new UnicastSubject(new State(capacityHint, null));
    }

    public static <T> UnicastSubject<T> create(int capacityHint, Action0 onTerminated) {
        return new UnicastSubject(new State(capacityHint, onTerminated));
    }

    private UnicastSubject(State<T> state) {
        super(state);
        this.state = state;
    }

    public void onNext(T t) {
        this.state.onNext(t);
    }

    public void onError(Throwable e) {
        this.state.onError(e);
    }

    public void onCompleted() {
        this.state.onCompleted();
    }

    public boolean hasObservers() {
        return this.state.subscriber.get() != null;
    }
}
