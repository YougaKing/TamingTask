package youga.tamingtask.taming;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author YougaKingWu
 * @descibe ...
 * @date 2017/9/25 0025-11:48
 */

public class WakeUpReceiver extends BroadcastReceiver {

    private static final String TAG = "WakeUpReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()--Action:" + intent.getAction());
        Intent service = new Intent(context, TamingService.class);
        service.setAction(intent.getAction());
        context.startService(service);
    }
}
