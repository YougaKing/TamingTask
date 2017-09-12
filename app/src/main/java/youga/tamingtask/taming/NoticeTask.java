package youga.tamingtask.taming;


import java.io.Serializable;
import java.util.Arrays;

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
    private static final long serialVersionUID = 6390061652465463005L;

    public int deviationMinute = 5;
    public int hour;
    public int minute;
    public String defaultTime = "20:00-20:30";
    public int[] loopDays = new int[]{
            SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    };
    public boolean isDefault = true;

    public NoticeTask() {

    }

    public boolean containsWeek(int week) {
        for (int i : loopDays) {
            if (i == week) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "NoticeTask{" +
                "deviationMinute=" + deviationMinute +
                ", hour=" + hour +
                ", minute=" + minute +
                ", defaultTime='" + defaultTime + '\'' +
                ", loopDays=" + Arrays.toString(loopDays) +
                '}';
    }
}
