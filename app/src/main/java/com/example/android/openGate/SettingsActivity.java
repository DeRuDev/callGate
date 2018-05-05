package com.example.android.openGate;

/**
 * Created by Marco on 11/04/18.
 */


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        String address = "";
        if (getIntent().getExtras() != null) {
            address = getIntent().getExtras().getString("LOCATION");
        }

        if (savedInstanceState == null) {
            SettingsFragment fragment = new SettingsFragment();

            Bundle args = new Bundle();
            args.putString("LOCATION", address);
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_container, fragment)
                    .commit();
        }
    }
}
