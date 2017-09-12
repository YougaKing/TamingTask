package youga.tamingtask.taming;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class TamingService extends Service {

    private static final int MSG = 1;
    private static final String TAG = "TamingService";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            NoticeTask task = TamingUtil.obtainNoticeTask(TamingService.this);
            mHandler.sendMessageDelayed(obtainMessage(MSG), 1000 * 59);


            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int week = calendar.get(Calendar.DAY_OF_WEEK);

            if (task.isDefault && task.hour == 0) {
                String[] times = task.defaultTime.split("-");
                String[] startTime = times[0].split(":");
                String[] endTime = times[1].split(":");
                int startHour = Integer.valueOf(startTime[0]);
                int startMinute = Integer.valueOf(startTime[1]);


                int endHour = Integer.valueOf(endTime[0]);
                int endMinute = Integer.valueOf(endTime[1]);

                task.hour = startHour;
                task.minute = new Random().nextInt(endMinute);

                TamingUtil.saveNoticeTask(TamingService.this, task);
            }

            if (task.containsWeek(week) && hour == task.hour && minute == task.minute) {
                Log.e(TAG, "开始通知...");

                if (task.isDefault) {
                    task.hour = 0;
                    TamingUtil.saveNoticeTask(TamingService.this, task);
                }


            }

            Log.w(TAG, calendar.getTime().toString() + "\nhour:" + hour + "-minute:" + minute + "-week:" + week + "\n" + task);

        }
    };


    public TamingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //@IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
