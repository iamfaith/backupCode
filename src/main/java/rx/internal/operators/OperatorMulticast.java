package rx.internal.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.observables.ConnectableObservable;
import rx.observers.Subscribers;
import rx.subjects.Subject;
import rx.subscriptions.Subscriptions;

public final class OperatorMulticast<T, R> extends ConnectableObservable<R> {
    final AtomicReference<Subject<? super T, ? extends R>> connectedSubject;
    final Object guard;
    Subscription guardedSubscription;
    final Observable<? extends T> source;
    final Func0<? extends Subject<? super T, ? extends R>> subjectFactory;
    Subscriber<T> subscription;
    final List<Subscriber<? super R>> waitingForConnect;

    class AnonymousClass1 implements OnSubscribe<R> {
        final /* synthetic */ AtomicReference val$connectedSubject;
        final /* synthetic */ Object val$guard;
        final /* synthetic */ List val$waitingForConnect;

        AnonymousClass1(Object obj, AtomicReference atomicReference, List list) {
            this.val$guard = obj;
            this.val$connectedSubject = atomicReference;
            this.val$waitingForConnect = list;
        }

        public void call(Subscriber<? super R> subscriber) {
            synchronized (this.val$guard) {
                if (this.val$connectedSubject.get() == null) {
                    this.val$waitingForConnect.add(subscriber);
                } else {
                    ((Subject) this.val$connectedSubject.get()).unsafeSubscribe(subscriber);
                }
            }
        }
    }

    public OperatorMulticast(Observable<? extends T> source, Func0<? extends Subject<? super T, ? extends R>> subjectFactory) {
        this(new Object(), new AtomicReference(), new ArrayList(), source, subjectFactory);
    }

    private OperatorMulticast(Object guard, AtomicReference<Subject<? super T, ? extends R>> connectedSubject, List<Subscriber<? super R>> waitingForConnect, Observable<? extends T> source, Func0<? extends Subject<? super T, ? extends R>> subjectFactory) {
        super(new AnonymousClass1(guard, connectedSubject, waitingForConnect));
        this.guard = guard;
        this.connectedSubject = connectedSubject;
        this.waitingForConnect = waitingForConnect;
        this.source = source;
        this.subjectFactory = subjectFactory;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void connect(Action1<? super Subscription> connection) {
        synchronized (this.guard) {
            if (this.subscription != null) {
                connection.call(this.guardedSubscription);
                return;
            }
            Subject<? super T, ? extends R> subject = (Subject) this.subjectFactory.call();
            this.subscription = Subscribers.from(subject);
            final AtomicReference<Subscription> gs = new AtomicReference();
            gs.set(Subscriptions.create(new Action0() {
                /* JADX WARNING: inconsistent code. */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void call() {
                    synchronized (OperatorMulticast.this.guard) {
                        if (OperatorMulticast.this.guardedSubscription == gs.get()) {
                            Subscription s = OperatorMulticast.this.subscription;
                            OperatorMulticast.this.subscription = null;
                            OperatorMulticast.this.guardedSubscription = null;
                            OperatorMulticast.this.connectedSubject.set(null);
                        }
                    }
                }
            }));
            this.guardedSubscription = (Subscription) gs.get();
            for (final Subscriber<? super R> s : this.waitingForConnect) {
                subject.unsafeSubscribe(new Subscriber<R>(s) {
                    public void onNext(R t) {
                        s.onNext(t);
                    }

                    public void onError(Throwable e) {
                        s.onError(e);
                    }

                    public void onCompleted() {
                        s.onCompleted();
                    }
                });
            }
            this.waitingForConnect.clear();
            this.connectedSubject.set(subject);
        }
    }
}
