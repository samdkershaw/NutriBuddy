package com.samdkershaw.nutribuddy;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class GoogleAcctHolder {
    private static GoogleAcctHolder ourInstance = new GoogleAcctHolder();
    private String mEmail = "";
    private String mForeName = "";
    private String mLastName = "";

    private String mId = "";
    private String idToken = "";
    private String serverAuthCode = "";
    private URL photoUrl = null;

    public static GoogleAcctHolder getInstance() {
        return ourInstance;
    }

    private GoogleAcctHolder() {
    }

    public void setGoogleAcct(GoogleSignInAccount acct) {
        mId = acct.getId();
        idToken = acct.getIdToken();
        serverAuthCode = acct.getServerAuthCode();
        String temp = acct.getPhotoUrl().getPath();
        try {
            photoUrl = new URL("https://lh3.googleusercontent.com" + temp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getId() { return mId; }
    public String getIdToken() { return idToken; }
    public String getServerAuthCode() { return serverAuthCode; }
    public URL getPhotoUrl() { return photoUrl; }
}
