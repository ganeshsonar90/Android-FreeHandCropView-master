package jp.itnav.freehandcropsample.paint.text.type;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.net.NetworkInfo;
import android.os.Handler;

public class ApplicationLoader extends Application {

    @SuppressLint("StaticFieldLeak")
    public static volatile Context applicationContext;
    public static volatile NetworkInfo currentNetworkInfo;
    public static volatile boolean unableGetCurrentNetwork;
    public static volatile Handler applicationHandler;


    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = getApplicationContext();

        applicationHandler = new Handler(applicationContext.getMainLooper());

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            AndroidUtilities.checkDisplaySize(applicationContext, newConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
