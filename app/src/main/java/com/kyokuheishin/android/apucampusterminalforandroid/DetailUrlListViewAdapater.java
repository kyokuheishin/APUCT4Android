package com.kyokuheishin.android.apucampusterminalforandroid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by qbx on 2017/8/5.
 */

public class DetailUrlListViewAdapater extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> LinkTitles;
    private ArrayList<String> Links;
    private ListView mListView;
    LayoutInflater layoutInflater;

    public DetailUrlListViewAdapater(ArrayList<String> linkTitles,ArrayList<String> links, ListView listView,Context context) {
        this.LinkTitles = linkTitles;
        this.Links = links;
        this.mListView = listView;
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        System.out.println(LinkTitles.size());
        return LinkTitles.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }



    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TextView textView = new TextView(mContext);
        textView.setText(LinkTitles.get(i));
        Log.d("text",textView.getText().toString());
        return textView;
    }
}
