package io.reactivex;


import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * @author YougaKingWu
 * @descibe ...
 * @date 2017/9/18 0018-10:57
 */

public abstract class Observable<T> {

    public abstract Observable<Object> throttleFirst(long skipDuration, TimeUnit unit);

    public final void subscribe(Consumer<T> consumer) {
        try {
            subscribeActual(consumer);
        } catch (NullPointerException e) { // NOPMD
            throw e;
        } catch (Throwable e) {
            NullPointerException npe = new NullPointerException("Actually not, but can't throw other exceptions due to RS");
            npe.initCause(e);
            throw npe;
        }
    }

    protected abstract void subscribeActual(Consumer<T> consumer);
}
