package com.example.lutic;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText login_phone, login_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.authButton);
        login_phone = (EditText) findViewById(R.id.login_phone);
        login_password = (EditText) findViewById(R.id.login_password);
    }
}