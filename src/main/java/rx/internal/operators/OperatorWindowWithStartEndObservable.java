package rx.internal.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.Observable.Operator;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func1;
import rx.observers.SerializedObserver;
import rx.observers.SerializedSubscriber;
import rx.subjects.UnicastSubject;
import rx.subscriptions.CompositeSubscription;

public final class OperatorWindowWithStartEndObservable<T, U, V> implements Operator<Observable<T>, T> {
    final Func1<? super U, ? extends Observable<? extends V>> windowClosingSelector;
    final Observable<? extends U> windowOpenings;

    static final class SerializedSubject<T> {
        final Observer<T> consumer;
        final Observable<T> producer;

        public SerializedSubject(Observer<T> consumer, Observable<T> producer) {
            this.consumer = new SerializedObserver(consumer);
            this.producer = producer;
        }
    }

    final class SourceSubscriber extends Subscriber<T> {
        final Subscriber<? super Observable<T>> child;
        final List<SerializedSubject<T>> chunks = new LinkedList();
        final CompositeSubscription csub;
        boolean done;
        final Object guard = new Object();

        public SourceSubscriber(Subscriber<? super Observable<T>> child, CompositeSubscription csub) {
            this.child = new SerializedSubscriber(child);
            this.csub = csub;
        }

        public void onStart() {
            request(Long.MAX_VALUE);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNext(T t) {
            synchronized (this.guard) {
                if (this.done) {
                    return;
                }
                List<SerializedSubject<T>> list = new ArrayList(this.chunks);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onError(Throwable e) {
            try {
                synchronized (this.guard) {
                    if (this.done) {
                        this.csub.unsubscribe();
                        return;
                    }
                    this.done = true;
                    List<SerializedSubject<T>> list = new ArrayList(this.chunks);
                    this.chunks.clear();
                }
            } catch (Throwable th) {
                this.csub.unsubscribe();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onCompleted() {
            try {
                synchronized (this.guard) {
                    if (this.done) {
                        this.csub.unsubscribe();
                        return;
                    }
                    this.done = true;
                    List<SerializedSubject<T>> list = new ArrayList(this.chunks);
                    this.chunks.clear();
                }
            } catch (Throwable th) {
                this.csub.unsubscribe();
            }
        }

        void beginWindow(U token) {
            final SerializedSubject<T> s = createSerializedSubject();
            synchronized (this.guard) {
                if (this.done) {
                    return;
                }
                this.chunks.add(s);
                this.child.onNext(s.producer);
                try {
                    Observable<? extends V> end = (Observable) OperatorWindowWithStartEndObservable.this.windowClosingSelector.call(token);
                    Subscriber<V> v = new Subscriber<V>() {
                        boolean once = true;

                        public void onNext(V v) {
                            onCompleted();
                        }

                        public void onError(Throwable e) {
                        }

                        public void onCompleted() {
                            if (this.once) {
                                this.once = false;
                                SourceSubscriber.this.endWindow(s);
                                SourceSubscriber.this.csub.remove(this);
                            }
                        }
                    };
                    this.csub.add(v);
                    end.unsafeSubscribe(v);
                } catch (Throwable e) {
                    onError(e);
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void endWindow(SerializedSubject<T> window) {
            boolean terminate = false;
            synchronized (this.guard) {
                if (!this.done) {
                    Iterator<SerializedSubject<T>> it = this.chunks.iterator();
                    while (it.hasNext()) {
                        if (((SerializedSubject) it.next()) == window) {
                            terminate = true;
                            it.remove();
                            break;
                        }
                    }
                }
            }
        }

        SerializedSubject<T> createSerializedSubject() {
            UnicastSubject<T> bus = UnicastSubject.create();
            return new SerializedSubject(bus, bus);
        }
    }

    public OperatorWindowWithStartEndObservable(Observable<? extends U> windowOpenings, Func1<? super U, ? extends Observable<? extends V>> windowClosingSelector) {
        this.windowOpenings = windowOpenings;
        this.windowClosingSelector = windowClosingSelector;
    }

    public Subscriber<? super T> call(Subscriber<? super Observable<T>> child) {
        CompositeSubscription csub = new CompositeSubscription();
        child.add(csub);
        final SourceSubscriber sub = new SourceSubscriber(child, csub);
        Subscriber<U> open = new Subscriber<U>() {
            public void onStart() {
                request(Long.MAX_VALUE);
            }

            public void onNext(U t) {
                sub.beginWindow(t);
            }

            public void onError(Throwable e) {
                sub.onError(e);
            }

            public void onCompleted() {
                sub.onCompleted();
            }
        };
        csub.add(sub);
        csub.add(open);
        this.windowOpenings.unsafeSubscribe(open);
        return sub;
    }
}
