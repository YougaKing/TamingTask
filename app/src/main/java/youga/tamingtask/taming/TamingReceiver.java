package youga.tamingtask.taming;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import java.util.Calendar;

/**
 * @author YougaKingWu
 * @descibe ...
 * @date 2017/9/22 0022-16:29
 */

public class TamingReceiver extends BroadcastReceiver {

    private static final int GRAY_SERVICE_ID = Process.myPid();
    public static final String GRAY_WAKE_ACTION = "youga.tamingtask.taming.GRAY_WAKE_ACTION";
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
        } else if (GRAY_WAKE_ACTION.equals(intent.getAction())) {
            // TODO: 2017/9/22 0022
            Intent wakeIntent = new Intent(context, TamingNotifyService.class);
            context.startService(wakeIntent);
            TamingUtil.setTamingAlarmTask(context);
        }
    }

    public static void extra(Calendar calendar, Context context) {
        NoticeTask task = TamingUtil.obtainNoticeTask(context);
        calendar.add(Calendar.MINUTE, 1);
        task.hour = calendar.get(Calendar.HOUR_OF_DAY);
        task.minute = calendar.get(Calendar.MINUTE);
        TamingUtil.saveNoticeTask(context, task);
    }


    /**
     * 用于其他进程来唤醒UI进程用的Service
     */
    public static class TamingNotifyService extends Service {

        @Override
        public void onCreate() {
            Log.d(TAG, "TamingNotifyService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d(TAG, "TamingNotifyService -> onStartCommand");

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
                //API < 18 ，此方法能有效隐藏Notification上的图标
                startForeground(GRAY_SERVICE_ID, new Notification());
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Intent innerIntent = new Intent(this, TamingNotifyInnerService.class);
                startService(innerIntent);
                startForeground(GRAY_SERVICE_ID, new Notification());
            } else {
                // TODO: 2017/9/22 0022

            }
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, "TamingNotifyService -> onDestroy");
            super.onDestroy();
        }
    }


    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class TamingNotifyInnerService extends Service {

        @Override
        public void onCreate() {
            Log.d(TAG, "TamingNotifyInnerService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d(TAG, "TamingNotifyInnerService -> onStartCommand");
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, "TamingNotifyInnerService -> onDestroy");
            super.onDestroy();
        }
    }
}
