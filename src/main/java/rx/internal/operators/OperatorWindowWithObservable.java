package rx.internal.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.Observable.Operator;
import rx.Observer;
import rx.Subscriber;
import rx.observers.SerializedSubscriber;
import rx.subjects.UnicastSubject;

public final class OperatorWindowWithObservable<T, U> implements Operator<Observable<T>, T> {
    static final Object NEXT_SUBJECT = new Object();
    static final NotificationLite<Object> nl = NotificationLite.instance();
    final Observable<U> other;

    static final class BoundarySubscriber<T, U> extends Subscriber<U> {
        final SourceSubscriber<T> sub;

        public BoundarySubscriber(Subscriber<?> subscriber, SourceSubscriber<T> sub) {
            this.sub = sub;
        }

        public void onStart() {
            request(Long.MAX_VALUE);
        }

        public void onNext(U u) {
            this.sub.replaceWindow();
        }

        public void onError(Throwable e) {
            this.sub.onError(e);
        }

        public void onCompleted() {
            this.sub.onCompleted();
        }
    }

    static final class SourceSubscriber<T> extends Subscriber<T> {
        final Subscriber<? super Observable<T>> child;
        Observer<T> consumer;
        boolean emitting;
        final Object guard = new Object();
        Observable<T> producer;
        List<Object> queue;

        public SourceSubscriber(Subscriber<? super Observable<T>> child) {
            this.child = new SerializedSubscriber(child);
        }

        public void onStart() {
            request(Long.MAX_VALUE);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNext(T t) {
            synchronized (this.guard) {
                if (this.emitting) {
                    if (this.queue == null) {
                        this.queue = new ArrayList();
                    }
                    this.queue.add(t);
                    return;
                }
                List<Object> localQueue = this.queue;
                this.queue = null;
                this.emitting = true;
            }
        }

        void drain(List<Object> queue) {
            if (queue != null) {
                for (T o : queue) {
                    if (o == OperatorWindowWithObservable.NEXT_SUBJECT) {
                        replaceSubject();
                    } else if (OperatorWindowWithObservable.nl.isError(o)) {
                        error(OperatorWindowWithObservable.nl.getError(o));
                        return;
                    } else if (OperatorWindowWithObservable.nl.isCompleted(o)) {
                        complete();
                        return;
                    } else {
                        emitValue(o);
                    }
                }
            }
        }

        void replaceSubject() {
            Observer<T> s = this.consumer;
            if (s != null) {
                s.onCompleted();
            }
            createNewWindow();
            this.child.onNext(this.producer);
        }

        void createNewWindow() {
            UnicastSubject<T> bus = UnicastSubject.create();
            this.consumer = bus;
            this.producer = bus;
        }

        void emitValue(T t) {
            Observer<T> s = this.consumer;
            if (s != null) {
                s.onNext(t);
            }
        }

        public void onError(Throwable e) {
            synchronized (this.guard) {
                if (this.emitting) {
                    this.queue = Collections.singletonList(OperatorWindowWithObservable.nl.error(e));
                    return;
                }
                this.queue = null;
                this.emitting = true;
                error(e);
            }
        }

        public void onCompleted() {
            synchronized (this.guard) {
                if (this.emitting) {
                    if (this.queue == null) {
                        this.queue = new ArrayList();
                    }
                    this.queue.add(OperatorWindowWithObservable.nl.completed());
                    return;
                }
                List<Object> localQueue = this.queue;
                this.queue = null;
                this.emitting = true;
                try {
                    drain(localQueue);
                    complete();
                } catch (Throwable e) {
                    error(e);
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void replaceWindow() {
            synchronized (this.guard) {
                if (this.emitting) {
                    if (this.queue == null) {
                        this.queue = new ArrayList();
                    }
                    this.queue.add(OperatorWindowWithObservable.NEXT_SUBJECT);
                    return;
                }
                List<Object> localQueue = this.queue;
                this.queue = null;
                this.emitting = true;
            }
        }

        void complete() {
            Observer<T> s = this.consumer;
            this.consumer = null;
            this.producer = null;
            if (s != null) {
                s.onCompleted();
            }
            this.child.onCompleted();
            unsubscribe();
        }

        void error(Throwable e) {
            Observer<T> s = this.consumer;
            this.consumer = null;
            this.producer = null;
            if (s != null) {
                s.onError(e);
            }
            this.child.onError(e);
            unsubscribe();
        }
    }

    public OperatorWindowWithObservable(Observable<U> other) {
        this.other = other;
    }

    public Subscriber<? super T> call(Subscriber<? super Observable<T>> child) {
        SourceSubscriber<T> sub = new SourceSubscriber(child);
        BoundarySubscriber<T, U> bs = new BoundarySubscriber(child, sub);
        child.add(sub);
        child.add(bs);
        sub.replaceWindow();
        this.other.unsafeSubscribe(bs);
        return sub;
    }
}
