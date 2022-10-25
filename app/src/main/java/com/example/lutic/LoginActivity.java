package com.example.lutic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lutic.Model.Users;
import com.example.lutic.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText login_phone, login_password;
    private ProgressDialog loadingBar;
    private TextView regLink, sellerLink, usersLink;

    private String parentOfName = "Users";
    private CheckBox checkBoxRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.authBtn);
        login_phone = (EditText) findViewById(R.id.register_phone);
        login_password = (EditText) findViewById(R.id.register_password);
        regLink = (TextView) findViewById(R.id.regLink);
        loadingBar = new ProgressDialog(this);

        sellerLink = (TextView) findViewById(R.id.prodovecLink);
        usersLink = (TextView) findViewById(R.id.usersLink);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.login_checkbox);
        Paper.init(this);

        regLink.setOnClickListener(view -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });

        sellerLink.setOnClickListener(view -> {
            usersLink.setVisibility(View.VISIBLE);
            sellerLink.setVisibility(View.INVISIBLE);
            loginBtn.setText("Войти как продавец");
            parentOfName = "Sellers";

        });
        usersLink.setOnClickListener(view -> {
            usersLink.setVisibility(View.INVISIBLE);
            sellerLink.setVisibility(View.VISIBLE);
            loginBtn.setText("Войти");
            parentOfName = "Users";

        });

        loginBtn.setOnClickListener(view -> loginUser());


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

        if(checkBoxRememberMe.isChecked()) {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(parentOfName).child(phone).exists()) {
                    Users userData = snapshot.child(parentOfName).child(phone).getValue(Users.class);
                    if(userData.getPhone().equals(phone)) {
                        if(userData.getPassword().equals(password)) {
                            if (parentOfName.equals("Users")) {
                                Log.i("TEST_APP", "Покупатель");
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(LoginActivity.this, UserActivity.class);
                                startActivity(homeIntent);

                            } else if (parentOfName.equals("Sellers")) {
                                Log.i("TEST_APP", "Продавец");
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                                Intent sellerIntent = new Intent(LoginActivity.this, SellerActivity.class);
                                startActivity(sellerIntent);
                            }
                        } else {
                            loadingBar.dismiss();
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