package rx.internal.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observers.Subscribers;

public final class OperatorToMultimap<T, K, V> implements Operator<Map<K, Collection<V>>, T> {
    final Func1<? super K, ? extends Collection<V>> collectionFactory;
    final Func1<? super T, ? extends K> keySelector;
    private final Func0<? extends Map<K, Collection<V>>> mapFactory;
    final Func1<? super T, ? extends V> valueSelector;

    public static final class DefaultMultimapCollectionFactory<K, V> implements Func1<K, Collection<V>> {
        public Collection<V> call(K k) {
            return new ArrayList();
        }
    }

    public static final class DefaultToMultimapFactory<K, V> implements Func0<Map<K, Collection<V>>> {
        public Map<K, Collection<V>> call() {
            return new HashMap();
        }
    }

    public OperatorToMultimap(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector) {
        this(keySelector, valueSelector, new DefaultToMultimapFactory(), new DefaultMultimapCollectionFactory());
    }

    public OperatorToMultimap(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector, Func0<? extends Map<K, Collection<V>>> mapFactory) {
        this(keySelector, valueSelector, mapFactory, new DefaultMultimapCollectionFactory());
    }

    public OperatorToMultimap(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector, Func0<? extends Map<K, Collection<V>>> mapFactory, Func1<? super K, ? extends Collection<V>> collectionFactory) {
        this.keySelector = keySelector;
        this.valueSelector = valueSelector;
        this.mapFactory = mapFactory;
        this.collectionFactory = collectionFactory;
    }

    public Subscriber<? super T> call(final Subscriber<? super Map<K, Collection<V>>> subscriber) {
        try {
            final Map<K, Collection<V>> fLocalMap = (Map) this.mapFactory.call();
            return new Subscriber<T>(subscriber) {
                private Map<K, Collection<V>> map = fLocalMap;

                public void onStart() {
                    request(Long.MAX_VALUE);
                }

                public void onNext(T v) {
                    try {
                        K key = OperatorToMultimap.this.keySelector.call(v);
                        V value = OperatorToMultimap.this.valueSelector.call(v);
                        Collection<V> collection = (Collection) this.map.get(key);
                        if (collection == null) {
                            try {
                                collection = (Collection) OperatorToMultimap.this.collectionFactory.call(key);
                                this.map.put(key, collection);
                            } catch (Throwable ex) {
                                Exceptions.throwOrReport(ex, subscriber);
                                return;
                            }
                        }
                        collection.add(value);
                    } catch (Throwable ex2) {
                        Exceptions.throwOrReport(ex2, subscriber);
                    }
                }

                public void onError(Throwable e) {
                    this.map = null;
                    subscriber.onError(e);
                }

                public void onCompleted() {
                    Map<K, Collection<V>> map0 = this.map;
                    this.map = null;
                    subscriber.onNext(map0);
                    subscriber.onCompleted();
                }
            };
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            subscriber.onError(ex);
            Subscriber<? super T> parent = Subscribers.empty();
            parent.unsubscribe();
            return parent;
        }
    }
}
