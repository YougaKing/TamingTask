package youga.tamingtask.taming;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author YougaKingWu
 * @descibe ...
 * @date 2017/9/12 0012-16:19
 */

public class TamingUtil {


    private static SharedPreferences getTamingPreferences(Context context) {
        return context.getSharedPreferences("tamingNoticeTask", Context.MODE_PRIVATE);

    }


    public static void init(Context context) {
        Context aContext = context.getApplicationContext();
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            JobScheduler jobScheduler = (JobScheduler) aContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//            jobScheduler.cancelAll();
//            JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(aContext.getPackageName(), JobSchedulerService.class.getName()))
//                    .setPeriodic(1000 * 60);
//            jobScheduler.schedule(builder.build());
//        } else {
//
//        }
        Intent service = new Intent(context, TamingService.class);
        context.startService(service);
    }


    public static NoticeTask obtainNoticeTask(Context context) {
        SharedPreferences preferences = getTamingPreferences(context);
        String string = preferences.getString(NoticeTask.class.getSimpleName(), null);
        if (TextUtils.isEmpty(string)) return new NoticeTask();
        byte[] base64Bytes = Base64.decode(string, Base64.DEFAULT);
        ByteArrayInputStream stream = new ByteArrayInputStream(base64Bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(stream);
            return (NoticeTask) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new NoticeTask();
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
}
