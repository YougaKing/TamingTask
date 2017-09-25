package youga.tamingtask.taming;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import youga.tamingtask.ExerciseTamingActivity;
import youga.tamingtask.R;

/**
 * @author YougaKingWu
 * @descibe ...
 * @date 2017/9/12 0012-16:19
 */

public class TamingUtil {

    public static final SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd/HH-mm-ss", Locale.CHINA);

    public static SharedPreferences getTamingPreferences(Context context) {
        return context.getSharedPreferences("tamingNoticeTask", Context.MODE_PRIVATE);

    }


    public static void init(Context context) {
        Intent intent = new Intent(context, TamingService.class);
        context.startService(intent);

        setTamingAlarmTask(context);
    }

    public static void setTamingAlarmTask(Context context) {
        Intent alarmIntent = new Intent();
        alarmIntent.setAction(TamingReceiver.ALARM_WAKE_ACTION);
        PendingIntent operation = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(operation);
        NoticeTask task = obtainNoticeTask(context);
        long triggerAtMillis = task.triggerAtMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, operation);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, operation);
        }
        Log.d("loopAlarmTask", "triggerAtMillis:" + triggerAtMillis + "-->" + mFormat.format(new Date(triggerAtMillis)) +
                "\nTimeInMillis:" + Calendar.getInstance().getTimeInMillis() + "-->" + mFormat.format(Calendar.getInstance().getTime()));
    }

    public static NoticeTask obtainNoticeTask(Context context) {
        SharedPreferences preferences = getTamingPreferences(context);
        String string = preferences.getString(NoticeTask.class.getSimpleName(), null);
        if (TextUtils.isEmpty(string)) return new NoticeTask(NoticeTask.defaultTime);
        byte[] base64Bytes = Base64.decode(string, Base64.DEFAULT);
        ByteArrayInputStream stream = new ByteArrayInputStream(base64Bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(stream);
            return (NoticeTask) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new NoticeTask(NoticeTask.defaultTime);
        } finally {
            try {
                stream.close();
                if (ois != null) ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveNoticeTask(Context context, NoticeTask task) {
        SharedPreferences.Editor editor = getTamingPreferences(context).edit();
        ByteArrayOutputStream toByte = new ByteArrayOutputStream();
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(toByte);
            stream.writeObject(task);
            String string = new String(Base64.encode(toByte.toByteArray(), Base64.DEFAULT));
            editor.putString(NoticeTask.class.getSimpleName(), string);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) stream.close();
                toByte.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void buildNotification(Context context, int week) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, ExerciseTamingActivity.class), 0);

        String text;
//        switch (week) {
//            case NoticeTask.MONDAY:
//                String[] strings = context.getResources().getStringArray(R.array.weeks_monday);
//                text = strings[new Random().nextInt(strings.length)];
//                break;
//            case NoticeTask.TUESDAY:
//                strings = context.getResources().getStringArray(R.array.weeks_tuesday);
//                text = strings[new Random().nextInt(strings.length)];
//                break;
//            case NoticeTask.WEDNESDAY:
//                strings = context.getResources().getStringArray(R.array.weeks_wednesday);
//                text = strings[new Random().nextInt(strings.length)];
//                break;
//            case NoticeTask.THURSDAY:
//                strings = context.getResources().getStringArray(R.array.weeks_thursday);
//                text = strings[new Random().nextInt(strings.length)];
//                break;
//            case NoticeTask.FRIDAY:
//                strings = context.getResources().getStringArray(R.array.weeks_friday);
//                text = strings[new Random().nextInt(strings.length)];
//                break;
//            case NoticeTask.SATURDAY:
//                strings = context.getResources().getStringArray(R.array.weeks_saturday);
//                text = strings[new Random().nextInt(strings.length)];
//                break;
//            case NoticeTask.SUNDAY:
//                strings = context.getResources().getStringArray(R.array.weeks_sunday);
//                text = strings[new Random().nextInt(strings.length)];
//                break;
//            default:
//                text = context.getResources().getString(R.string.app_name);
//                break;
//        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(mFormat.format(Calendar.getInstance().getTime()))
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);

        manager.notify(10, builder.build());
    }


    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations(Context context) {
        String packageName = context.getPackageName();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isIgnoringBatteryOptimizations(packageName);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public static void isIgnoreBatteryOption(Activity activity, int resultCode) {
        try {
            Intent intent = new Intent();
            String packageName = activity.getPackageName();
            PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                activity.startActivityForResult(intent, resultCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
