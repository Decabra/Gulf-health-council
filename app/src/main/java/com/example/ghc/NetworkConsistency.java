package com.example.ghc;

import android.app.Activity;
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
    FetchData fetchData;
    Activity activity;

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
                periodicNetworkStateChecker = isRealInternetConnected();
                Log.d("periodicChecker: ", ""+ periodicNetworkStateChecker);
                networkConsistencyOutcomes(periodicNetworkStateChecker);
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

    public boolean isRealInternetConnected() {
        try {
            long startTime = System.currentTimeMillis();
            String command = "ping -c 1 google.com";
            boolean IR = (Runtime.getRuntime().exec(command).waitFor() == 0);
            long elapsedTime = System.currentTimeMillis() - startTime;
            long elapsedSeconds = elapsedTime / 1000;
            Log.d("Time elapsed",""+elapsedSeconds);
            return IR;
        } catch (Exception e) {
            return false;
        }
    }

    protected void networkConsistencyOutcomes(boolean checker){
        if (!checker){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (fetchData.progressDialog.isShowing())
                        fetchData.progressDialog.dismiss();
                    if (!fetchData.alertDialog.isShowing())
                        fetchData.alertDialog.show();
                }
            });
        }
    }

}
