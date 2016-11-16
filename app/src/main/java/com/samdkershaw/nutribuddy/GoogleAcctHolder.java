package com.samdkershaw.nutribuddy;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class GoogleAcctHolder {
    private static GoogleAcctHolder ourInstance = new GoogleAcctHolder();
    private String mEmail = "";
    private String mForeName = "";
    private String mLastName = "";

    private String mId = "";
    private String idToken = "";
    private String serverAuthCode = "";

    public static GoogleAcctHolder getInstance() {
        return ourInstance;
    }

    private GoogleAcctHolder() {
    }

    public void setGoogleAcct(GoogleSignInAccount acct) {
        mId = acct.getId();
        idToken = acct.getIdToken();
        serverAuthCode = acct.getServerAuthCode();
    }

    public String getId() { return mId; }
    public String getIdToken() { return idToken; }
    public String getServerAuthCode() { return serverAuthCode; }
}
