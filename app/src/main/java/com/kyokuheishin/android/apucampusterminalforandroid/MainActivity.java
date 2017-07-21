package com.kyokuheishin.android.apucampusterminalforandroid;

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
import android.view.Menu;
import android.view.MenuItem;
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
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public Toolbar toolbar;
    private boolean isUpdateFinished = true;
    private int mMessageType = 0;

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
                Snackbar.make(view, "プロトタイプバージョンです。", Snackbar.LENGTH_LONG)
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
//        new Thread(mRunnable).start();
        ;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalIteamCount = recyclerView.getAdapter().getItemCount();
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();

                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition == totalIteamCount - 1
                        && visibleItemCount > 0 && isUpdateFinished) {
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_information_from_university) {
            // Handle the camera action
            mMessageType = 0;
            GetList getList = new GetList();
            getList.execute();
            Toast.makeText(MainActivity.this, "ロード中…", Toast.LENGTH_SHORT).show();
            toolbar.setTitle("大学からの情報");


        }
//        else if (id == R.id.nav_gallery) {
//
//        }
        else if (id == R.id.nav_important_notcie_to_you) {
            mMessageType = 1;
            GetList getList = new GetList();
            getList.execute();
            Toast.makeText(MainActivity.this, "ロード中…", Toast.LENGTH_SHORT).show();
            toolbar.setTitle("あなた宛の重要なお知らせ");
        }
//        else if (id == R.id.nav_manage) {
//
//        }
//        else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GetList extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
                mAdapter = new RecyclerViewAdapter(mHashMap, MainActivity.this);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(mAdapter);

            }
        }
    }

    private class GetNextList extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isUpdateFinished = false;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (mHashMap != null) {
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

