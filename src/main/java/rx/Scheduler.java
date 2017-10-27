package rx;

import java.util.concurrent.TimeUnit;
import rx.functions.Action0;
import rx.subscriptions.MultipleAssignmentSubscription;

public abstract class Scheduler {
    static final long CLOCK_DRIFT_TOLERANCE_NANOS = TimeUnit.MINUTES.toNanos(Long.getLong("rx.scheduler.drift-tolerance", 15).longValue());

    public static abstract class Worker implements Subscription {
        public abstract Subscription schedule(Action0 action0);

        public abstract Subscription schedule(Action0 action0, long j, TimeUnit timeUnit);

        public Subscription schedulePeriodically(Action0 action, long initialDelay, long period, TimeUnit unit) {
            final long periodInNanos = unit.toNanos(period);
            final long firstNowNanos = TimeUnit.MILLISECONDS.toNanos(now());
            final long firstStartInNanos = firstNowNanos + unit.toNanos(initialDelay);
            final MultipleAssignmentSubscription mas = new MultipleAssignmentSubscription();
            final Action0 action0 = action;
            Action0 recursiveAction = new Action0() {
                long count;
                long lastNowNanos = firstNowNanos;
                long startInNanos = firstStartInNanos;

                public void call() {
                    if (!mas.isUnsubscribed()) {
                        long nextTick;
                        action0.call();
                        long nowNanos = TimeUnit.MILLISECONDS.toNanos(Worker.this.now());
                        long j;
                        long j2;
                        if (Scheduler.CLOCK_DRIFT_TOLERANCE_NANOS + nowNanos < this.lastNowNanos || nowNanos >= (this.lastNowNanos + periodInNanos) + Scheduler.CLOCK_DRIFT_TOLERANCE_NANOS) {
                            nextTick = nowNanos + periodInNanos;
                            j = periodInNanos;
                            j2 = this.count + 1;
                            this.count = j2;
                            this.startInNanos = nextTick - (j * j2);
                        } else {
                            j = this.startInNanos;
                            j2 = this.count + 1;
                            this.count = j2;
                            nextTick = j + (j2 * periodInNanos);
                        }
                        this.lastNowNanos = nowNanos;
                        mas.set(Worker.this.schedule(this, nextTick - nowNanos, TimeUnit.NANOSECONDS));
                    }
                }
            };
            MultipleAssignmentSubscription s = new MultipleAssignmentSubscription();
            mas.set(s);
            s.set(schedule(recursiveAction, initialDelay, unit));
            return mas;
        }

        public long now() {
            return System.currentTimeMillis();
        }
    }

    public abstract Worker createWorker();

    public long now() {
        return System.currentTimeMillis();
    }
}
