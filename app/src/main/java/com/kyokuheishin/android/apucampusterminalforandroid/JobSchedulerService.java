package com.kyokuheishin.android.apucampusterminalforandroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kyokuheishin on 2018/1/9.
 */

public class JobSchedulerService extends JobService {



    private PasswordStore passwordStore;
    private LatestMessageStore latestMessageStore;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public class loginService extends AsyncTask<Void,Void,Boolean>{
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
                JobSchedulerService.checkerService checkerService = new checkerService(ct,context);
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
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }


        }
    }

     public class checkerService extends AsyncTask<Void,Void,Boolean>{

        private CampusTerminal ct;
        private CampusTerminal.ctMessage cm;
        private HashMap<String, ArrayList<String>> informationFromUniversiyHashmap;
        private HashMap<String, ArrayList<String>> importantMessageToYouHashmap;
        private Context context;

        public checkerService(CampusTerminal ct,Context context) {
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
                importantMessageToYouHashmap = new HashMap<>(cm.ctGetMessageList(1));
                return true;

            } catch (IOException e) {
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
                        i++;
                        break;
                    }else {
                        Notification.Builder builder = new Notification.Builder(context);
                        builder.setSmallIcon(R.drawable.ic_menu_camera);
                        builder.setAutoCancel(true);
                        builder.setContentTitle("新しい大学からのメッセージがあります");
                        builder.setContentText(title);
                        NotificationManager notificationManager =  (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(i,builder.build());
                        i++;
                    }
                }
            }
        }
    }
}


