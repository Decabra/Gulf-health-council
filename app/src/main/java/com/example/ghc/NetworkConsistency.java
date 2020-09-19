package com.example.ghc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class NetworkConsistency {
    private Context mContext;
    private boolean periodicChecker = false;
    private Handler handler = new Handler();
    int Interval = 5000; //milliseconds
//    boolean

    NetworkConsistency(Context mContext){
        this.mContext = mContext;
    }
    boolean networkStatus(){
        ConnectivityManager connMgr = (ConnectivityManager)  mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    Runnable PeriodicStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                periodicChecker = internetIsConnected();
                Log.d("periodicChecker: ", ""+periodicChecker);
            }
            finally {
                handler.postDelayed(PeriodicStatusChecker, Interval);
            }
        }
    };

    void startRepeatingTask() {
        PeriodicStatusChecker.run();
    }

    void stopRepeatingTask() {
        handler.removeCallbacks(PeriodicStatusChecker);
    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }


}
