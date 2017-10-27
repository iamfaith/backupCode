package rx.internal.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.internal.util.OpenHashSet;
import rx.observables.ConnectableObservable;
import rx.schedulers.Timestamped;
import rx.subscriptions.Subscriptions;

public final class OperatorReplay<T> extends ConnectableObservable<T> {
    static final Func0 DEFAULT_UNBOUNDED_FACTORY = new Func0() {
        public Object call() {
            return new UnboundedReplayBuffer(16);
        }
    };
    final Func0<? extends ReplayBuffer<T>> bufferFactory;
    final AtomicReference<ReplaySubscriber<T>> current;
    final Observable<? extends T> source;

    static final class Node extends AtomicReference<Node> {
        private static final long serialVersionUID = 245354315435971818L;
        final long index;
        final Object value;

        public Node(Object value, long index) {
            this.value = value;
            this.index = index;
        }
    }

    interface ReplayBuffer<T> {
        void complete();

        void error(Throwable th);

        void next(T t);

        void replay(InnerProducer<T> innerProducer);
    }

    static class BoundedReplayBuffer<T> extends AtomicReference<Node> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 2346567790059478686L;
        long index;
        final NotificationLite<T> nl = NotificationLite.instance();
        int size;
        Node tail;

        public BoundedReplayBuffer() {
            Node n = new Node(null, 0);
            this.tail = n;
            set(n);
        }

        final void addLast(Node n) {
            this.tail.set(n);
            this.tail = n;
            this.size++;
        }

        final void removeFirst() {
            Node next = (Node) ((Node) get()).get();
            if (next == null) {
                throw new IllegalStateException("Empty list!");
            }
            this.size--;
            setFirst(next);
        }

        final void removeSome(int n) {
            Node head = (Node) get();
            while (n > 0) {
                head = (Node) head.get();
                n--;
                this.size--;
            }
            setFirst(head);
        }

        final void setFirst(Node n) {
            set(n);
        }

        public final void next(T value) {
            Object o = enterTransform(this.nl.next(value));
            long j = this.index + 1;
            this.index = j;
            addLast(new Node(o, j));
            truncate();
        }

        public final void error(Throwable e) {
            Object o = enterTransform(this.nl.error(e));
            long j = this.index + 1;
            this.index = j;
            addLast(new Node(o, j));
            truncateFinal();
        }

        public final void complete() {
            Object o = enterTransform(this.nl.completed());
            long j = this.index + 1;
            this.index = j;
            addLast(new Node(o, j));
            truncateFinal();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final void replay(InnerProducer<T> output) {
            synchronized (output) {
                if (output.emitting) {
                    output.missed = true;
                    return;
                }
                output.emitting = true;
            }
        }

        Object enterTransform(Object value) {
            return value;
        }

        Object leaveTransform(Object value) {
            return value;
        }

        void truncate() {
        }

        void truncateFinal() {
        }

        final void collect(Collection<? super T> output) {
            Node n = (Node) get();
            while (true) {
                Node next = (Node) n.get();
                if (next != null) {
                    Object v = leaveTransform(next.value);
                    if (!this.nl.isCompleted(v) && !this.nl.isError(v)) {
                        output.add(this.nl.getValue(v));
                        n = next;
                    } else {
                        return;
                    }
                }
                return;
            }
        }

        boolean hasError() {
            return this.tail.value != null && this.nl.isError(leaveTransform(this.tail.value));
        }

        boolean hasCompleted() {
            return this.tail.value != null && this.nl.isCompleted(leaveTransform(this.tail.value));
        }
    }

    static final class InnerProducer<T> extends AtomicLong implements Producer, Subscription {
        static final long UNSUBSCRIBED = Long.MIN_VALUE;
        private static final long serialVersionUID = -4453897557930727610L;
        final Subscriber<? super T> child;
        boolean emitting;
        Object index;
        boolean missed;
        final ReplaySubscriber<T> parent;
        final AtomicLong totalRequested = new AtomicLong();

        public InnerProducer(ReplaySubscriber<T> parent, Subscriber<? super T> child) {
            this.parent = parent;
            this.child = child;
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
                    if (r < 0 || n != 0) {
                        u = r + n;
                        if (u < 0) {
                            u = Long.MAX_VALUE;
                        }
                    } else {
                        return;
                    }
                } while (!compareAndSet(r, u));
                addTotalRequested(n);
                this.parent.manageRequests(this);
                this.parent.buffer.replay(this);
            }
        }

        void addTotalRequested(long n) {
            long r;
            long u;
            do {
                r = this.totalRequested.get();
                u = r + n;
                if (u < 0) {
                    u = Long.MAX_VALUE;
                }
            } while (!this.totalRequested.compareAndSet(r, u));
        }

        public long produced(long n) {
            if (n <= 0) {
                throw new IllegalArgumentException("Cant produce zero or less");
            }
            long u;
            long r;
            do {
                r = get();
                if (r == UNSUBSCRIBED) {
                    return UNSUBSCRIBED;
                }
                u = r - n;
                if (u < 0) {
                    throw new IllegalStateException("More produced (" + n + ") than requested (" + r + ")");
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
                this.parent.manageRequests(this);
            }
        }

        <U> U index() {
            return this.index;
        }
    }

    static final class UnboundedReplayBuffer<T> extends ArrayList<Object> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 7063189396499112664L;
        final NotificationLite<T> nl = NotificationLite.instance();
        volatile int size;

        public UnboundedReplayBuffer(int capacityHint) {
            super(capacityHint);
        }

        public void next(T value) {
            add(this.nl.next(value));
            this.size++;
        }

        public void error(Throwable e) {
            add(this.nl.error(e));
            this.size++;
        }

        public void complete() {
            add(this.nl.completed());
            this.size++;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void replay(InnerProducer<T> output) {
            synchronized (output) {
                if (output.emitting) {
                    output.missed = true;
                    return;
                }
                output.emitting = true;
            }
        }
    }

    static final class ReplaySubscriber<T> extends Subscriber<T> implements Subscription {
        static final InnerProducer[] EMPTY = new InnerProducer[0];
        static final InnerProducer[] TERMINATED = new InnerProducer[0];
        final ReplayBuffer<T> buffer;
        boolean coordinateAll;
        List<InnerProducer<T>> coordinationQueue;
        boolean done;
        boolean emitting;
        long maxChildRequested;
        long maxUpstreamRequested;
        boolean missed;
        final NotificationLite<T> nl = NotificationLite.instance();
        volatile Producer producer;
        final OpenHashSet<InnerProducer<T>> producers = new OpenHashSet();
        InnerProducer<T>[] producersCache = EMPTY;
        long producersCacheVersion;
        volatile long producersVersion;
        final AtomicBoolean shouldConnect = new AtomicBoolean();
        volatile boolean terminated;

        public ReplaySubscriber(AtomicReference<ReplaySubscriber<T>> atomicReference, ReplayBuffer<T> buffer) {
            this.buffer = buffer;
            request(0);
        }

        void init() {
            add(Subscriptions.create(new Action0() {
                public void call() {
                    if (!ReplaySubscriber.this.terminated) {
                        synchronized (ReplaySubscriber.this.producers) {
                            if (!ReplaySubscriber.this.terminated) {
                                ReplaySubscriber.this.producers.terminate();
                                ReplaySubscriber replaySubscriber = ReplaySubscriber.this;
                                replaySubscriber.producersVersion++;
                                ReplaySubscriber.this.terminated = true;
                            }
                        }
                    }
                }
            }));
        }

        boolean add(InnerProducer<T> producer) {
            if (producer == null) {
                throw new NullPointerException();
            } else if (this.terminated) {
                return false;
            } else {
                synchronized (this.producers) {
                    if (this.terminated) {
                        return false;
                    }
                    this.producers.add(producer);
                    this.producersVersion++;
                    return true;
                }
            }
        }

        void remove(InnerProducer<T> producer) {
            if (!this.terminated) {
                synchronized (this.producers) {
                    if (this.terminated) {
                        return;
                    }
                    this.producers.remove(producer);
                    this.producersVersion++;
                }
            }
        }

        public void setProducer(Producer p) {
            if (this.producer != null) {
                throw new IllegalStateException("Only a single producer can be set on a Subscriber.");
            }
            this.producer = p;
            manageRequests(null);
            replay();
        }

        public void onNext(T t) {
            if (!this.done) {
                this.buffer.next(t);
                replay();
            }
        }

        public void onError(Throwable e) {
            if (!this.done) {
                this.done = true;
                try {
                    this.buffer.error(e);
                    replay();
                } finally {
                    unsubscribe();
                }
            }
        }

        public void onCompleted() {
            if (!this.done) {
                this.done = true;
                try {
                    this.buffer.complete();
                    replay();
                } finally {
                    unsubscribe();
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void manageRequests(InnerProducer<T> inner) {
            if (!isUnsubscribed()) {
                synchronized (this) {
                    if (this.emitting) {
                        if (inner != null) {
                            List<InnerProducer<T>> q = this.coordinationQueue;
                            if (q == null) {
                                q = new ArrayList();
                                this.coordinationQueue = q;
                            }
                            q.add(inner);
                        } else {
                            this.coordinateAll = true;
                        }
                        this.missed = true;
                        return;
                    }
                    this.emitting = true;
                }
            }
        }

        InnerProducer<T>[] copyProducers() {
            InnerProducer<T>[] result;
            synchronized (this.producers) {
                Object[] a = this.producers.values();
                int n = a.length;
                result = new InnerProducer[n];
                System.arraycopy(a, 0, result, 0, n);
            }
            return result;
        }

        void makeRequest(long maxTotalRequests, long previousTotalRequests) {
            long ur = this.maxUpstreamRequested;
            Producer p = this.producer;
            long diff = maxTotalRequests - previousTotalRequests;
            if (diff != 0) {
                this.maxChildRequested = maxTotalRequests;
                if (p == null) {
                    long u = ur + diff;
                    if (u < 0) {
                        u = Long.MAX_VALUE;
                    }
                    this.maxUpstreamRequested = u;
                } else if (ur != 0) {
                    this.maxUpstreamRequested = 0;
                    p.request(ur + diff);
                } else {
                    p.request(diff);
                }
            } else if (ur != 0 && p != null) {
                this.maxUpstreamRequested = 0;
                p.request(ur);
            }
        }

        void replay() {
            InnerProducer<T>[] pc = this.producersCache;
            if (this.producersCacheVersion != this.producersVersion) {
                synchronized (this.producers) {
                    pc = this.producersCache;
                    Object[] a = this.producers.values();
                    int n = a.length;
                    if (pc.length != n) {
                        pc = new InnerProducer[n];
                        this.producersCache = pc;
                    }
                    System.arraycopy(a, 0, pc, 0, n);
                    this.producersCacheVersion = this.producersVersion;
                }
            }
            ReplayBuffer<T> b = this.buffer;
            for (InnerProducer<T> rp : pc) {
                if (rp != null) {
                    b.replay(rp);
                }
            }
        }
    }

    static final class SizeAndTimeBoundReplayBuffer<T> extends BoundedReplayBuffer<T> {
        private static final long serialVersionUID = 3457957419649567404L;
        final int limit;
        final long maxAgeInMillis;
        final Scheduler scheduler;

        public SizeAndTimeBoundReplayBuffer(int limit, long maxAgeInMillis, Scheduler scheduler) {
            this.scheduler = scheduler;
            this.limit = limit;
            this.maxAgeInMillis = maxAgeInMillis;
        }

        Object enterTransform(Object value) {
            return new Timestamped(this.scheduler.now(), value);
        }

        Object leaveTransform(Object value) {
            return ((Timestamped) value).getValue();
        }

        void truncate() {
            long timeLimit = this.scheduler.now() - this.maxAgeInMillis;
            Node prev = (Node) get();
            Node next = (Node) prev.get();
            int e = 0;
            while (next != null) {
                if (this.size <= this.limit) {
                    if (next.value.getTimestampMillis() > timeLimit) {
                        break;
                    }
                    e++;
                    this.size--;
                    prev = next;
                    next = (Node) next.get();
                } else {
                    e++;
                    this.size--;
                    prev = next;
                    next = (Node) next.get();
                }
            }
            if (e != 0) {
                setFirst(prev);
            }
        }

        void truncateFinal() {
            long timeLimit = this.scheduler.now() - this.maxAgeInMillis;
            Node prev = (Node) get();
            Node next = (Node) prev.get();
            int e = 0;
            while (next != null && this.size > 1 && next.value.getTimestampMillis() <= timeLimit) {
                e++;
                this.size--;
                prev = next;
                next = (Node) next.get();
            }
            if (e != 0) {
                setFirst(prev);
            }
        }
    }

    static final class SizeBoundReplayBuffer<T> extends BoundedReplayBuffer<T> {
        private static final long serialVersionUID = -5898283885385201806L;
        final int limit;

        public SizeBoundReplayBuffer(int limit) {
            this.limit = limit;
        }

        void truncate() {
            if (this.size > this.limit) {
                removeFirst();
            }
        }
    }

    public static <T, U, R> Observable<R> multicastSelector(final Func0<? extends ConnectableObservable<U>> connectableFactory, final Func1<? super Observable<U>, ? extends Observable<R>> selector) {
        return Observable.create(new OnSubscribe<R>() {
            public void call(final Subscriber<? super R> child) {
                try {
                    ConnectableObservable<U> co = (ConnectableObservable) connectableFactory.call();
                    ((Observable) selector.call(co)).subscribe((Subscriber) child);
                    co.connect(new Action1<Subscription>() {
                        public void call(Subscription t) {
                            child.add(t);
                        }
                    });
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, (Observer) child);
                }
            }
        });
    }

    public static <T> ConnectableObservable<T> observeOn(final ConnectableObservable<T> co, Scheduler scheduler) {
        final Observable<T> observable = co.observeOn(scheduler);
        return new ConnectableObservable<T>(new OnSubscribe<T>() {
            public void call(final Subscriber<? super T> child) {
                observable.unsafeSubscribe(new Subscriber<T>(child) {
                    public void onNext(T t) {
                        child.onNext(t);
                    }

                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    public void onCompleted() {
                        child.onCompleted();
                    }
                });
            }
        }) {
            public void connect(Action1<? super Subscription> connection) {
                co.connect(connection);
            }
        };
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source) {
        return create((Observable) source, DEFAULT_UNBOUNDED_FACTORY);
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source, final int bufferSize) {
        if (bufferSize == ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED) {
            return create(source);
        }
        return create((Observable) source, new Func0<ReplayBuffer<T>>() {
            public ReplayBuffer<T> call() {
                return new SizeBoundReplayBuffer(bufferSize);
            }
        });
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source, long maxAge, TimeUnit unit, Scheduler scheduler) {
        return create(source, maxAge, unit, scheduler, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source, long maxAge, TimeUnit unit, final Scheduler scheduler, final int bufferSize) {
        final long maxAgeInMillis = unit.toMillis(maxAge);
        return create((Observable) source, new Func0<ReplayBuffer<T>>() {
            public ReplayBuffer<T> call() {
                return new SizeAndTimeBoundReplayBuffer(bufferSize, maxAgeInMillis, scheduler);
            }
        });
    }

    static <T> ConnectableObservable<T> create(Observable<? extends T> source, final Func0<? extends ReplayBuffer<T>> bufferFactory) {
        final AtomicReference<ReplaySubscriber<T>> curr = new AtomicReference();
        return new OperatorReplay(new OnSubscribe<T>() {
            public void call(Subscriber<? super T> child) {
                ReplaySubscriber<T> r;
                ReplaySubscriber<T> u;
                do {
                    r = (ReplaySubscriber) curr.get();
                    if (r != null) {
                        break;
                    }
                    u = new ReplaySubscriber(curr, (ReplayBuffer) bufferFactory.call());
                    u.init();
                } while (!curr.compareAndSet(r, u));
                r = u;
                InnerProducer<T> inner = new InnerProducer(r, child);
                r.add(inner);
                child.add(inner);
                r.buffer.replay(inner);
                child.setProducer(inner);
            }
        }, source, curr, bufferFactory);
    }

    private OperatorReplay(OnSubscribe<T> onSubscribe, Observable<? extends T> source, AtomicReference<ReplaySubscriber<T>> current, Func0<? extends ReplayBuffer<T>> bufferFactory) {
        super(onSubscribe);
        this.source = source;
        this.current = current;
        this.bufferFactory = bufferFactory;
    }

    public void connect(Action1<? super Subscription> connection) {
        ReplaySubscriber<T> ps;
        ReplaySubscriber<T> u;
        boolean doConnect;
        do {
            ps = (ReplaySubscriber) this.current.get();
            if (ps != null && !ps.isUnsubscribed()) {
                break;
            }
            u = new ReplaySubscriber(this.current, (ReplayBuffer) this.bufferFactory.call());
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
