package com.kyokuheishin.android.apucampusterminalforandroid;

import android.app.DownloadManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.graphics.Canvas;
import android.util.AttributeSet;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.kyokuheishin.android.apucampusterminalforandroid.R.color.colorAccent;

public class DetailActivity extends AppCompatActivity {

    int seletedNo;
    private CampusTerminal ct;
    private CampusTerminal.ctMessage cm;
    private LinearLayout urlLinearLayout,fileLinearLayout;
    private TextView bodyTextView,receivingTimeTextView,viewingPeriodTextView,sourceTextView;
    private TableRow urlTableRow, fileTableRow;
    private String cookies;
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

        urlLinearLayout = (LinearLayout) findViewById(R.id.content_detail_url);
        fileLinearLayout = (LinearLayout) findViewById(R.id.content_detail_file);

        urlTableRow = (TableRow) findViewById(R.id.content_detail_url_row);
        fileTableRow = (TableRow)findViewById(R.id.content_detail_file_row);

//        urlListView = (ListView)findViewById(R.id.detail_url_list);

        getDetail getDetail = new getDetail();
        getDetail.execute();
    }

    private void addHyperlinkTextView (ArrayList<String> titles, final ArrayList<String> links,
                                       LinearLayout linearLayout, int type){

            for (int i=0;i<titles.size();i++){
                LayoutInflater inflater = LayoutInflater.from(DetailActivity.this);
                TextView textView = (TextView) inflater.inflate(R.layout.hyperlink_text,null,false);


//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
////                params.setMarginStart(56);
////                params.setMarginEnd(16);
//                textView.setLayoutParams(params);



//                textView.setText(LinkTitles.get(i));
                if (type == 0){
                    textView.setText(Html.fromHtml
                            ("<a href=\""+links.get(i)+"\">"+titles.get(i)+"</a>"));
                    textView.setMovementMethod(LinkMovementMethod.getInstance());

                }else if (type == 1){
                    textView.setText(titles.get(i));
                    Resources resource = (Resources) getBaseContext().getResources();
                    ColorStateList colorStateList =
                            (ColorStateList) resource.getColorStateList(R.color.colorAccent);
                    textView.setTextColor(colorStateList);
                    textView.setPaintFlags(textView.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    textView.setClickable(true);
                    final int finalI = i;

                    final DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            System.out.println("???????");

                            String stringUrl = "https://portal2.apu.ac.jp/campusp"+
                                    links.get(finalI).substring(24,links.get(finalI).length()-3);
                            Log.d("stringurl",stringUrl);
                            DownloadManager.Request request =
                                    new DownloadManager.Request(Uri.parse(stringUrl));
                            Log.d("cookies",cookies);
                            request.addRequestHeader("Cookie", cookies);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                        }
                    });
                }

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
                ArrayList<String> linkTitles = mHashMap.get("otherInformationLinkTitle");
                cookies = mHashMap.get("cookies").get(0).split(";")[0].substring(1);
                if (linkTitles.size() != 0){
                    ArrayList<String> links = mHashMap.get("otherInformationLink");
                    addHyperlinkTextView(linkTitles, links, urlLinearLayout,0);
                    urlTableRow.setVisibility(View.VISIBLE);
                }
                ArrayList<String> fileTitles = mHashMap.get("otherInformationFileTitle");
                if (fileTitles.size() != 0){
                    ArrayList<String> fileLinks = mHashMap.get("otherInformationFileLink");
                    addHyperlinkTextView(fileTitles,fileLinks,fileLinearLayout,1);
                    fileTableRow.setVisibility(View.VISIBLE);
                }




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
