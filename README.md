# Android后台执行定时任务

##### Android 中的定时任务一般有两种实现方式，一种是使用 Java API 里提供的 Timer 类，一种是使用 Android 的 Alarm 机制。这两种方式在多数情况下都能实现类似的效果。

* Timer并不太适用于那些需要长期在后台运行的定时任务。为了能让电池更加耐用，每种手机都会有自己的休眠策略，Android 手机就会在长时间不操作的情况下自动让 CPU 进入到睡眠状态，这就有可能导致 Timer 中的定时任务无法正常运行。
* Alarm具有唤醒 CPU 的功能，即可以保证每次需要执行定时任务的时候 CPU 都能正常工作。

#### Alarm主要是借助AlarmManager类来实现，Android 官方文档对[AlarmManager](https://developer.android.com/reference/android/app/AlarmManager.html)解释如下
    该类提供对系统报警服务的访问。这些允许您安排您的应用程序在将来的某个时间运行。当报警熄灭时，已注册的意图由系统进行广播，如果尚未运行，则自动启动目标应用程序...
    ...
    注意：从API 19（KITKAT）开始，报警传递不准确：操作系统将移动报警，以最大限度地减少唤醒和电池使用。有新的API支持需要严格交付保证的应用程序
    ...
#### 设置定时任务，API大于19会有报警时间不准确，API大于23时Doze模式系统将尝试减少设备的唤醒频率推迟后台作业可能导致无法执行，我们需要根据版本分别适配。同时用BroadcastReceiver接受提醒并执行任务
        Intent alarmIntent = new Intent();
        alarmIntent.setAction(TamingReceiver.ALARM_WAKE_ACTION);
        PendingIntent operation = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(operation);
        long triggerAtMillis = task.triggerAtMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), operation);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), operation);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), operation);
        }
        //接受任务提醒
        public class TamingReceiver extends BroadcastReceiver {
        
            public static final String ALARM_WAKE_ACTION = "youga.tamingtask.taming.ALARM_WAKE_ACTION";
        
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive()--Action:" + intent.getAction());
            }
        }
#### 实际使用时AlarmManager无论在应用是否被关闭都能正常执行，但这仅限于原生Android系统。国产定制Android系统小米、魅族、华为等等都会对AlarmManager唤醒做限制，导致应用被关闭后无法正常执行。此时我们需要做的就是应用保活
#### 应用保活可以分为两个方面,一. 提供进程优先级，降低进程被杀死的概率，二. 在进程被杀死后，进行拉活
#### 提升进程优先级的方案可分为Activity 提升权限, Notification 提升权限
* Activity 提升权限有网传QQ一像素Activity方案,该方案涉及触摸时间拦截，各种状态监听操作难度复杂。
* Notification 提升权限，API小于18可以直接设置前台Notification。API大于18利用系统漏洞，两个Service共同设置同一个ID 的前台Notification，并关闭其中一个Service，Notification小时，另一个Service优先级不变,次漏洞API=24时被修复

       public void onCreate() {
            super.onCreate();
            Log.d(TAG, "onCreate()");

            Daemon.run(TamingService.this, TamingService.class, Daemon.INTERVAL_ONE_MINUTE);

            Intent service = new Intent(this, TamingGuardService.class);
            startService(service);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                startForeground(GRAY_SERVICE_ID, new Notification());
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Intent innerIntent = new Intent(this, TamingInnerService.class);
                startService(innerIntent);
                startForeground(GRAY_SERVICE_ID, new Notification());
            } else {
                // TODO: 2017/9/22 0022

            }
       }

#### 进程死后拉活的方案可分为系统广播拉活, 利用第三方应用广播拉活，利用系统Service机制拉活，利用Native进程拉活

* 广播接收器被管理软件、系统软件通过“自启管理”等功能禁用的场景无法接收到广播，从而无法自启，系统广播事件不可控，只能保证发生事件时拉活进程，但无法保证进程挂掉后立即拉活。第三方应用广播拉活需要反编译适配主流APP,太耗时复杂随着应用程序升级可能失效
* 同时我们启动另个守护GuardService监听这个Service状态，如果发现这个被异常Service关闭则启动这个Service,API小于21我们用AlarmManager重复监听，API大于21我们使用JobScheduler监听，然而JobScheduler在API大于24时Doze模式会因为电池优化而无法正常执行，我们需要忽略电池优化

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
        
       //忽略电池优化
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
           PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
           boolean ignoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(context.getPackageName());
           if (!ignoringBatteryOptimizations) {
               Intent dozeIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
               dozeIntent.setData(Uri.parse("package:" + context.getPackageName()));
               startActivity(dozeIntent);
           }
       }
    
* 利用Native进程拉活，我们采用开源的守护进程库。[Android-AppDaemon](https://github.com/Coolerfall/Android-AppDaemon)。该方案主要适用于 Android5.0 以下版本手机。该方案不受 forcestop 影响，被强制停止的应用依然可以被拉活，在 Android5.0 以下版本拉活效果非常好。对于 Android5.0 以上手机，系统虽然会将native进程内的所有进程都杀死，这里其实就是系统“依次”杀死进程时间与拉活逻辑执行时间赛跑的问题，如果可以跑的比系统逻辑快，依然可以有效拉起。

      @Override
      public void onCreate() {
          super.onCreate();
          Log.d(TAG, "onCreate()");
  
          Daemon.run(TamingService.this, TamingService.class, Daemon.INTERVAL_ONE_MINUTE);
      }
    
