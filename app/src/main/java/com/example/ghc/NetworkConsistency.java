package com.example.ghc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConsistency {
    private Context mContext;
    NetworkConsistency(Context mContext){
        this.mContext = mContext;
    }
    boolean NetworkStatus(){
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
}
