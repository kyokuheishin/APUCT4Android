package com.kyokuheishin.android.apucampusterminalforandroid;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class eggActivity extends AppCompatActivity {

    private PasswordStore passwordStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button button = (Button)findViewById(R.id.notification_tester);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordStore = new PasswordStore(getApplicationContext());
                JobSchedulerService.loginService loginService = new JobSchedulerService().new loginService(passwordStore.get().get(0),passwordStore.get().get(1),getApplicationContext());
                loginService.execute();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
