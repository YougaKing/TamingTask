package youga.tamingtask.taming;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * @author YougaKingWu
 * @descibe ...
 * @date 2017/9/22 0022-16:29
 */

public class TamingReceiver extends BroadcastReceiver {

    public static final String ALARM_WAKE_ACTION = "youga.tamingtask.taming.ALARM_WAKE_ACTION";
    private static final String TAG = "TamingReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()--Action:" + intent.getAction());

        if (ALARM_WAKE_ACTION.equals(intent.getAction())) {
            Calendar calendar = Calendar.getInstance();
            TamingUtil.buildNotification(context, calendar.get(Calendar.DAY_OF_WEEK));

            extra(calendar, context);

            TamingUtil.setTamingAlarmTask(context);
            Log.d(TAG, TamingUtil.mFormat.format(Calendar.getInstance().getTime()));
        }
    }

    public static void extra(Calendar calendar, Context context) {
        NoticeTask task = TamingUtil.obtainNoticeTask(context);
        calendar.add(Calendar.MINUTE, 1);
        task.hour = calendar.get(Calendar.HOUR_OF_DAY);
        task.minute = calendar.get(Calendar.MINUTE);
        TamingUtil.saveNoticeTask(context, task);
    }


}
