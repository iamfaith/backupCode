package rx.internal.operators;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.internal.util.atomic.SpscAtomicArrayQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.subscriptions.Subscriptions;

public final class OperatorEagerConcatMap<T, R> implements Operator<R, T> {
    final int bufferSize;
    final Func1<? super T, ? extends Observable<? extends R>> mapper;
    private final int maxConcurrent;

    static final class EagerOuterProducer extends AtomicLong implements Producer {
        private static final long serialVersionUID = -657299606803478389L;
        final EagerOuterSubscriber<?, ?> parent;

        public EagerOuterProducer(EagerOuterSubscriber<?, ?> parent) {
            this.parent = parent;
        }

        public void request(long n) {
            if (n < 0) {
                throw new IllegalStateException("n >= 0 required but it was " + n);
            } else if (n > 0) {
                BackpressureUtils.getAndAddRequest(this, n);
                this.parent.drain();
            }
        }
    }

    static final class EagerInnerSubscriber<T> extends Subscriber<T> {
        volatile boolean done;
        Throwable error;
        final NotificationLite<T> nl;
        final EagerOuterSubscriber<?, T> parent;
        final Queue<Object> queue;

        public EagerInnerSubscriber(EagerOuterSubscriber<?, T> parent, int bufferSize) {
            Queue<Object> q;
            this.parent = parent;
            if (UnsafeAccess.isUnsafeAvailable()) {
                q = new SpscArrayQueue(bufferSize);
            } else {
                q = new SpscAtomicArrayQueue(bufferSize);
            }
            this.queue = q;
            this.nl = NotificationLite.instance();
            request((long) bufferSize);
        }

        public void onNext(T t) {
            this.queue.offer(this.nl.next(t));
            this.parent.drain();
        }

        public void onError(Throwable e) {
            this.error = e;
            this.done = true;
            this.parent.drain();
        }

        public void onCompleted() {
            this.done = true;
            this.parent.drain();
        }

        void requestMore(long n) {
            request(n);
        }
    }

    static final class EagerOuterSubscriber<T, R> extends Subscriber<T> {
        final Subscriber<? super R> actual;
        final int bufferSize;
        volatile boolean cancelled;
        volatile boolean done;
        Throwable error;
        final Func1<? super T, ? extends Observable<? extends R>> mapper;
        private EagerOuterProducer sharedProducer;
        final LinkedList<EagerInnerSubscriber<R>> subscribers = new LinkedList();
        final AtomicInteger wip = new AtomicInteger();

        public EagerOuterSubscriber(Func1<? super T, ? extends Observable<? extends R>> mapper, int bufferSize, int maxConcurrent, Subscriber<? super R> actual) {
            this.mapper = mapper;
            this.bufferSize = bufferSize;
            this.actual = actual;
            request(maxConcurrent == ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED ? Long.MAX_VALUE : (long) maxConcurrent);
        }

        void init() {
            this.sharedProducer = new EagerOuterProducer(this);
            add(Subscriptions.create(new Action0() {
                public void call() {
                    EagerOuterSubscriber.this.cancelled = true;
                    if (EagerOuterSubscriber.this.wip.getAndIncrement() == 0) {
                        EagerOuterSubscriber.this.cleanup();
                    }
                }
            }));
            this.actual.add(this);
            this.actual.setProducer(this.sharedProducer);
        }

        void cleanup() {
            synchronized (this.subscribers) {
                List<Subscription> list = new ArrayList(this.subscribers);
                this.subscribers.clear();
            }
            for (Subscription s : list) {
                s.unsubscribe();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNext(T t) {
            try {
                Observable<? extends R> observable = (Observable) this.mapper.call(t);
                EagerInnerSubscriber<R> inner = new EagerInnerSubscriber(this, this.bufferSize);
                if (!this.cancelled) {
                    synchronized (this.subscribers) {
                        if (this.cancelled) {
                            return;
                        }
                        this.subscribers.add(inner);
                    }
                }
            } catch (Throwable e) {
                Exceptions.throwOrReport(e, this.actual, t);
            }
        }

        public void onError(Throwable e) {
            this.error = e;
            this.done = true;
            drain();
        }

        public void onCompleted() {
            this.done = true;
            drain();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void drain() {
            if (this.wip.getAndIncrement() == 0) {
                int missed = 1;
                AtomicLong requested = this.sharedProducer;
                Subscriber<? super R> actualSubscriber = this.actual;
                NotificationLite<R> nl = NotificationLite.instance();
                while (!this.cancelled) {
                    EagerInnerSubscriber<R> innerSubscriber;
                    boolean outerDone = this.done;
                    synchronized (this.subscribers) {
                        innerSubscriber = (EagerInnerSubscriber) this.subscribers.peek();
                    }
                    boolean empty = innerSubscriber == null;
                    if (outerDone) {
                        Throwable error = this.error;
                        if (error != null) {
                            cleanup();
                            actualSubscriber.onError(error);
                            return;
                        } else if (empty) {
                            actualSubscriber.onCompleted();
                            return;
                        }
                    }
                    if (!empty) {
                        long requestedAmount = requested.get();
                        long emittedAmount = 0;
                        boolean unbounded = requestedAmount == Long.MAX_VALUE;
                        Queue<Object> innerQueue = innerSubscriber.queue;
                        boolean innerDone = false;
                        while (true) {
                            outerDone = innerSubscriber.done;
                            Object v = innerQueue.peek();
                            empty = v == null;
                            if (outerDone) {
                                Throwable innerError = innerSubscriber.error;
                                if (innerError == null) {
                                    if (empty) {
                                        break;
                                    }
                                }
                                cleanup();
                                actualSubscriber.onError(innerError);
                                return;
                            }
                            if (empty || requestedAmount == 0) {
                                break;
                            }
                            innerQueue.poll();
                            try {
                                actualSubscriber.onNext(nl.getValue(v));
                                requestedAmount--;
                                emittedAmount--;
                            } catch (Throwable ex) {
                                Exceptions.throwOrReport(ex, actualSubscriber, v);
                                return;
                            }
                        }
                        if (emittedAmount != 0) {
                            if (!unbounded) {
                                requested.addAndGet(emittedAmount);
                            }
                            if (!innerDone) {
                                innerSubscriber.requestMore(-emittedAmount);
                            }
                        }
                        if (innerDone) {
                            continue;
                        }
                    }
                    missed = this.wip.addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
                cleanup();
            }
        }
    }

    public OperatorEagerConcatMap(Func1<? super T, ? extends Observable<? extends R>> mapper, int bufferSize, int maxConcurrent) {
        this.mapper = mapper;
        this.bufferSize = bufferSize;
        this.maxConcurrent = maxConcurrent;
    }

    public Subscriber<? super T> call(Subscriber<? super R> t) {
        EagerOuterSubscriber<T, R> outer = new EagerOuterSubscriber(this.mapper, this.bufferSize, this.maxConcurrent, t);
        outer.init();
        return outer;
    }
}
