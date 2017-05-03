package com.chowdhuryfahim.bloodinquiry;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 *
 *  Created by Fahim on 3/28/2017.
 *
 */

public class OnlineChecker {
    Context context;

    OnlineChecker(Context context){
        this.context = context;
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
