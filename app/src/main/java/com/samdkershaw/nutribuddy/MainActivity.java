package com.samdkershaw.nutribuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class MainActivity extends Activity {

    GoogleAcctHolder acctInfo = GoogleAcctHolder.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TextView tv1 = (TextView)findViewById(R.id.text_name);
        TextView tv2 = (TextView)findViewById(R.id.text_googleId);
        TextView tv3 = (TextView)findViewById(R.id.text_googleIdToken);
        TextView tv4 = (TextView)findViewById(R.id.text_googleServerAuthCode);
        tv2.setText(acctInfo.getId());
        tv3.setText(acctInfo.getIdToken());
        tv4.setText(acctInfo.getServerAuthCode());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
