package com.samdkershaw.nutribuddy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Config;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.NETWORK_STATS_SERVICE;

public abstract class Core {
    public boolean isInternetAvailable() {
        //ConnectivityManager cm = (ConnectivityManager) Context.getSystemService(CONNECTIVITY_SERVICE);
        return false;
    }
}
