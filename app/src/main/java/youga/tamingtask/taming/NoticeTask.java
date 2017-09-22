package youga.tamingtask.taming;


import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

/**
 * @author YougaKingWu
 * @descibe ...
 * @date 2017/9/12 0012-16:08
 */

public class NoticeTask implements Serializable {

    public final static int SUNDAY = 1;
    public final static int MONDAY = 2;
    public final static int TUESDAY = 3;
    public final static int WEDNESDAY = 4;
    public final static int THURSDAY = 5;
    public final static int FRIDAY = 6;
    public final static int SATURDAY = 7;
    public final static String defaultTime = "20:00-20:30";
    private static final long serialVersionUID = 6390061652465463005L;

    public int hour;
    public int minute;
    public int[] loopDays = new int[]{
            SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    };

    public NoticeTask() {
    }

    NoticeTask(String defaultTime) {
        String[] times = defaultTime.split("-");
        String[] startTime = times[0].split(":");
        String[] endTime = times[1].split(":");
        int startHour = Integer.valueOf(startTime[0]);
        int startMinute = Integer.valueOf(startTime[1]);


        int endHour = Integer.valueOf(endTime[0]);
        int endMinute = Integer.valueOf(endTime[1]);

        hour = startHour;
        minute = new Random().nextInt(endMinute);
    }

    public boolean containsWeek(int week) {
        Arrays.sort(loopDays);
        for (int i : loopDays) {
            if (i == week) return true;
        }
        return false;
    }

//    public String loopDaysString(Resources resources) {
//        StringBuilder builder = new StringBuilder();
//        if (containsWeek(NoticeTask.MONDAY)) {
//            builder.append(resources.getString(R.string.week_monday));
//            builder.append(",");
//        }
//        if (containsWeek(NoticeTask.TUESDAY)) {
//            builder.append(resources.getString(R.string.week_tuesday));
//            builder.append(",");
//        }
//        if (containsWeek(NoticeTask.WEDNESDAY)) {
//            builder.append(resources.getString(R.string.week_wednesday));
//            builder.append(",");
//        }
//        if (containsWeek(NoticeTask.THURSDAY)) {
//            builder.append(resources.getString(R.string.week_thursday));
//            builder.append(",");
//        }
//        if (containsWeek(NoticeTask.FRIDAY)) {
//            builder.append(resources.getString(R.string.week_friday));
//            builder.append(",");
//        }
//        if (containsWeek(NoticeTask.SATURDAY)) {
//            builder.append(resources.getString(R.string.week_saturday));
//            builder.append(",");
//        }
//        if (containsWeek(NoticeTask.SUNDAY)) {
//            builder.append(resources.getString(R.string.week_sunday));
//            builder.append(",");
//        }
//        if (builder.length() > 1) builder.replace(builder.length() - 1, builder.length(), "");
//        return builder.toString();
//    }


    boolean isLoop() {
        return loopDays.length > 0;
    }


    long triggerAtMillis() {
        if (!isLoop()) return 0L;
        Calendar calendar = Calendar.getInstance();
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(Calendar.HOUR_OF_DAY, hour);
        tempCalendar.set(Calendar.MINUTE, minute);
        tempCalendar.set(Calendar.SECOND, 0);
        if (loopDays.length == SATURDAY) {//everyday
            if (calendar.getTimeInMillis() <= tempCalendar.getTimeInMillis()) {
                return tempCalendar.getTimeInMillis();
            } else {//next day
                tempCalendar.add(Calendar.DATE, 1);
                return tempCalendar.getTimeInMillis();
            }
        } else {
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            if (containsWeek(week)) {//today
                if (calendar.getTimeInMillis() <= tempCalendar.getTimeInMillis()) {
                    return tempCalendar.getTimeInMillis();
                } else {//next week day
                    return nextTriggerAtMillis(calendar, tempCalendar);
                }
            } else {//next week day
                return nextTriggerAtMillis(calendar, tempCalendar);
            }
        }
    }

    private long nextTriggerAtMillis(Calendar calendar, Calendar tempCalendar) {
        for (int i = 0; i < SUNDAY; i++) {
            tempCalendar.add(Calendar.DATE, 1);
            int week = tempCalendar.get(Calendar.DAY_OF_WEEK);
            if (containsWeek(week)) {
                if (calendar.getTimeInMillis() <= tempCalendar.getTimeInMillis()) {
                    return tempCalendar.getTimeInMillis();
                } else {//next week day
                    return nextTriggerAtMillis(calendar, tempCalendar);
                }
            }
        }
        Log.e("NoticeTask", "I down know what happen ,general will not run here");
        return 0L;
    }

    @Override
    public String toString() {
        return "NoticeTask{" +
                "hour=" + hour +
                ", minute=" + minute +
                ", loopDays=" + Arrays.toString(loopDays) +
                '}';
    }
}
