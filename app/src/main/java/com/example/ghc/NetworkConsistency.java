package com.example.ghc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

public class NetworkConsistency {
    private Context mContext;
    protected boolean periodicNetworkStateChecker = false;
    private Handler handler = new Handler();
    private int Interval = 5000; //milliseconds
    protected String internetDisconnectedMessage = "Internet disconnected!";
    protected String plzCheckInternetMessage = "Please check your Internet Connection";
//    protected FetchData fetchData;


    NetworkConsistency(Context mContext){
        this.mContext = mContext;
    }
    boolean networkStatus(){
        ConnectivityManager connMgr = (ConnectivityManager)  mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    Runnable PeriodicStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                periodicNetworkStateChecker = internetIsConnected();
                Log.d("periodicChecker: ", ""+ periodicNetworkStateChecker);
//                networkConsistencyOutcomes(periodicNetworkStateChecker, fetchData.progressDialog, fetchData.alertDialog);
            }
            finally {
                handler.postDelayed(PeriodicStatusChecker, Interval);
            }
        }
    };

    boolean startRepeatingTask() {
        PeriodicStatusChecker.run();
        return periodicNetworkStateChecker;
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

//    protected void networkConsistencyOutcomes(boolean checker, ProgressDialog progressDialog, AlertDialog alertDialog){
//
//    }

}
