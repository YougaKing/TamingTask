package youga.tamingtask.taming;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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


    public static final String GUARD_INTERVAL_ACTION = "youga.tamingtask.taming.GUARD_INTERVAL_ACTION";
    private static final int GRAY_SERVICE_ID = Process.myPid();
    private static final int MSG = 1;
    private static final String TAG = "TamingService";
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

        Intent service = new Intent(this, TamingGuardService.class);
        startService(service);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //API < 18 ，此方法能有效隐藏Notification上的图标
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Intent innerIntent = new Intent(this, TamingInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else {
            // TODO: 2017/9/22 0022

        }

        getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), TamingGuardService.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

//        startTimeTask();
//        mHandler.sendMessage(mHandler.obtainMessage(MSG));
    }

    //@IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand()-->" + (intent != null ? intent.getAction() : "intent=null"));

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
