package com.samdkershaw.nutribuddy;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WelcomeFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_welcome, container, false);
        Button getStartedButton = (Button) thisView.findViewById(R.id.button_get_started);
        getStartedButton.setOnClickListener(this);

        return thisView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_get_started:
                LoginActivity thisActivity = (LoginActivity) getActivity();
                thisActivity.onClick(v);
                break;
        }
    }
}
