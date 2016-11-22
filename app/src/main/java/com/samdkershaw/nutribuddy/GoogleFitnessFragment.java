package com.samdkershaw.nutribuddy;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.fitness.Fitness;

public class GoogleFitnessFragment extends Fragment {

    private WebView mainBrowser;

    private String SCOPES_BASE_URL = "https://www.googleapis.com/auth/fitness.";
    private String SCOPES =
            SCOPES_BASE_URL + "activity.read" +
            SCOPES_BASE_URL + "nutrition.read" +
            SCOPES_BASE_URL + "nutrition.write";
    private String AUTH_BASE_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private String RESPONSE_TYPE = "response_type";
    private String CLIENT_ID = "client_id";
    //private String client_id = getString(R.string.)

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google_fitness, container, false);
    }

    private class ScopesBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
