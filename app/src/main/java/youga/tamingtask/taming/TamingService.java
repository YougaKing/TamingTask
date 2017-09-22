package youga.tamingtask.taming;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.os.Process;


import com.coolerfall.daemon.Daemon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TamingService extends Service {


    private static final int GRAY_SERVICE_ID = Process.myPid();
    private static final int MSG = 1;
    private static final String TAG = "TamingService";
    private static final long ALARM_INTERVAL = 1000 * 10;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd/HH-mm", Locale.CHINA);

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            NoticeTask task = TamingUtil.obtainNoticeTask(TamingService.this);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG), 1000 * 59);


            Calendar calendar = Calendar.getInstance();
            int week = calendar.get(Calendar.DAY_OF_WEEK);

            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), task.hour, task.minute);
            tempCalendar.add(Calendar.MINUTE, -1);
            SharedPreferences preferences = TamingUtil.getTamingPreferences(TamingService.this);
            boolean isNotify = preferences.getBoolean(mFormat.format(tempCalendar.getTime()), false);

            if (task.containsWeek(week) && calendar.getTimeInMillis() >= tempCalendar.getTimeInMillis() && !isNotify) {
                Log.e(TAG, "开始通知...");

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(mFormat.format(tempCalendar.getTime()), true);
                editor.apply();

                if (task.isLoop()) TamingUtil.buildNotification(TamingService.this, week);

                TamingReceiver.extra(calendar, TamingService.this);
            }

            Log.w(TAG, "calendar:" + mFormat.format(calendar.getTime()) + "-week:" + week + "\ntask:" + task + "\ntempCalendar:" + mFormat.format(tempCalendar.getTime()) + "\nisNotify:" + isNotify);
            return false;
        }
    });


    public TamingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        Daemon.run(TamingService.this, TamingService.class, Daemon.INTERVAL_ONE_MINUTE);
//        startTimeTask();
//        mHandler.sendMessage(mHandler.obtainMessage(MSG));
    }

    //@IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            //API < 18 ，此方法能有效隐藏Notification上的图标
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Intent innerIntent = new Intent(this, TamingInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else {
            // TODO: 2017/9/22 0022

        }

        //发送唤醒广播来促使挂掉的UI进程重新启动起来
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent();
        alarmIntent.setAction(TamingReceiver.GRAY_WAKE_ACTION);
        PendingIntent operation = PendingIntent.getBroadcast(this, 121, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ALARM_INTERVAL, operation);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class TamingInnerService extends Service {

        @Override
        public void onCreate() {
            Log.d(TAG, "TamingInnerService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d(TAG, "TamingInnerService -> onStartCommand");
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
            Log.d(TAG, "TamingInnerService -> onDestroy");
            super.onDestroy();
        }
    }

}
