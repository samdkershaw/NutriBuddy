package com.samdkershaw.nutribuddy;

import android.content.SharedPreferences;
import android.os.Bundle;

class GoogleDetailsStore {
    private static final String SHARED_PREFS_NAME = "AuthStatePrefs";
    private static GoogleDetailsStore ourInstance = new GoogleDetailsStore();
    private String authToken = null;

    static GoogleDetailsStore getInstance() {
        return ourInstance;
    }

    private GoogleDetailsStore() {

    }

    Bundle getAcctInfo() {
        Bundle bundle = new Bundle();
        bundle.putString("authToken", authToken);

        return bundle;
    }

    void setAuthToken(String mAuthToken) { this.authToken = mAuthToken; }
    String getAuthToken() { return authToken; }
}
