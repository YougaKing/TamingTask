package rxbind.internal;

import android.os.Looper;

/**
 * Created by Youga on 2017/8/23.
 */

public class Preconditions {
    public static void checkNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }

    public static boolean checkMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            return false;
        }
        return true;
    }

    private Preconditions() {
        throw new AssertionError("No instances.");
    }
}
