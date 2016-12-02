package com.samdkershaw.nutribuddy;

import android.net.Uri;
import android.os.Bundle;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RestConnection {
    private GoogleDetailsStore acctDetails;
    private final String googleClientId = "65097338712-7evu3o82f8si2195turum6u6jhltt8dr.apps.googleusercontent.com";

    public RestConnection() {
    }

    public void getAccessFromAuthCode(String authCode) {
        URL url = buildUrlForAuth();
        HttpURLConnection client = null;
        try {
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            client.setDoOutput(true);
            client.setConnectTimeout(10000);
            client.setReadTimeout(10000);
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }

    public URL buildUrlForAuth() {
        acctDetails = GoogleDetailsStore.getInstance();
        Uri newUri = new Uri.Builder()
                .scheme("https")
                .path("www.googleapis.com/oauth2/v4/token")
                .appendQueryParameter("code", acctDetails.getAuthToken())
                .appendQueryParameter("client_id", googleClientId)
                .appendQueryParameter("redirect_uri", "postmessage")
                .appendQueryParameter("grant_type", "authorization_code")
                .build();
        try {
            return new URL(newUri.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
