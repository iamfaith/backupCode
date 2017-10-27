package rx.internal.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable.Operator;
import rx.Observer;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.observers.SerializedSubscriber;

public final class OperatorBufferWithTime<T> implements Operator<List<T>, T> {
    final int count;
    final Scheduler scheduler;
    final long timeshift;
    final long timespan;
    final TimeUnit unit;

    final class ExactSubscriber extends Subscriber<T> {
        final Subscriber<? super List<T>> child;
        List<T> chunk = new ArrayList();
        boolean done;
        final Worker inner;

        public ExactSubscriber(Subscriber<? super List<T>> child, Worker inner) {
            this.child = child;
            this.inner = inner;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNext(T t) {
            List<T> toEmit = null;
            synchronized (this) {
                if (this.done) {
                    return;
                }
                this.chunk.add(t);
                if (this.chunk.size() == OperatorBufferWithTime.this.count) {
                    toEmit = this.chunk;
                    this.chunk = new ArrayList();
                }
            }
        }

        public void onError(Throwable e) {
            synchronized (this) {
                if (this.done) {
                    return;
                }
                this.done = true;
                this.chunk = null;
                this.child.onError(e);
                unsubscribe();
            }
        }

        public void onCompleted() {
            try {
                this.inner.unsubscribe();
                synchronized (this) {
                    if (this.done) {
                        return;
                    }
                    this.done = true;
                    List<T> toEmit = this.chunk;
                    this.chunk = null;
                    this.child.onNext(toEmit);
                    this.child.onCompleted();
                    unsubscribe();
                }
            } catch (Throwable t) {
                Exceptions.throwOrReport(t, this.child);
            }
        }

        void scheduleExact() {
            this.inner.schedulePeriodically(new Action0() {
                public void call() {
                    ExactSubscriber.this.emit();
                }
            }, OperatorBufferWithTime.this.timespan, OperatorBufferWithTime.this.timespan, OperatorBufferWithTime.this.unit);
        }

        void emit() {
            synchronized (this) {
                if (this.done) {
                    return;
                }
                List<T> toEmit = this.chunk;
                this.chunk = new ArrayList();
                try {
                    this.child.onNext(toEmit);
                } catch (Throwable t) {
                    Exceptions.throwOrReport(t, (Observer) this);
                }
            }
        }
    }

    final class InexactSubscriber extends Subscriber<T> {
        final Subscriber<? super List<T>> child;
        final List<List<T>> chunks = new LinkedList();
        boolean done;
        final Worker inner;

        public InexactSubscriber(Subscriber<? super List<T>> child, Worker inner) {
            this.child = child;
            this.inner = inner;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNext(T t) {
            Throwable th;
            synchronized (this) {
                if (this.done) {
                    return;
                }
                Iterator<List<T>> it = this.chunks.iterator();
                List<List<T>> sizeReached = null;
                while (it.hasNext()) {
                    List<List<T>> sizeReached2;
                    try {
                        List<T> chunk = (List) it.next();
                        chunk.add(t);
                        if (chunk.size() == OperatorBufferWithTime.this.count) {
                            it.remove();
                            if (sizeReached == null) {
                                sizeReached2 = new LinkedList();
                            } else {
                                sizeReached2 = sizeReached;
                            }
                            try {
                                sizeReached2.add(chunk);
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        } else {
                            sizeReached2 = sizeReached;
                        }
                        sizeReached = sizeReached2;
                    } catch (Throwable th3) {
                        th = th3;
                        sizeReached2 = sizeReached;
                    }
                }
            }
            throw th;
        }

        public void onError(Throwable e) {
            synchronized (this) {
                if (this.done) {
                    return;
                }
                this.done = true;
                this.chunks.clear();
                this.child.onError(e);
                unsubscribe();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onCompleted() {
            try {
                synchronized (this) {
                    if (this.done) {
                        return;
                    }
                    this.done = true;
                    List<List<T>> sizeReached = new LinkedList(this.chunks);
                    this.chunks.clear();
                }
            } catch (Throwable t) {
                Exceptions.throwOrReport(t, this.child);
            }
        }

        void scheduleChunk() {
            this.inner.schedulePeriodically(new Action0() {
                public void call() {
                    InexactSubscriber.this.startNewChunk();
                }
            }, OperatorBufferWithTime.this.timeshift, OperatorBufferWithTime.this.timeshift, OperatorBufferWithTime.this.unit);
        }

        void startNewChunk() {
            final List<T> chunk = new ArrayList();
            synchronized (this) {
                if (this.done) {
                    return;
                }
                this.chunks.add(chunk);
                this.inner.schedule(new Action0() {
                    public void call() {
                        InexactSubscriber.this.emitChunk(chunk);
                    }
                }, OperatorBufferWithTime.this.timespan, OperatorBufferWithTime.this.unit);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void emitChunk(List<T> chunkToEmit) {
            boolean emit = false;
            synchronized (this) {
                if (!this.done) {
                    Iterator<List<T>> it = this.chunks.iterator();
                    while (it.hasNext()) {
                        if (((List) it.next()) == chunkToEmit) {
                            it.remove();
                            emit = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    public OperatorBufferWithTime(long timespan, long timeshift, TimeUnit unit, int count, Scheduler scheduler) {
        this.timespan = timespan;
        this.timeshift = timeshift;
        this.unit = unit;
        this.count = count;
        this.scheduler = scheduler;
    }

    public Subscriber<? super T> call(Subscriber<? super List<T>> child) {
        Worker inner = this.scheduler.createWorker();
        SerializedSubscriber<List<T>> serialized = new SerializedSubscriber(child);
        if (this.timespan == this.timeshift) {
            ExactSubscriber bsub = new ExactSubscriber(serialized, inner);
            bsub.add(inner);
            child.add(bsub);
            bsub.scheduleExact();
            return bsub;
        }
        Subscriber bsub2 = new InexactSubscriber(serialized, inner);
        bsub2.add(inner);
        child.add(bsub2);
        bsub2.startNewChunk();
        bsub2.scheduleChunk();
        return bsub2;
    }
}
