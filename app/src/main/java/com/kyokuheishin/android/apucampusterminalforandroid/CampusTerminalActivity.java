package com.kyokuheishin.android.apucampusterminalforandroid;

import android.app.Application;

/**
 * Created by qbx on 2017/7/4.
 */

public class CampusTerminalActivity extends Application {
    CampusTerminal ct = new CampusTerminal();
    CampusTerminal.ctMessage cm = ct.new ctMessage();
}
