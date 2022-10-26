package com.example.lutic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lutic.Model.Users;
import com.example.lutic.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button authButton, registerButton;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(register);
            }
        });
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);


        if(!Objects.equals(UserPhoneKey, "") && !Objects.equals(UserPasswordKey, "")) {
            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                ValidateUser(UserPhoneKey, UserPasswordKey);
            }
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
                    Log.e("myTag", ""+userData);
                    if(userData.getPhone().equals(phone)) {
                        if(userData.getPassword().equals(password)) {
                            Log.i("TEST_APP", "Покупатель");
                            loadingBar.dismiss();
                            Prevalent.currentUser = userData;
                            Toast.makeText(MainActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(MainActivity.this, UserActivity.class);
                            startActivity(homeIntent);
                        } else {
                            loadingBar.dismiss();
                        }
                    }
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Аккаунт " + phone + " не существует", Toast.LENGTH_SHORT).show();
                    Intent registerIntant = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(registerIntant);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}