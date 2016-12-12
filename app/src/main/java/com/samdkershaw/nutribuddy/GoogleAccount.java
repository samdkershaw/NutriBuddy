package com.samdkershaw.nutribuddy;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

class GoogleAccount {
    private static final String SHARED_PREFS_NAME = "AuthStatePrefs";
    private static GoogleAccount ourInstance = new GoogleAccount();
    private String displayName = null;
    private String email = null;
    private String surname = null;
    private String forename = null;
    private String id = null;
    private String idToken = null;
    private Uri photoUrl = null;
    private String serverAuthCode = null;

    static GoogleAccount getInstance() {
        return ourInstance;
    }

    private GoogleAccount() {

    }

    Bundle getAcctBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("displayName", displayName);
        bundle.putString("email", email);
        bundle.putString("surname", surname);
        bundle.putString("forename", forename);
        bundle.putString("id", id);
        bundle.putString("idToken", idToken);
        bundle.putString("photoUrl", photoUrl.toString());
        bundle.putString("serverAuthCode", serverAuthCode);

        return bundle;
    }

    void setAcct(GoogleSignInAccount mAcct) {
        displayName = mAcct.getDisplayName();
        email = mAcct.getEmail();
        surname = mAcct.getFamilyName();
        forename = mAcct.getGivenName();
        id = mAcct.getId();
        idToken = mAcct.getIdToken();
        photoUrl = mAcct.getPhotoUrl();
        serverAuthCode = mAcct.getServerAuthCode();
    }
}
