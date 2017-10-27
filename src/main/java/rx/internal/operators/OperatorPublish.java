package rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.MissingBackpressureException;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.SynchronizedQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.observables.ConnectableObservable;
import rx.subscriptions.Subscriptions;

public final class OperatorPublish<T> extends ConnectableObservable<T> {
    final AtomicReference<PublishSubscriber<T>> current;
    final Observable<? extends T> source;

    static final class InnerProducer<T> extends AtomicLong implements Producer, Subscription {
        static final long NOT_REQUESTED = -4611686018427387904L;
        static final long UNSUBSCRIBED = Long.MIN_VALUE;
        private static final long serialVersionUID = -4453897557930727610L;
        final Subscriber<? super T> child;
        final PublishSubscriber<T> parent;

        public InnerProducer(PublishSubscriber<T> parent, Subscriber<? super T> child) {
            this.parent = parent;
            this.child = child;
            lazySet(NOT_REQUESTED);
        }

        public void request(long n) {
            if (n >= 0) {
                long r;
                long u;
                do {
                    r = get();
                    if (r == UNSUBSCRIBED) {
                        return;
                    }
                    if (r >= 0 && n == 0) {
                        return;
                    }
                    if (r == NOT_REQUESTED) {
                        u = n;
                    } else {
                        u = r + n;
                        if (u < 0) {
                            u = Long.MAX_VALUE;
                        }
                    }
                } while (!compareAndSet(r, u));
                this.parent.dispatch();
            }
        }

        public long produced(long n) {
            if (n <= 0) {
                throw new IllegalArgumentException("Cant produce zero or less");
            }
            long u;
            long r;
            do {
                r = get();
                if (r == NOT_REQUESTED) {
                    throw new IllegalStateException("Produced without request");
                } else if (r == UNSUBSCRIBED) {
                    return UNSUBSCRIBED;
                } else {
                    u = r - n;
                    if (u < 0) {
                        throw new IllegalStateException("More produced (" + n + ") than requested (" + r + ")");
                    }
                }
            } while (!compareAndSet(r, u));
            return u;
        }

        public boolean isUnsubscribed() {
            return get() == UNSUBSCRIBED;
        }

        public void unsubscribe() {
            if (get() != UNSUBSCRIBED && getAndSet(UNSUBSCRIBED) != UNSUBSCRIBED) {
                this.parent.remove(this);
                this.parent.dispatch();
            }
        }
    }

    static final class PublishSubscriber<T> extends Subscriber<T> implements Subscription {
        static final InnerProducer[] EMPTY = new InnerProducer[0];
        static final InnerProducer[] TERMINATED = new InnerProducer[0];
        final AtomicReference<PublishSubscriber<T>> current;
        boolean emitting;
        boolean missed;
        final NotificationLite<T> nl;
        final AtomicReference<InnerProducer[]> producers;
        final Queue<Object> queue;
        final AtomicBoolean shouldConnect;
        volatile Object terminalEvent;

        public PublishSubscriber(AtomicReference<PublishSubscriber<T>> current) {
            this.queue = UnsafeAccess.isUnsafeAvailable() ? new SpscArrayQueue(RxRingBuffer.SIZE) : new SynchronizedQueue(RxRingBuffer.SIZE);
            this.nl = NotificationLite.instance();
            this.producers = new AtomicReference(EMPTY);
            this.current = current;
            this.shouldConnect = new AtomicBoolean();
        }

        void init() {
            add(Subscriptions.create(new Action0() {
                public void call() {
                    PublishSubscriber.this.producers.getAndSet(PublishSubscriber.TERMINATED);
                    PublishSubscriber.this.current.compareAndSet(PublishSubscriber.this, null);
                }
            }));
        }

        public void onStart() {
            request((long) RxRingBuffer.SIZE);
        }

        public void onNext(T t) {
            if (this.queue.offer(this.nl.next(t))) {
                dispatch();
            } else {
                onError(new MissingBackpressureException());
            }
        }

        public void onError(Throwable e) {
            if (this.terminalEvent == null) {
                this.terminalEvent = this.nl.error(e);
                dispatch();
            }
        }

        public void onCompleted() {
            if (this.terminalEvent == null) {
                this.terminalEvent = this.nl.completed();
                dispatch();
            }
        }

        boolean add(InnerProducer<T> producer) {
            if (producer == null) {
                throw new NullPointerException();
            }
            InnerProducer[] c;
            InnerProducer[] u;
            do {
                c = (InnerProducer[]) this.producers.get();
                if (c == TERMINATED) {
                    return false;
                }
                int len = c.length;
                u = new InnerProducer[(len + 1)];
                System.arraycopy(c, 0, u, 0, len);
                u[len] = producer;
            } while (!this.producers.compareAndSet(c, u));
            return true;
        }

        void remove(InnerProducer<T> producer) {
            InnerProducer[] c;
            InnerProducer[] u;
            do {
                c = (InnerProducer[]) this.producers.get();
                if (c != EMPTY && c != TERMINATED) {
                    int j = -1;
                    int len = c.length;
                    for (int i = 0; i < len; i++) {
                        if (c[i].equals(producer)) {
                            j = i;
                            break;
                        }
                    }
                    if (j < 0) {
                        return;
                    }
                    if (len == 1) {
                        u = EMPTY;
                    } else {
                        u = new InnerProducer[(len - 1)];
                        System.arraycopy(c, 0, u, 0, j);
                        System.arraycopy(c, j + 1, u, j, (len - j) - 1);
                    }
                } else {
                    return;
                }
            } while (!this.producers.compareAndSet(c, u));
        }

        boolean checkTerminated(Object term, boolean empty) {
            if (term != null) {
                if (!this.nl.isCompleted(term)) {
                    Throwable t = this.nl.getError(term);
                    this.current.compareAndSet(this, null);
                    try {
                        for (InnerProducer<?> ip : (InnerProducer[]) this.producers.getAndSet(TERMINATED)) {
                            ip.child.onError(t);
                        }
                        return true;
                    } finally {
                        unsubscribe();
                    }
                } else if (empty) {
                    this.current.compareAndSet(this, null);
                    try {
                        for (InnerProducer<?> ip2 : (InnerProducer[]) this.producers.getAndSet(TERMINATED)) {
                            ip2.child.onCompleted();
                        }
                        return true;
                    } finally {
                        unsubscribe();
                    }
                }
            }
            return false;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void dispatch() {
            synchronized (this) {
                if (this.emitting) {
                    this.missed = true;
                    return;
                }
                this.emitting = true;
                this.missed = false;
            }
            int i$++;
        }
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source) {
        final AtomicReference<PublishSubscriber<T>> curr = new AtomicReference();
        return new OperatorPublish(new OnSubscribe<T>() {
            public void call(Subscriber<? super T> child) {
                while (true) {
                    PublishSubscriber<T> r = (PublishSubscriber) curr.get();
                    if (r == null || r.isUnsubscribed()) {
                        PublishSubscriber<T> u = new PublishSubscriber(curr);
                        u.init();
                        if (curr.compareAndSet(r, u)) {
                            r = u;
                        } else {
                            continue;
                        }
                    }
                    InnerProducer<T> inner = new InnerProducer(r, child);
                    if (r.add(inner)) {
                        child.add(inner);
                        child.setProducer(inner);
                        return;
                    }
                }
            }
        }, source, curr);
    }

    public static <T, R> Observable<R> create(Observable<? extends T> source, Func1<? super Observable<T>, ? extends Observable<R>> selector) {
        return create(source, selector, false);
    }

    public static <T, R> Observable<R> create(final Observable<? extends T> source, final Func1<? super Observable<T>, ? extends Observable<R>> selector, final boolean delayError) {
        return Observable.create(new OnSubscribe<R>() {
            public void call(final Subscriber<? super R> child) {
                final OnSubscribe op = new OnSubscribePublishMulticast(RxRingBuffer.SIZE, delayError);
                Subscriber<R> subscriber = new Subscriber<R>() {
                    public void onNext(R t) {
                        child.onNext(t);
                    }

                    public void onError(Throwable e) {
                        op.unsubscribe();
                        child.onError(e);
                    }

                    public void onCompleted() {
                        op.unsubscribe();
                        child.onCompleted();
                    }

                    public void setProducer(Producer p) {
                        child.setProducer(p);
                    }
                };
                child.add(op);
                child.add(subscriber);
                ((Observable) selector.call(Observable.create(op))).unsafeSubscribe(subscriber);
                source.unsafeSubscribe(op.subscriber());
            }
        });
    }

    private OperatorPublish(OnSubscribe<T> onSubscribe, Observable<? extends T> source, AtomicReference<PublishSubscriber<T>> current) {
        super(onSubscribe);
        this.source = source;
        this.current = current;
    }

    public void connect(Action1<? super Subscription> connection) {
        PublishSubscriber<T> ps;
        PublishSubscriber<T> u;
        boolean doConnect;
        do {
            ps = (PublishSubscriber) this.current.get();
            if (ps != null && !ps.isUnsubscribed()) {
                break;
            }
            u = new PublishSubscriber(this.current);
            u.init();
        } while (!this.current.compareAndSet(ps, u));
        ps = u;
        if (ps.shouldConnect.get() || !ps.shouldConnect.compareAndSet(false, true)) {
            doConnect = false;
        } else {
            doConnect = true;
        }
        connection.call(ps);
        if (doConnect) {
            this.source.unsafeSubscribe(ps);
        }
    }
}
