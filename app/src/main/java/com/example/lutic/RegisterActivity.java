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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private EditText registerPhone, registerPassword;
    private ProgressDialog loadingBar;
    private TextView authLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn = (Button) findViewById(R.id.regBtn);
        registerPhone = (EditText) findViewById(R.id.register_phone);
        registerPassword = (EditText) findViewById(R.id.register_password);
        authLink = (TextView) findViewById(R.id.authLink);
//        loadingBar = (ProgressBar) findViewById(R.id.progress);
        loadingBar = new ProgressDialog(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
        authLink.setOnClickListener(view -> {
            Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });

    }

    private void CreateAccount() {
        String phone = registerPhone.getText().toString();
        String password = registerPassword.getText().toString();

        if(TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "?????????????? ?????????? ????????????????", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "?????????????? ???????? ????????????", Toast.LENGTH_SHORT).show();
        } else {
//            loadingBar.setVisibility(View.VISIBLE);
            loadingBar.setTitle("???????????????? ????????????????");
            loadingBar.setMessage("??????????");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhone(phone, password);
        }
    }

    private void ValidatePhone(String phone, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child("Users").child(phone).exists())){
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("password", password);

                    RootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {
//                                        loadingBar.setVisibility(View.INVISIBLE);
                                    loadingBar.dismiss();
                                    Toast.makeText(RegisterActivity.this, "?????????????????????? ???????????? ??????????????", Toast.LENGTH_SHORT).show();
                                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(loginIntent);
                                } else {
//                                        loadingBar.setVisibility(View.INVISIBLE);
                                    loadingBar.dismiss();
                                    Toast.makeText(RegisterActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "?????????? "+ phone + " ?????? ??????????????????????????????", Toast.LENGTH_SHORT).show();
//                    loadingBar.setVisibility(View.INVISIBLE);
                    loadingBar.dismiss();
                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}