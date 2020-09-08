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
    Handler handler = new Handler();
    int delay = 1000; //milliseconds

    NetworkConsistency(Context mContext){
        this.mContext = mContext;
    }
    boolean networkStatus(){
        ConnectivityManager connMgr = (ConnectivityManager)  mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
//        final String DEBUG_TAG = "NetworkStatusExample";
//        final ConnectivityManager connMgr = getSystemService(Context.CONNECTIVITY_SERVICE);
//        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        boolean isWifiConn = false;
//        boolean isMobileConn = false;
//        for(Network network : connMgr.getAllNetworks()) {
//                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
//                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//                    isWifiConn |= networkInfo.isConnected();
//                }
//                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//                    isMobileConn |= networkInfo.isConnected();
//                }
//            }
//        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
//        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);
    }
    boolean periodicNetworkChecking(){
        handler.postDelayed(new Runnable(){
            public void run(){
                periodicChecker =  networkStatus();
                handler.postDelayed(this, delay);
            }
        }, delay);
        Log.d("periodicChecker", ""+periodicChecker);
        return periodicChecker;
    }
}
