package youga.tamingtask.taming;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
    static final String TAG = "JobSchedulerService";

    public JobSchedulerService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
//        NoticeTask task = TamingUtil.obtainNoticeTask(this);
        Log.w(TAG, "onStartJob()--" + params.toString());
        TamingUtil.setTamingAlarmTask(this);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.w(TAG, "onStopJob()--" + params.toString());
        return false;
    }
}
