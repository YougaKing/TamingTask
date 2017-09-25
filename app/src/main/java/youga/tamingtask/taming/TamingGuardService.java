package youga.tamingtask.taming;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * @author YougaKingWu
 * @descibe ...
 * @date 2017/9/25 0025-11:20
 */

public class TamingGuardService extends Service {

    private static final int GRAY_SERVICE_ID = Process.myPid();
    private static final String TAG = "TamingGuardService";
    private static final long ALARM_INTERVAL = 1000 * 10;
    private static final long JOB_INTERVAL = 1000 * 10;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "TamingGuardService -> onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "TamingGuardService -> onStartCommand");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //API < 18 ，此方法能有效隐藏Notification上的图标
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Intent innerIntent = new Intent(this, TamingGuardInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else {
            // TODO: 2017/9/22 0022

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(0, new ComponentName(getPackageName(), JobSchedulerService.class.getName()));
            builder.setPeriodic(JOB_INTERVAL); //每隔60秒运行一次
            //Android 7.0+ 增加了一项针对 JobScheduler 的新限制，最小间隔只能是下面设定的数字
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setPeriodic(JobInfo.getMinPeriodMillis(), JobInfo.getMinFlexMillis());
            }
            builder.setRequiresCharging(true);
            builder.setPersisted(true);  //设置设备重启后，是否重新执行任务
            builder.setRequiresDeviceIdle(true);

            if (jobScheduler.schedule(builder.build()) <= 0) {
                Log.w("init", "jobScheduler.schedule something goes wrong");
            }
        } else {
            //发送唤醒广播来促使挂掉的UI进程重新启动起来
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, TamingService.class);
            alarmIntent.setAction(TamingService.GUARD_INTERVAL_ACTION);
            PendingIntent operation = PendingIntent.getService(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ALARM_INTERVAL, operation);
        }


        //守护 Service 组件的启用状态, 使其不被 MAT 等工具禁用
        getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), TamingService.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        return START_STICKY;
    }


    public static class TamingGuardInnerService extends Service {

        @Override
        public void onCreate() {
            Log.d(TAG, "TamingGuardInnerService -> onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.d(TAG, "TamingGuardInnerService -> onStartCommand");
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
            Log.d(TAG, "TamingGuardInnerService -> onDestroy");
            super.onDestroy();
        }
    }
}
