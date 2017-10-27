package rx.internal.producers;

import java.util.ArrayList;
import java.util.List;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.internal.operators.BackpressureUtils;

public final class ProducerObserverArbiter<T> implements Producer, Observer<T> {
    static final Producer NULL_PRODUCER = new Producer() {
        public void request(long n) {
        }
    };
    final Subscriber<? super T> child;
    Producer currentProducer;
    boolean emitting;
    volatile boolean hasError;
    Producer missedProducer;
    long missedRequested;
    Object missedTerminal;
    List<T> queue;
    long requested;

    public ProducerObserverArbiter(Subscriber<? super T> child) {
        this.child = child;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onNext(T t) {
        synchronized (this) {
            if (this.emitting) {
                List<T> q = this.queue;
                if (q == null) {
                    q = new ArrayList(4);
                    this.queue = q;
                }
                q.add(t);
            }
        }
    }

    public void onError(Throwable e) {
        boolean emit;
        synchronized (this) {
            if (this.emitting) {
                this.missedTerminal = e;
                emit = false;
            } else {
                this.emitting = true;
                emit = true;
            }
        }
        if (emit) {
            this.child.onError(e);
        } else {
            this.hasError = true;
        }
    }

    public void onCompleted() {
        synchronized (this) {
            if (this.emitting) {
                this.missedTerminal = Boolean.valueOf(true);
                return;
            }
            this.emitting = true;
            this.child.onCompleted();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void request(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("n >= 0 required");
        } else if (n != 0) {
            synchronized (this) {
                if (this.emitting) {
                    this.missedRequested += n;
                    return;
                }
                this.emitting = true;
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setProducer(Producer p) {
        synchronized (this) {
            if (this.emitting) {
                if (p == null) {
                    p = NULL_PRODUCER;
                }
                this.missedProducer = p;
                return;
            }
            this.emitting = true;
        }
    }

    void emitLoop() {
        Subscriber<? super T> c = this.child;
        long toRequest = 0;
        Producer requestFrom = null;
        while (true) {
            boolean quit = false;
            synchronized (this) {
                long localRequested = this.missedRequested;
                Producer localProducer = this.missedProducer;
                Boolean localTerminal = this.missedTerminal;
                List<T> q = this.queue;
                if (localRequested == 0 && localProducer == null && q == null && localTerminal == null) {
                    this.emitting = false;
                    quit = true;
                } else {
                    this.missedRequested = 0;
                    this.missedProducer = null;
                    this.queue = null;
                    this.missedTerminal = null;
                }
            }
            if (quit) {
                break;
            }
            boolean empty = q == null || q.isEmpty();
            if (localTerminal != null) {
                if (localTerminal != Boolean.TRUE) {
                    c.onError((Throwable) localTerminal);
                    return;
                } else if (empty) {
                    c.onCompleted();
                    return;
                }
            }
            long e = 0;
            if (q != null) {
                for (T v : q) {
                    if (!c.isUnsubscribed()) {
                        if (this.hasError) {
                            continue;
                            break;
                        }
                        try {
                            c.onNext(v);
                        } catch (Throwable ex) {
                            Exceptions.throwOrReport(ex, c, v);
                            return;
                        }
                    }
                    return;
                }
                e = 0 + ((long) q.size());
            }
            long r = this.requested;
            if (r != Long.MAX_VALUE) {
                long u;
                if (localRequested != 0) {
                    u = r + localRequested;
                    if (u < 0) {
                        u = Long.MAX_VALUE;
                    }
                    r = u;
                }
                if (!(e == 0 || r == Long.MAX_VALUE)) {
                    u = r - e;
                    if (u < 0) {
                        throw new IllegalStateException("More produced than requested");
                    }
                    r = u;
                }
                this.requested = r;
            }
            if (localProducer == null) {
                Producer p = this.currentProducer;
                if (p != null && localRequested != 0) {
                    toRequest = BackpressureUtils.addCap(toRequest, localRequested);
                    requestFrom = p;
                }
            } else if (localProducer == NULL_PRODUCER) {
                this.currentProducer = null;
            } else {
                this.currentProducer = localProducer;
                if (r != 0) {
                    toRequest = BackpressureUtils.addCap(toRequest, r);
                    requestFrom = localProducer;
                }
            }
        }
        if (toRequest != 0 && requestFrom != null) {
            requestFrom.request(toRequest);
        }
    }
}
