package com.samdkershaw.nutribuddy;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionsManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/*This activity hosts the welcome screen as well as the WebView
     which is then used to request scopes from Google for the
     Fitness API.
 */
public class MainActivity extends Activity {

    private Toast mToast;
    protected String mLoginHint;
    BroadcastReceiver mRestrictionsReceiver;

    private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String USED_INTENT = "USED_INTENT";
    private static final String LOGIN_HINT = "login_hint";

    AuthState mAuthState;

    Button mButtonGetStarted;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mButtonGetStarted = (Button) findViewById(R.id.button_get_started);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_authorization);

        enablePostAuthorizationFlows();

        mButtonGetStarted.setOnClickListener(new AuthorizeListener(this));

        getAppRestrictions();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        checkIntent(intent);
    }

    private void checkIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case "com.samdkershaw.nutribuddy.HANDLE_AUTHORIZATION_RESPONSE":
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent);
                        intent.putExtra(USED_INTENT, true);
                    }
                    break;
            }
        }
    }

    private void enablePostAuthorizationFlows() {
        mAuthState = restoreAuthState();
        if (mAuthState != null && mAuthState.isAuthorized()) {
            if (mProgressBar.getVisibility() == View.INVISIBLE) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if (mButtonGetStarted.getVisibility() == View.VISIBLE) {
                mButtonGetStarted.setVisibility(View.GONE);
            }
        } else {
            mButtonGetStarted.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void persistAuthState(@NonNull AuthState authState) {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putString(AUTH_STATE, authState.toJsonString())
                .commit();
        enablePostAuthorizationFlows();
    }

    private void clearAuthState() {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(AUTH_STATE)
                .apply();
    }

    @Nullable
    private AuthState restoreAuthState() {
        String jsonString = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(AUTH_STATE, null);
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                return AuthState.fromJson(jsonString);
            } catch (JSONException jsonException) {
                // should never happen
            }
        }
        return null;
    }

    @Override
    protected void onResume(){
        super.onResume();

        // Retrieve app restrictions and take appropriate action
        getAppRestrictions();

        // Register a receiver for app restrictions changed broadcast
        registerRestrictionsReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mRestrictionsReceiver);
    }

    private void handleAuthorizationResponse(@NonNull Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        final AuthState authState = new AuthState(response, error);
        if (response != null) {
            Log.i("Token:", String.format("Handled Authorization Response %s ", authState.toJsonString()));
            AuthorizationService service = new AuthorizationService(this);
            service.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                @Override
                public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                    if (exception != null) {
                        Log.w("Token:", "Token Exchange failed", exception);
                    } else {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, exception);
                            persistAuthState(authState);
                            Log.i("Token:", String.format("Token Response [ Access Token: %s, ID Token: %s ]", tokenResponse.accessToken, tokenResponse.idToken));
                        }
                    }
                }
            });
        }
    }

    private void showErrorToast(String errorMsg) {
        mToast = Toast.makeText(getApplicationContext(),
                "Whoops... Error: " + errorMsg,
                Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void loginDenied(String errorMsg) {
        showErrorToast(errorMsg);
    }

    //Called when auth token is received in GoogleFitnessFragment class
    public void completeLoginProcess() {
        String authToken = GoogleDetailsStore.getInstance().getAuthToken();
        RestConnection restConnection = new RestConnection();
        restConnection.getAccessFromAuthCode(authToken);
        Intent mainActivityIntent = new Intent(this, AppActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

    private void getAppRestrictions(){
        RestrictionsManager restrictionsManager =
                (RestrictionsManager) this
                        .getSystemService(Context.RESTRICTIONS_SERVICE);

        Bundle appRestrictions = restrictionsManager.getApplicationRestrictions();

        // Block user if KEY_RESTRICTIONS_PENDING is true, and save login hint if available
        if(!appRestrictions.isEmpty()){
            if(appRestrictions.getBoolean(UserManager.
                    KEY_RESTRICTIONS_PENDING)!=true){
                mLoginHint = appRestrictions.getString(LOGIN_HINT);
            }
            else {
                Toast.makeText(this,R.string.restrictions_pending_block_user,
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void registerRestrictionsReceiver(){
        IntentFilter restrictionsFilter =
                new IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED);

        mRestrictionsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getAppRestrictions();
            }
        };

        registerReceiver(mRestrictionsReceiver, restrictionsFilter);
    }

    public static class AuthorizeListener implements Button.OnClickListener {

        private final MainActivity mMainActivity;

        public AuthorizeListener(@NonNull MainActivity mainActivity) { mMainActivity = mainActivity; }

        @Override
        public void onClick(View v) {
            AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                    Uri.parse("https://accounts.google.com/o/oauth2/v2/auth"),
                    Uri.parse("https://www.googleapis.com/oauth2/v4/token")
            );
            AuthorizationService authorizationService = new AuthorizationService(v.getContext());
            String clientId = "65097338712-a69lc4uoanrth87430sqvnicc90sjpdb.apps.googleusercontent.com";
            Uri redirectUri = Uri.parse("com.samdkershaw.nutribuddy:/oauth2callback");
            String state = this.getClass().getName();
            AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                    serviceConfiguration,
                    clientId,
                    AuthorizationRequest.RESPONSE_TYPE_CODE,
                    redirectUri
            );
            String FITNESS_SCOPES_BASE_URL = "https://www.googleapis.com/auth/fitness.";
            builder.setScopes("profile",
                    "email",
                    FITNESS_SCOPES_BASE_URL + "activity.write",
                    FITNESS_SCOPES_BASE_URL + "body.write",
                    FITNESS_SCOPES_BASE_URL + "location.write",
                    FITNESS_SCOPES_BASE_URL + "nutrition.write"
                    );

            if(mMainActivity.getLoginHint() != null){
                Map loginHintMap = new HashMap<String, String>();
                loginHintMap.put(LOGIN_HINT,mMainActivity.getLoginHint());
                builder.setAdditionalParameters(loginHintMap);

                Log.i("Token:", String.format("login_hint: %s", mMainActivity.getLoginHint()));
            }

            AuthorizationRequest request = builder.build();
            String action = "com.samdkershaw.nutribuddy.HANDLE_AUTHORIZATION_RESPONSE";
            Intent postAuthorizationIntent = new Intent(action);
            PendingIntent pendingIntent = PendingIntent.getActivity(v.getContext(), request.hashCode(), postAuthorizationIntent, 0);
            authorizationService.performAuthorizationRequest(request, pendingIntent);
        }


    }

    public String getLoginHint() { return mLoginHint; }

}