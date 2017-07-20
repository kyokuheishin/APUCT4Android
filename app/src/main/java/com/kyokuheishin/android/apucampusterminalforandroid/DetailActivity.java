package com.kyokuheishin.android.apucampusterminalforandroid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {

    int seletedNo;
    private CampusTerminal ct;
    private CampusTerminal.ctMessage cm;
    private TextView bodyTextView;
    private HashMap<String, ArrayList<String>> mHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("selectedNo.") && bundle.containsKey("title")){
            setTitle(bundle.getString("title"));
            seletedNo = bundle.getInt("selectedNo.");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ct = ((CampusTerminal)getApplicationContext());
        cm = MainActivity.cm;

        bodyTextView = (TextView)findViewById(R.id.detail_body);

        getDetail getDetail = new getDetail();
        getDetail.execute();
    }

    private class getDetail extends AsyncTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (mHashMap != null){
                bodyTextView.setText(mHashMap.get("body").get(0));
            }
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                mHashMap = cm.ctGetMessageDetail(seletedNo);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


    }




}
