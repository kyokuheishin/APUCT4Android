package com.kyokuheishin.android.apucampusterminalforandroid;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kyokuheishin on 2018/1/9.
 */

public class JobSchedulerService extends JobService {


    private Context context;
    private PasswordStore passwordStore;
    private LatestMessageStore latestMessageStore;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        passwordStore = new PasswordStore(context);

    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        final JobParameters parameters = jobParameters;
        Log.d("CT_CHECKER_TASK","Start checking...");
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                CTService.loginService loginService = new CTService.loginService(passwordStore.get().get(0),passwordStore.get().get(1),context);
                loginService.execute();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.d("CT_CHECKER_TASK","Finish checking...");
                jobFinished(parameters,true);
            }

        };

        task.execute();

        return true;
    }



    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d("CT_CHECKER_TASK","End checking...");
        return false;
    }




}


