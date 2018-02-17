package com.kyokuheishin.android.apucampusterminalforandroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kyokuheishin on 2018/2/17.
 */

public class CTService {

    private PasswordStore passwordStore;
    private LatestMessageStore latestMessageStore;


    public static class loginService extends AsyncTask<Void,Void,Boolean> {
        private CampusTerminal ct;
        private String mUsername;
        private String mPassword;
        private boolean loginStatus;
        private Context context;

        loginService(String username, String password,Context context){
            mUsername = username;
            mPassword = password;
            this.context = context;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                //TODO:Add calling method to get the latest message
                checkerService checkerService = new checkerService(ct,context);
                checkerService.execute();


            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                ct = new CampusTerminal();
                ct.ctSpTop();
                loginStatus = ct.ctLogin(mUsername,mPassword);
                Thread.sleep(3000);
                return loginStatus;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return false;
            }


        }
    }

    public static class checkerService extends AsyncTask<Void,Void,Boolean>{

        private CampusTerminal ct;
        private CampusTerminal.ctMessage cm;
        private HashMap<String, ArrayList<String>> informationFromUniversiyHashmap;
        private HashMap<String, ArrayList<String>> importantMessageToYouHashmap;
        private JobParameters jobParameters;
        public Context context;
        private PasswordStore passwordStore;
        private LatestMessageStore latestMessageStore;


        checkerService(CampusTerminal ct, Context context) {
            this.ct = ct;
            this.context = context;
        }



        @Override
        protected Boolean doInBackground(Void... params) {
//            informationFromUniversiyHashmap = new HashMap<>();

            cm = ct.new ctMessage();
            try {
                cm.ctInformation();
                informationFromUniversiyHashmap = new HashMap<>(cm.ctGetMessageList(0));
//                importantMessageToYouHashmap = new HashMap<>(cm.ctGetMessageList(1));
                Thread.sleep(1000);
                importantMessageToYouHashmap = new HashMap<>(cm.ctGetMessageList(1));
                return true;

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return false;
            }


        }


        @Override
        protected void onPostExecute(Boolean success) {

            if (success){
                latestMessageStore = new LatestMessageStore(context);
                ArrayList latestMessageArrayList = latestMessageStore.get();
                int i = 0;
                for (String title:informationFromUniversiyHashmap.get("title")){
                    if (title.equals(latestMessageArrayList.get(0))){

                        break;
                    }else {
                        Notification.Builder builder = new Notification.Builder(context);
                        builder.setSmallIcon(R.drawable.ic_menu_camera);
                        builder.setAutoCancel(true);
                        builder.setContentTitle("大学からのメッセージ");
                        builder.setContentText(title);
                        NotificationManager notificationManager =  (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                        assert notificationManager != null;
                        notificationManager.notify(i,builder.build());
                        i++;
                    }
                }
                int i1 = 10;
                for (String title:importantMessageToYouHashmap.get("title")){
                    if (title.equals(latestMessageArrayList.get(1))){

                        break;
                    }else {
                        Notification.Builder builder = new Notification.Builder(context);
                        builder.setSmallIcon(R.drawable.ic_menu_camera);
                        builder.setAutoCancel(true);
                        builder.setContentTitle("あなた宛の重要なお知らせ");
                        builder.setContentText(title);
                        NotificationManager notificationManager =  (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                        assert notificationManager != null;
                        notificationManager.notify(i1,builder.build());
                        i1++;
                    }
                }
            }
        }
    }
}
