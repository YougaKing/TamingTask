package rxbind.view;

import android.support.annotation.NonNull;
import android.view.View;

import io.reactivex.Observable;

import static rxbind.internal.Preconditions.checkNotNull;

/**
 * Created by Youga on 2017/8/22.
 */

public class RxView {

    private RxView() {
        throw new AssertionError("No instances.");
    }

    public static Observable<Object> clicks(@NonNull View view) {
        checkNotNull(view, "view == null");
        return new ViewClickObservable(view);
    }

}
