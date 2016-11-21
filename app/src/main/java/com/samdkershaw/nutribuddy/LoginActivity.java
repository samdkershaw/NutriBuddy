package com.samdkershaw.nutribuddy;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.fitness.Fitness;

//import java.util.concurrent.TimeUnit;

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class LoginActivity extends Activity implements
        View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    //private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private Dialog mErrorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getSupportActionBar().hide();

        // Views

        Log.d(TAG, "Reached this point...");
        // Button listeners
        findViewById(R.id.button_get_started).setOnClickListener(this);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.DialogBoxThemeBlah);
            mProgressDialog.setTitle(R.string.attempting_sign_in);
            mProgressDialog.setMessage(getString(R.string.attempting_sign_in_desc));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void showErrorDialog() {
       if (mErrorDialog == null) {
           mErrorDialog = new Dialog(this, R.style.DialogBoxThemeBlah);
           mErrorDialog.setTitle(R.string.error_dialog_title);
           mErrorDialog.setCancelable(true);
           mErrorDialog.show();
       }
    }

    private void hideErrorDialog() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.hide();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_get_started:
                showProgressDialog();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}