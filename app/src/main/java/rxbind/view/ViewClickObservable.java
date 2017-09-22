package rxbind.view;

import android.view.View;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import static rxbind.internal.Preconditions.checkMainThread;

/**
 * Created by Youga on 2017/8/22.
 */

class ViewClickObservable extends Observable<Object> {

    private final View view;
    private Listener listener;

    ViewClickObservable(View view) {
        this.view = view;
    }

    @Override
    public Observable<Object> throttleFirst(long skipDuration, TimeUnit unit) {
        if (!checkMainThread()) {
            return this;
        }
        listener = new Listener(skipDuration, unit);
        view.setOnClickListener(listener);
        return this;
    }

    @Override
    protected void subscribeActual(Consumer<Object> consumer) {
        listener.setConsumer(consumer);
    }

    private static final class Listener implements View.OnClickListener {
        private long lastClickTime, minClickTime;
        private Consumer<Object> consumer;

        Listener(long skipDuration, TimeUnit unit) {
            if (unit == TimeUnit.MILLISECONDS) {
                minClickTime = skipDuration;
            } else if (unit == TimeUnit.SECONDS) {
                minClickTime = skipDuration * 1000;
            } else {
                throw new IllegalArgumentException("TimeUnit not support");
            }
        }

        @Override
        public void onClick(View v) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > minClickTime) {
                lastClickTime = currentTime;
                try {
                    consumer.accept(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        void setConsumer(Consumer<Object> consumer) {
            this.consumer = consumer;
        }
    }
}
