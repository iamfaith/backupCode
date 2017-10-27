package rx.internal.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.Observable.Operator;
import rx.Observer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.observers.SerializedSubscriber;
import rx.subscriptions.CompositeSubscription;

public final class OperatorBufferWithStartEndObservable<T, TOpening, TClosing> implements Operator<List<T>, T> {
    final Func1<? super TOpening, ? extends Observable<? extends TClosing>> bufferClosing;
    final Observable<? extends TOpening> bufferOpening;

    final class BufferingSubscriber extends Subscriber<T> {
        final Subscriber<? super List<T>> child;
        final List<List<T>> chunks = new LinkedList();
        final CompositeSubscription closingSubscriptions = new CompositeSubscription();
        boolean done;

        public BufferingSubscriber(Subscriber<? super List<T>> child) {
            this.child = child;
            add(this.closingSubscriptions);
        }

        public void onNext(T t) {
            synchronized (this) {
                for (List<T> chunk : this.chunks) {
                    chunk.add(t);
                }
            }
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
                    List<List<T>> toEmit = new LinkedList(this.chunks);
                    this.chunks.clear();
                }
            } catch (Throwable t) {
                Exceptions.throwOrReport(t, this.child);
            }
        }

        void startBuffer(TOpening v) {
            final List<T> chunk = new ArrayList();
            synchronized (this) {
                if (this.done) {
                    return;
                }
                this.chunks.add(chunk);
                try {
                    Observable<? extends TClosing> cobs = (Observable) OperatorBufferWithStartEndObservable.this.bufferClosing.call(v);
                    Subscriber<TClosing> closeSubscriber = new Subscriber<TClosing>() {
                        public void onNext(TClosing tClosing) {
                            BufferingSubscriber.this.closingSubscriptions.remove(this);
                            BufferingSubscriber.this.endBuffer(chunk);
                        }

                        public void onError(Throwable e) {
                            BufferingSubscriber.this.onError(e);
                        }

                        public void onCompleted() {
                            BufferingSubscriber.this.closingSubscriptions.remove(this);
                            BufferingSubscriber.this.endBuffer(chunk);
                        }
                    };
                    this.closingSubscriptions.add(closeSubscriber);
                    cobs.unsafeSubscribe(closeSubscriber);
                } catch (Throwable t) {
                    Exceptions.throwOrReport(t, (Observer) this);
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void endBuffer(List<T> toEnd) {
            boolean canEnd = false;
            synchronized (this) {
                if (!this.done) {
                    Iterator<List<T>> it = this.chunks.iterator();
                    while (it.hasNext()) {
                        if (((List) it.next()) == toEnd) {
                            canEnd = true;
                            it.remove();
                            break;
                        }
                    }
                }
            }
        }
    }

    public OperatorBufferWithStartEndObservable(Observable<? extends TOpening> bufferOpenings, Func1<? super TOpening, ? extends Observable<? extends TClosing>> bufferClosingSelector) {
        this.bufferOpening = bufferOpenings;
        this.bufferClosing = bufferClosingSelector;
    }

    public Subscriber<? super T> call(Subscriber<? super List<T>> child) {
        final BufferingSubscriber bsub = new BufferingSubscriber(new SerializedSubscriber(child));
        Subscriber<TOpening> openSubscriber = new Subscriber<TOpening>() {
            public void onNext(TOpening t) {
                bsub.startBuffer(t);
            }

            public void onError(Throwable e) {
                bsub.onError(e);
            }

            public void onCompleted() {
                bsub.onCompleted();
            }
        };
        child.add(openSubscriber);
        child.add(bsub);
        this.bufferOpening.unsafeSubscribe(openSubscriber);
        return bsub;
    }
}
