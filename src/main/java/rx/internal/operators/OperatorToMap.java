package rx.internal.operators;

import java.util.HashMap;
import java.util.Map;
import rx.Observable.Operator;
import rx.Observer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observers.Subscribers;

public final class OperatorToMap<T, K, V> implements Operator<Map<K, V>, T> {
    final Func1<? super T, ? extends K> keySelector;
    private final Func0<? extends Map<K, V>> mapFactory;
    final Func1<? super T, ? extends V> valueSelector;

    public static final class DefaultToMapFactory<K, V> implements Func0<Map<K, V>> {
        public Map<K, V> call() {
            return new HashMap();
        }
    }

    public OperatorToMap(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector) {
        this(keySelector, valueSelector, new DefaultToMapFactory());
    }

    public OperatorToMap(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector, Func0<? extends Map<K, V>> mapFactory) {
        this.keySelector = keySelector;
        this.valueSelector = valueSelector;
        this.mapFactory = mapFactory;
    }

    public Subscriber<? super T> call(final Subscriber<? super Map<K, V>> subscriber) {
        try {
            final Map<K, V> fLocalMap = (Map) this.mapFactory.call();
            return new Subscriber<T>(subscriber) {
                private Map<K, V> map = fLocalMap;

                public void onStart() {
                    request(Long.MAX_VALUE);
                }

                public void onNext(T v) {
                    try {
                        this.map.put(OperatorToMap.this.keySelector.call(v), OperatorToMap.this.valueSelector.call(v));
                    } catch (Throwable ex) {
                        Exceptions.throwOrReport(ex, subscriber);
                    }
                }

                public void onError(Throwable e) {
                    this.map = null;
                    subscriber.onError(e);
                }

                public void onCompleted() {
                    Map<K, V> map0 = this.map;
                    this.map = null;
                    subscriber.onNext(map0);
                    subscriber.onCompleted();
                }
            };
        } catch (Throwable ex) {
            Exceptions.throwOrReport(ex, (Observer) subscriber);
            Subscriber<? super T> parent = Subscribers.empty();
            parent.unsubscribe();
            return parent;
        }
    }
}
