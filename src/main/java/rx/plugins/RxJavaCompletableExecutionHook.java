package rx.plugins;

import rx.Completable;
import rx.Completable.CompletableOnSubscribe;
import rx.Completable.CompletableOperator;
import rx.annotations.Experimental;

@Experimental
public abstract class RxJavaCompletableExecutionHook {
    public CompletableOnSubscribe onCreate(CompletableOnSubscribe f) {
        return f;
    }

    public CompletableOnSubscribe onSubscribeStart(Completable completableInstance, CompletableOnSubscribe onSubscribe) {
        return onSubscribe;
    }

    public Throwable onSubscribeError(Throwable e) {
        return e;
    }

    public CompletableOperator onLift(CompletableOperator lift) {
        return lift;
    }
}
