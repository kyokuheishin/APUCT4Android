package com.kyokuheishin.android.apucampusterminalforandroid;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.util.ArrayList;


/**
 * Created by kyokuheishin on 2018/1/9.
 */

public class LatestMessageStore {

    private Context context;
    private SharedPreferences sharedPreferences;

    public LatestMessageStore(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_latest_message_key),Context.MODE_PRIVATE);
    }

    void storeLatestMessageTitle(String latestMessageTitle){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("latestMessageTitle", latestMessageTitle);

        editor.apply();
    }

    void storeLatestImportantTitle(String latestImportantTitle){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("latestImportantTitle",latestImportantTitle);
        editor.apply();
    }

    ArrayList<String> get(){
        ArrayList<String> list = new ArrayList<>();
        String defaultValue = this.context.getResources().getString(R.string.default_value);
        String latestMessageTitle = sharedPreferences.getString("latestMessageTitle",defaultValue);
        String latestImportantTitle = sharedPreferences.getString("latestImportantTitle",defaultValue);
        list.add(latestMessageTitle);
        list.add(latestImportantTitle);
        return list;
    }
}
