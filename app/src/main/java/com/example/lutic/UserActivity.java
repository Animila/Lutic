package com.example.lutic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import io.paperdb.Paper;

public class UserActivity extends AppCompatActivity {
    private Button logoutBtnUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        logoutBtnUser = (Button) findViewById(R.id.logoutBtn);

        logoutBtnUser.setOnClickListener(view -> {
            Paper.book().destroy();
            Intent logoutIntent = new Intent(UserActivity.this, MainActivity.class);
            startActivity(logoutIntent);
        });

    }
}