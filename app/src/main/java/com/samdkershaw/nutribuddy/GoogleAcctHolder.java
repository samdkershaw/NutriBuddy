package com.samdkershaw.nutribuddy;

class GoogleAcctHolder {
    private static GoogleAcctHolder ourInstance = new GoogleAcctHolder();
    private String authToken = null;

    static GoogleAcctHolder getInstance() {
        return ourInstance;
    }

    private GoogleAcctHolder() {
    }

    void setAuthToken(String mAuthToken) { this.authToken = mAuthToken; }
    String getAuthToken() { return authToken; }
}
