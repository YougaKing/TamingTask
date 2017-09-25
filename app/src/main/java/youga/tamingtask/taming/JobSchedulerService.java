package youga.tamingtask.taming;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
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
        Log.w(TAG, "onStartJob()--" + params.toString());
        Intent intent = new Intent(this, TamingService.class);
        intent.setAction(TamingService.GUARD_INTERVAL_ACTION);
        startService(intent);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.w(TAG, "onStopJob()--" + params.toString());
        return false;
    }
}
