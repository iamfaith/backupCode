package rx.internal.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable.OnSubscribe;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.internal.producers.ProducerArbiter;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.UtilityFunctions;
import rx.observables.GroupedObservable;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.Subscriptions;

public final class OperatorGroupBy<T, K, V> implements Operator<GroupedObservable<K, V>, T> {
    final int bufferSize;
    final boolean delayError;
    final Func1<? super T, ? extends K> keySelector;
    final Func1<? super T, ? extends V> valueSelector;

    public static final class GroupByProducer implements Producer {
        final GroupBySubscriber<?, ?, ?> parent;

        public GroupByProducer(GroupBySubscriber<?, ?, ?> parent) {
            this.parent = parent;
        }

        public void request(long n) {
            this.parent.requestMore(n);
        }
    }

    public static final class GroupBySubscriber<T, K, V> extends Subscriber<T> {
        static final Object NULL_KEY = new Object();
        final Subscriber<? super GroupedObservable<K, V>> actual;
        final int bufferSize;
        final AtomicBoolean cancelled;
        final boolean delayError;
        volatile boolean done;
        Throwable error;
        final AtomicInteger groupCount;
        final Map<Object, GroupedUnicast<K, V>> groups = new ConcurrentHashMap();
        final Func1<? super T, ? extends K> keySelector;
        final GroupByProducer producer;
        final Queue<GroupedObservable<K, V>> queue = new ConcurrentLinkedQueue();
        final AtomicLong requested;
        final ProducerArbiter s = new ProducerArbiter();
        final Func1<? super T, ? extends V> valueSelector;
        final AtomicInteger wip;

        public GroupBySubscriber(Subscriber<? super GroupedObservable<K, V>> actual, Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector, int bufferSize, boolean delayError) {
            this.actual = actual;
            this.keySelector = keySelector;
            this.valueSelector = valueSelector;
            this.bufferSize = bufferSize;
            this.delayError = delayError;
            this.s.request((long) bufferSize);
            this.producer = new GroupByProducer(this);
            this.cancelled = new AtomicBoolean();
            this.requested = new AtomicLong();
            this.groupCount = new AtomicInteger(1);
            this.wip = new AtomicInteger();
        }

        public void setProducer(Producer s) {
            this.s.setProducer(s);
        }

        public void onNext(T t) {
            if (!this.done) {
                Queue<GroupedObservable<K, V>> q = this.queue;
                Subscriber<? super GroupedObservable<K, V>> a = this.actual;
                try {
                    K key = this.keySelector.call(t);
                    boolean notNew = true;
                    Object mapKey = key != null ? key : NULL_KEY;
                    GroupedUnicast<K, V> group = (GroupedUnicast) this.groups.get(mapKey);
                    if (group == null) {
                        if (!this.cancelled.get()) {
                            group = GroupedUnicast.createWith(key, this.bufferSize, this, this.delayError);
                            this.groups.put(mapKey, group);
                            this.groupCount.getAndIncrement();
                            notNew = false;
                            q.offer(group);
                            drain();
                        } else {
                            return;
                        }
                    }
                    try {
                        group.onNext(this.valueSelector.call(t));
                        if (notNew) {
                            this.s.request(1);
                        }
                    } catch (Throwable ex) {
                        unsubscribe();
                        errorAll(a, q, ex);
                    }
                } catch (Throwable ex2) {
                    unsubscribe();
                    errorAll(a, q, ex2);
                }
            }
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(t);
                return;
            }
            this.error = t;
            this.done = true;
            this.groupCount.decrementAndGet();
            drain();
        }

        public void onCompleted() {
            if (!this.done) {
                for (GroupedUnicast<K, V> e : this.groups.values()) {
                    e.onComplete();
                }
                this.groups.clear();
                this.done = true;
                this.groupCount.decrementAndGet();
                drain();
            }
        }

        public void requestMore(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n >= 0 required but it was " + n);
            }
            BackpressureUtils.getAndAddRequest(this.requested, n);
            drain();
        }

        public void cancel() {
            if (this.cancelled.compareAndSet(false, true) && this.groupCount.decrementAndGet() == 0) {
                unsubscribe();
            }
        }

        public void cancel(K key) {
            if (this.groups.remove(key != null ? key : NULL_KEY) != null && this.groupCount.decrementAndGet() == 0) {
                unsubscribe();
            }
        }

        void drain() {
            if (this.wip.getAndIncrement() == 0) {
                int missed = 1;
                Queue<GroupedObservable<K, V>> q = this.queue;
                Subscriber<? super GroupedObservable<K, V>> a = this.actual;
                while (!checkTerminated(this.done, q.isEmpty(), a, q)) {
                    long r = this.requested.get();
                    boolean unbounded = r == Long.MAX_VALUE;
                    long e = 0;
                    while (r != 0) {
                        boolean d = this.done;
                        GroupedObservable<K, V> t = (GroupedObservable) q.poll();
                        boolean empty = t == null;
                        if (!checkTerminated(d, empty, a, q)) {
                            if (empty) {
                                break;
                            }
                            a.onNext(t);
                            r--;
                            e--;
                        } else {
                            return;
                        }
                    }
                    if (e != 0) {
                        if (!unbounded) {
                            this.requested.addAndGet(e);
                        }
                        this.s.request(-e);
                    }
                    missed = this.wip.addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
        }

        void errorAll(Subscriber<? super GroupedObservable<K, V>> a, Queue<?> q, Throwable ex) {
            q.clear();
            List<GroupedUnicast<K, V>> list = new ArrayList(this.groups.values());
            this.groups.clear();
            for (GroupedUnicast<K, V> e : list) {
                e.onError(ex);
            }
            a.onError(ex);
        }

        boolean checkTerminated(boolean d, boolean empty, Subscriber<? super GroupedObservable<K, V>> a, Queue<?> q) {
            if (d) {
                Throwable err = this.error;
                if (err != null) {
                    errorAll(a, q, err);
                    return true;
                } else if (empty) {
                    this.actual.onCompleted();
                    return true;
                }
            }
            return false;
        }
    }

    static final class GroupedUnicast<K, T> extends GroupedObservable<K, T> {
        final State<T, K> state;

        public static <T, K> GroupedUnicast<K, T> createWith(K key, int bufferSize, GroupBySubscriber<?, K, T> parent, boolean delayError) {
            return new GroupedUnicast(key, new State(bufferSize, parent, key, delayError));
        }

        protected GroupedUnicast(K key, State<T, K> state) {
            super(key, state);
            this.state = state;
        }

        public void onNext(T t) {
            this.state.onNext(t);
        }

        public void onError(Throwable e) {
            this.state.onError(e);
        }

        public void onComplete() {
            this.state.onComplete();
        }
    }

    static final class State<T, K> extends AtomicInteger implements Producer, Subscription, OnSubscribe<T> {
        private static final long serialVersionUID = -3852313036005250360L;
        final AtomicReference<Subscriber<? super T>> actual;
        final AtomicBoolean cancelled;
        final boolean delayError;
        volatile boolean done;
        Throwable error;
        final K key;
        final AtomicBoolean once;
        final GroupBySubscriber<?, K, T> parent;
        final Queue<Object> queue = new ConcurrentLinkedQueue();
        final AtomicLong requested;

        public State(int bufferSize, GroupBySubscriber<?, K, T> parent, K key, boolean delayError) {
            this.parent = parent;
            this.key = key;
            this.delayError = delayError;
            this.cancelled = new AtomicBoolean();
            this.actual = new AtomicReference();
            this.once = new AtomicBoolean();
            this.requested = new AtomicLong();
        }

        public void request(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n >= required but it was " + n);
            } else if (n != 0) {
                BackpressureUtils.getAndAddRequest(this.requested, n);
                drain();
            }
        }

        public boolean isUnsubscribed() {
            return this.cancelled.get();
        }

        public void unsubscribe() {
            if (this.cancelled.compareAndSet(false, true) && getAndIncrement() == 0) {
                this.parent.cancel(this.key);
            }
        }

        public void call(Subscriber<? super T> s) {
            if (this.once.compareAndSet(false, true)) {
                s.add(this);
                s.setProducer(this);
                this.actual.lazySet(s);
                drain();
                return;
            }
            s.onError(new IllegalStateException("Only one Subscriber allowed!"));
        }

        public void onNext(T t) {
            if (t == null) {
                this.error = new NullPointerException();
                this.done = true;
            } else {
                this.queue.offer(NotificationLite.instance().next(t));
            }
            drain();
        }

        public void onError(Throwable e) {
            this.error = e;
            this.done = true;
            drain();
        }

        public void onComplete() {
            this.done = true;
            drain();
        }

        void drain() {
            if (getAndIncrement() == 0) {
                int missed = 1;
                Queue<Object> q = this.queue;
                boolean delayError = this.delayError;
                Subscriber<? super T> a = (Subscriber) this.actual.get();
                NotificationLite<T> nl = NotificationLite.instance();
                while (true) {
                    if (a != null) {
                        if (!checkTerminated(this.done, q.isEmpty(), a, delayError)) {
                            long r = this.requested.get();
                            boolean unbounded = r == Long.MAX_VALUE;
                            long e = 0;
                            while (r != 0) {
                                boolean d = this.done;
                                Object v = q.poll();
                                boolean empty = v == null;
                                if (!checkTerminated(d, empty, a, delayError)) {
                                    if (empty) {
                                        break;
                                    }
                                    a.onNext(nl.getValue(v));
                                    r--;
                                    e--;
                                } else {
                                    return;
                                }
                            }
                            if (e != 0) {
                                if (!unbounded) {
                                    this.requested.addAndGet(e);
                                }
                                this.parent.s.request(-e);
                            }
                        } else {
                            return;
                        }
                    }
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                    if (a == null) {
                        a = (Subscriber) this.actual.get();
                    }
                }
            }
        }

        boolean checkTerminated(boolean d, boolean empty, Subscriber<? super T> a, boolean delayError) {
            if (this.cancelled.get()) {
                this.queue.clear();
                this.parent.cancel(this.key);
                return true;
            }
            if (d) {
                Throwable e;
                if (!delayError) {
                    e = this.error;
                    if (e != null) {
                        this.queue.clear();
                        a.onError(e);
                        return true;
                    } else if (empty) {
                        a.onCompleted();
                        return true;
                    }
                } else if (empty) {
                    e = this.error;
                    if (e != null) {
                        a.onError(e);
                        return true;
                    }
                    a.onCompleted();
                    return true;
                }
            }
            return false;
        }
    }

    public OperatorGroupBy(Func1<? super T, ? extends K> keySelector) {
        this(keySelector, UtilityFunctions.identity(), RxRingBuffer.SIZE, false);
    }

    public OperatorGroupBy(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector) {
        this(keySelector, valueSelector, RxRingBuffer.SIZE, false);
    }

    public OperatorGroupBy(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector, int bufferSize, boolean delayError) {
        this.keySelector = keySelector;
        this.valueSelector = valueSelector;
        this.bufferSize = bufferSize;
        this.delayError = delayError;
    }

    public Subscriber<? super T> call(Subscriber<? super GroupedObservable<K, V>> t) {
        final GroupBySubscriber<T, K, V> parent = new GroupBySubscriber(t, this.keySelector, this.valueSelector, this.bufferSize, this.delayError);
        t.add(Subscriptions.create(new Action0() {
            public void call() {
                parent.cancel();
            }
        }));
        t.setProducer(parent.producer);
        return parent;
    }
}
