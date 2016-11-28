package com.samdkershaw.nutribuddy;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/*This activity hosts the welcome screen as well as the WebView
     which is then used to request scopes from Google for the
     Fitness API.
 */
public class LoginActivity extends Activity implements
        View.OnClickListener {

    //private GoogleApiClient mGoogleApiClient;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if ((findViewById(R.id.fragment_container) != null && savedInstanceState == null)
                || GoogleAcctHolder.getInstance().getAuthToken() == null) {
            WelcomeFragment welcomeFragment = new WelcomeFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, welcomeFragment, "WelcomeFragment")
                    .commit();
        }
    }

    private void showErrorToast(String errorMsg) {
        mToast = Toast.makeText(getApplicationContext(),
                "Whoops... Error: " + errorMsg,
                Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void swapFragments() {
        Fragment cFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();
        if (cFragment instanceof WelcomeFragment) {
            fragTransaction.replace(R.id.fragment_container, new AuthBrowserFragment());
            fragTransaction.addToBackStack(null);
            fragTransaction.commitAllowingStateLoss();
        }
        else if (cFragment instanceof AuthBrowserFragment) {
            fragTransaction.replace(R.id.fragment_container, new WelcomeFragment());
            fragTransaction.commitAllowingStateLoss();
        }
    }

    public void loginDenied(String errorMsg) {
        swapFragments();
        showErrorToast(errorMsg);
    }

    //Called when auth token is received in GoogleFitnessFragment class
    public void completeLoginProcess() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_get_started:
                swapFragments();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}