package com.samdkershaw.nutribuddy;

public class GoogleAcctHolder {
    private static GoogleAcctHolder ourInstance = new GoogleAcctHolder();
    private String googleEmail = "";


    public static GoogleAcctHolder getInstance() {
        return ourInstance;
    }

    private GoogleAcctHolder() {
    }

    public String getGoogleEmail() {
        return googleEmail;
    }

    public void setGoogleEmail(String email) {
        googleEmail = email;
    }
}
