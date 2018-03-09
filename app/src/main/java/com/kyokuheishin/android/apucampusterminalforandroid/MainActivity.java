package com.kyokuheishin.android.apucampusterminalforandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.kyokuheishin.android.apucampusterminalforandroid.RecyclerViewAdapter.RecyclerViewAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity

    implements NavigationView.OnNavigationItemSelectedListener {
    private CardView mCardView;
    private CampusTerminal ct;
    static CampusTerminal.ctMessage cm;
    private RecyclerViewAdapter mAdapter;
    private HashMap<String, ArrayList<String>> mHashMap;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;
    private LatestMessageStore latestMessageStore;
    private boolean isUpdateFinished = true;
    private int mMessageType = 0;
    private int mMessageSizeOld;
    private int menuFlag;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(MainActivity.this);

//        mCardView = (CardView) findViewById(R.id.card_view);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "ベータ版です。", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        ct = ((CampusTerminal) getApplicationContext());
        cm = ct.new ctMessage();
        latestMessageStore = new LatestMessageStore(getApplicationContext());

        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        navigationView.setCheckedItem(R.id.nav_information_from_university);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                /*無限スクロールに関する処理*/

                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();

                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition == totalItemCount - 1
                        && visibleItemCount > 0 && isUpdateFinished) {
                    /*スクロールの状態が停止状態　かつ　現在閲覧のは最後のカード　かつ　画面にカードが存在するとき　かつ
                    * 前回のロード終了後のみ実行（バグ防止のため）
                    * */

                    GetNextList getNextList = new GetNextList();
                    getNextList.execute();
                    Toast.makeText(MainActivity.this, "ロード中…", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        View hview = navigationView.getHeaderView(0);

        TextView mailTextView = hview.findViewById(R.id.textView_username);
        PasswordStore passwordStore = new PasswordStore(getApplicationContext());
        mailTextView.setText(passwordStore.get().get(0));


        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (menuFlag == R.id.nav_setting){
                    Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                    startActivity(intent);
                }else if (menuFlag == R.id.nav_logout){
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

//        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
//        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
//        mPublisherAdView.loadAd(adRequest);

        MobileAds.initialize(this,getString(R.string.banner_ad_unit_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("71407EFC6E0E6FA094FF12A423BA3A12")

                .build();
        mAdView.loadAd(adRequest);
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        /*メッセージ種類の切替に関する処理*/
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_information_from_university) {
            recyclerView.setVisibility(View.INVISIBLE);
            setTitle("大学からの情報");
            /*切替時のバグ防止のため、一時的にRecyclerViewを不可視にする*/
            // Handle the camera action
            mMessageType = 0;
            GetList getList = new GetList();
            getList.execute();

            return true;



        }
//        else if (id == R.id.nav_gallery) {
//
//        }
        else if (id == R.id.nav_important_notcie_to_you) {
            recyclerView.setVisibility(View.INVISIBLE);
            setTitle("あなた宛の重要なお知らせ");
            mMessageType = 1;
            GetList getList = new GetList();
            getList.execute();
            Toast.makeText(MainActivity.this, "ロード中…", Toast.LENGTH_SHORT).show();


            return true;
        }
        else if (id == R.id.nav_logout){
            menuFlag =id;


            return false;
        }

        else if (id == R.id.nav_setting){
            menuFlag = id;


            return false;
        }
        else {
            return false;
        }
//        else if (id == R.id.nav_manage) {
//
//        }
//        else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }



    }

    private class GetList extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "ロード中…", Toast.LENGTH_SHORT).show();


        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {

                Thread.sleep(1000);
                cm.ctInformation();
//                Thread.sleep(20000);
                mHashMap = cm.ctGetMessageList(mMessageType);
                String log = mHashMap.toString();
//                Thread.sleep(2000);
                Log.d("list", log);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return mHashMap;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (mHashMap != null) {
                /*コンテンツをRecyclerViewに読み込ませるための処理*/

                String title = mHashMap.get("title").get(0);

                if (mMessageType ==0){
                    latestMessageStore.storeLatestMessageTitle(title);
                }else {
                    latestMessageStore.storeLatestImportantTitle(title);
                }

                mAdapter = new RecyclerViewAdapter(mHashMap, MainActivity.this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setVisibility(View.VISIBLE);

            }
        }
    }

    private class GetNextList extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            isUpdateFinished = false;
            mMessageSizeOld = mHashMap.get("title").size();
        }

        @Override
        protected void onPostExecute(Object o) {
            /*追加コンテンツをRecyclerViewに読み込ませるための処理*/
            super.onPostExecute(o);
            if (mHashMap != null) {
                if (mHashMap.get("title").size() == mMessageSizeOld){
                    Toast.makeText(MainActivity.this, "これ以上ありません", Toast.LENGTH_SHORT).show();
                }
                mAdapter.swap(mHashMap);
            }
            isUpdateFinished = true;
        }


        @Override
        protected Object doInBackground(Object[] objects) {
            try {

                mHashMap = cm.ctGetMessageListNextPage();

//                Thread.sleep(2000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mHashMap;
        }
    }


    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            try {
                Thread.sleep(1000);
                cm.ctInformation();
//                Thread.sleep(20000);
                mHashMap = cm.ctGetMessageList(0);
                String log = mHashMap.toString();
                Thread.sleep(2000);
                Log.d("list", log);
//                cm.ctGetMessageList(0);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

// catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    };


}

