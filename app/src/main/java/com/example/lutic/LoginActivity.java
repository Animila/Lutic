package com.example.lutic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lutic.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText login_phone, login_password;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.authBtn);
        login_phone = (EditText) findViewById(R.id.register_phone);
        login_password = (EditText) findViewById(R.id.register_password);
        loadingBar = new ProgressDialog(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

    }

    private void loginUser() {
        String phone = login_phone.getText().toString();
        String password = login_password.getText().toString();

        if(TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Введите свой пароль", Toast.LENGTH_SHORT).show();
        } else {
//            loadingBar.setVisibility(View.VISIBLE);
            loadingBar.setTitle("Вход в приложение");
            loadingBar.setMessage("Ждите");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(phone, password);
        }
    }

    private void ValidateUser(String phone, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(phone).exists()) {
                    Users userData = snapshot.child("Users").child(phone).getValue(Users.class);
                    if(userData.getPhone().equals(phone)) {
                        if(userData.getPassword().equals(password)) {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(homeIntent);
                        } else {
                            Toast.makeText(LoginActivity.this, "неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Аккаунт " + phone + " не существует", Toast.LENGTH_SHORT).show();
                    Intent registerIntant = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(registerIntant);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}