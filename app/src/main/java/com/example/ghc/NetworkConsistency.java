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
                periodicChecker = networkStatus();
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

/*    boolean periodicNetworkChecking(){
        handler.postDelayed(new Runnable(){
            public void run(){
                periodicChecker =  networkStatus();
                handler.postDelayed(this, delay);
            }
        }, delay);
        Log.d("periodicChecker", ""+periodicChecker);
        return periodicChecker;
    }*/
}
