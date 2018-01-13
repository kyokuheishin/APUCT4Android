package com.kyokuheishin.android.apucampusterminalforandroid;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.jsoup.Connection;

import java.util.ArrayList;


/**
 * Created by kyokuheishin on 2018/1/9.
 */

public class PasswordStore {

    private Context context;
    private SharedPreferences sharedPreferences;

    public PasswordStore(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key),Context.MODE_PRIVATE);
    }

    void store(String username ,String password){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("username", new String(Base64.encode(username.getBytes(),Base64.NO_WRAP)));
        editor.putString("password",new String(Base64.encode(password.getBytes(),Base64.NO_WRAP)));
        editor.apply();
    }

    ArrayList<String> get(){
        ArrayList<String> list = new ArrayList<>();
        String defaultValue = this.context.getResources().getString(R.string.default_value);
        String spUsername = sharedPreferences.getString("username",defaultValue);
        String spPassword = sharedPreferences.getString("password",defaultValue);
        String username = new String(Base64.decode(spUsername.getBytes(),Base64.NO_WRAP));
        String password = new String(Base64.decode(spPassword.getBytes(),Base64.NO_WRAP));
        list.add(username);
        list.add(password);
        return list;
    }
}
