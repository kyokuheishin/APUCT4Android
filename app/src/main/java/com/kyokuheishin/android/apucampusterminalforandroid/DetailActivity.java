package com.kyokuheishin.android.apucampusterminalforandroid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.Canvas;
import android.util.AttributeSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {

    int seletedNo;
    private CampusTerminal ct;
    private CampusTerminal.ctMessage cm;
    private LinearLayout urlLinearLayout;
    private TextView bodyTextView,receivingTimeTextView,viewingPeriodTextView,sourceTextView;
    private ListView urlListView,fileListView;
    private android.view.ViewGroup.LayoutParams params;
    private int old_count = 0;
    private HashMap<String, ArrayList<String>> mHashMap;
    private BaseAdapter mBaseAdapter;

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
        receivingTimeTextView = (TextView)findViewById(R.id.detail_receiving_time);
        viewingPeriodTextView = (TextView)findViewById(R.id.detail_viewing_period);
        sourceTextView = (TextView)findViewById(R.id.detail_source);

        urlLinearLayout = (LinearLayout) findViewById(R.id.content_detail_url) ;
//        urlListView = (ListView)findViewById(R.id.detail_url_list);

        getDetail getDetail = new getDetail();
        getDetail.execute();
    }

    private void addHyperlinkTextView (HashMap<String, ArrayList<String>> hashMap,
                                       LinearLayout linearLayout){
        ArrayList<String> LinkTitles = hashMap.get("otherInformationLinkTitle");
        ArrayList<String> Links = hashMap.get("otherInformationLink");
            for (int i=0;i<LinkTitles.size();i++){
                TextView textView = new TextView(this);
//                textView.setText(LinkTitles.get(i));
                textView.setText(Html.fromHtml
                        ("<a href=\""+Links.get(i)+"\">"+LinkTitles.get(i)+"</a>"));
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                linearLayout.addView(textView);
            }
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
                receivingTimeTextView.setText(mHashMap.get("otherInformationContent").get(0));
                viewingPeriodTextView.setText(mHashMap.get("otherInformationContent").get(1));
                sourceTextView.setText(mHashMap.get("otherInformationContent").get(2));
                addHyperlinkTextView(mHashMap,urlLinearLayout);
//                mBaseAdapter = new DetailUrlListViewAdapater(mHashMap.get("otherInformationLinkTitle"),mHashMap.get("otherInformationLink"),urlListView,DetailActivity.this);
//                urlListView.setAdapter(mBaseAdapter);
//                urlListView.setVisibility(View.VISIBLE);
//                System.out.println(urlListView.getCount());
//                System.out.println(urlListView.getChildAt(1));
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
