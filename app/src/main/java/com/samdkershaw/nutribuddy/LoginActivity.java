package com.samdkershaw.nutribuddy;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.fitness.Fitness;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*This activity hosts the welcome screen as well as the WebView
     which is then used to request scopes from Google for the
     Fitness API.
 */
public class LoginActivity extends FragmentActivity implements
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Toast mToast;
    private Button getStartedButton;

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getStartedButton = (Button) findViewById(R.id.button_get_started);
        getStartedButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestScopes(Fitness.SCOPE_ACTIVITY_READ_WRITE,
                        Fitness.SCOPE_BODY_READ_WRITE,
                        Fitness.SCOPE_LOCATION_READ_WRITE,
                        Fitness.SCOPE_NUTRITION_READ_WRITE)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.GOALS_API)
                .addApi(Fitness.CONFIG_API)
                .addApi(Fitness.HISTORY_API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign in");
            handleSignInResult(opr.get());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, Integer.toString(resultCode));
            handleSignInResult(result);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_get_started:
                signInWithGoogle();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, connectionResult.getErrorMessage());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            GoogleAccount acctHolder = GoogleAccount.getInstance();
            acctHolder.setAcct(acct);
            openMainActivity();
        } else {
            showErrorToast(result.getStatus().toString());
        }
    }

    private void showErrorToast(String errorMsg) {
        mToast = Toast.makeText(getApplicationContext(),
                "Whoops... Error: " + errorMsg,
                Toast.LENGTH_LONG);
        mToast.show();
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}