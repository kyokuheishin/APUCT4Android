package com.kyokuheishin.android.apucampusterminalforandroid;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class eggActivity extends AppCompatActivity {

    private PasswordStore passwordStore;
    JobScheduler jobScheduler;
    JobInfo.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Easter Eggs");
        setSupportActionBar(toolbar);
        jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        builder = new JobInfo.Builder(0,new ComponentName(this,JobSchedulerService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPeriodic(60*1000);
        builder.setPersisted(true);



        Button buttonNotificationTester = findViewById(R.id.notification_tester);
        buttonNotificationTester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordStore = new PasswordStore(getApplicationContext());
                CTService.loginService loginService = new CTService.loginService(passwordStore.get().get(0),passwordStore.get().get(1),getApplicationContext());
                loginService.execute();
            }
        });

        Button buttonJobSchedulerTester = findViewById(R.id.job_scheduler_tester);
        buttonJobSchedulerTester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CT_CHECKER_TASK","Button pressed!");
                jobScheduler.schedule(builder.build());
            }
        });

        Button buttonCancelAllJobs = findViewById(R.id.job_scheduler_cancel_all);
        buttonCancelAllJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CT_CHECKER_TASK","All jobs has been cancelled!");
                jobScheduler.cancelAll();
            }
        });

        Button buttonOpenSettings = findViewById(R.id.open_settings);
        buttonOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(eggActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
