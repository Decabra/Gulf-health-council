package com.example.ghc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

public class NetworkConsistency {
    private Context Context;
    protected boolean periodicNetworkStateChecker = false;
    private Handler handler = new Handler();
    int Interval = 5000; //milliseconds

    NetworkConsistency(Context mContext){
        this.Context = mContext;
    }
    boolean networkStatus(){
        ConnectivityManager connMgr = (ConnectivityManager)  Context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    Runnable PeriodicStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                periodicNetworkStateChecker = internetIsConnected();
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

//    protected void networkConsistencyOutcomes(boolean checker){
//        if (checker){
//            Progress
//        }
//
//    }

}
