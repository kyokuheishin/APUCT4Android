package com.kyokuheishin.android.apucampusterminalforandroid;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by kyokuheishin on 2018/1/9.
 */

public class JobSchedulerService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
