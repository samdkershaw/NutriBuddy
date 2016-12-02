package com.samdkershaw.nutribuddy;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.MalformedURLException;
import java.net.URL;

public class AuthBrowserFragment extends Fragment {

    //Variables
    private String FITNESS_SCOPES_BASE_URL;
    private String SCOPES;
    private String AUTH_BASE_URL;
    private String LOOPBACK_URL;
    private String CLIENT_ID;
    private String STATE;

    //Views
    private WebView mainBrowser;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FITNESS_SCOPES_BASE_URL = "https://www.googleapis.com/auth/fitness.";
        SCOPES =
                "profile " +
                        "email " +
                        FITNESS_SCOPES_BASE_URL + "activity.read " +
                        FITNESS_SCOPES_BASE_URL + "nutrition.read " +
                        FITNESS_SCOPES_BASE_URL + "nutrition.write";
        AUTH_BASE_URL = "accounts.google.com/o/oauth2/v2/auth";
        LOOPBACK_URL = "com.samdkershaw.nutribuddy:/";
        CLIENT_ID = getString(R.string.google_client_id);
        STATE = this.getClass().getSimpleName();
    }

    public String buildUrlForScopes() {
        Uri newUri = new Uri.Builder()
                .scheme("https")
                .path(AUTH_BASE_URL)
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("scope", SCOPES)
                .appendQueryParameter("redirect_uri", LOOPBACK_URL)
                .appendQueryParameter("state", STATE)
                .build();
        try {
            return newUri.toString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return "http://www.google.com";
    }

    public String buildUrlForAuth() {
        GoogleDetailsStore googleDetailsStore = GoogleDetailsStore.getInstance();
        Uri newUri = new Uri.Builder()
                .scheme("https")
                .path("www.googleapis.com/oauth2/v4/token")
                .appendQueryParameter("code", googleDetailsStore.getAuthToken())
                .appendQueryParameter("client_id", getString(R.string.google_client_id))
                .appendQueryParameter("redirect_uri", "postmessage")
                .appendQueryParameter("grant_type", "authorization_code")
                .build();
        try {
            return newUri.toString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_google_fitness, container, false);
        mainBrowser = (WebView) thisView.findViewById(R.id.webview_google_fitness);
        setBrowserSettings();

        return thisView;
    }

    @Override
    public void onStart() {
        super.onStart();
        String url = buildUrlForScopes();
        //String url = "https://www.facebook.com/";
        showProgressDialog();
        mainBrowser.loadUrl(url);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void setBrowserSettings() {
        mainBrowser.setWebViewClient(new ScopesBrowser());
        mainBrowser.getSettings().setJavaScriptEnabled(true);
        mainBrowser.getSettings().setLoadsImagesAutomatically(true);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle(getString(R.string.webview_loading_page_title));
            mProgressDialog.setMessage(getString(R.string.webview_loading_page_message));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private class ScopesBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(url);

            if (url.contains("error=")) {
                mainBrowser.stopLoading();
                ((MainActivity)getActivity()).loginDenied(sanitizer.getValue("error"));
            }
            if (url.contains("code=")) {
                view.setVisibility(View.INVISIBLE);
                GoogleDetailsStore acctHolder = GoogleDetailsStore.getInstance();
                acctHolder.setAuthToken(sanitizer.getValue("code"));
                mainBrowser.stopLoading();
                ((MainActivity)getActivity()).completeLoginProcess();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //view.setVisibility(View.VISIBLE);
            hideProgressDialog();
        }
    }
}
