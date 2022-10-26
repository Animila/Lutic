package com.example.lutic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private TextView exitSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        exitSetting = (TextView) findViewById(R.id.close_settings_tw);
        exitSetting.setOnClickListener(view -> {
            Intent loginIntent = new Intent(SettingsActivity.this, UserActivity.class);
            startActivity(loginIntent);
        });
    }
}