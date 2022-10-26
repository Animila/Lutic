package com.example.lutic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lutic.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private TextView exitSetting, saveSetting;
    private EditText setting_account_name, setting_account_phone, setting_account_address;
    private ImageView setting_account_image;
    private String checker = "";
    private static final int GalleryPick = 1;
    private StorageReference storageProfilePictureRef;
    private StorageTask uploadTask;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setting_account_image = (ImageView) findViewById(R.id.settings_account_image);
        setting_account_name = (EditText) findViewById(R.id.settings_account_name);
        setting_account_address = (EditText) findViewById(R.id.settings_account_address);
        setting_account_phone = (EditText) findViewById(R.id.settings_account_number);

        setting_account_phone.setText(Prevalent.currentUser.getPhone());

        exitSetting = (TextView) findViewById(R.id.close_settings_tw);
        saveSetting = (TextView) findViewById(R.id.save_settings_tw);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
        userInfoDisplay(setting_account_image, setting_account_name, setting_account_phone, setting_account_address);

        exitSetting.setOnClickListener(view -> {
            Intent loginIntent = new Intent(SettingsActivity.this, UserActivity.class);
            startActivity(loginIntent);
        });

        saveSetting.setOnClickListener(view -> {
            if(checker.equals("clicked")) {
                userInfoSaved();
            } else {
                updateOnlyUserInfo();
            }
        });

        setting_account_image.setOnClickListener(view -> {
            checker = "clicked";
            OpenGallery();
        });
    }

    private void userInfoDisplay(final ImageView setting_account_image,final  EditText setting_account_name,final  EditText setting_account_phone,final  EditText setting_account_address) {
        String phone = Prevalent.currentUser.getPhone();

        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        Log.d("myTag", ""+phone);
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(setting_account_image);
                        setting_account_name.setText(name);
                        setting_account_phone.setText(phone);
                        setting_account_address.setText(address);
                    }

                    if (dataSnapshot.child("address").exists())
                    {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        setting_account_name.setText(name);
                        setting_account_phone.setText(phone);
                        setting_account_address.setText(address);
                    }
                    if (dataSnapshot.child("name").exists())
                    {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        setting_account_name.setText(name);
                        setting_account_phone.setText(phone);
                        setting_account_address.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void userInfoSaved() {
        if(TextUtils.isEmpty(setting_account_name.getText().toString())) {
            Toast.makeText(this, "Заполните Имя", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(setting_account_address.getText().toString())) {
            Toast.makeText(this, "Заполните адрес", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(setting_account_phone.getText().toString())) {
            Toast.makeText(this, "Заполните телефон", Toast.LENGTH_SHORT).show();
        } else if(checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Обновляемся..");
        progressDialog.setMessage("Пожалуйста, подождите");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePictureRef
                    .child(Prevalent.currentUser.getPhone() + ".WebP");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception
                        {
                            if (!task.isSuccessful())
                            {
                                throw task.getException();
                            }

                            return fileRef.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if (task.isSuccessful())
                            {
                                Uri downloadUrl = task.getResult();
                                String myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap. put("name", setting_account_name.getText().toString());
                                userMap. put("address", setting_account_address.getText().toString());
                                userMap. put("phone", setting_account_phone.getText().toString());
                                userMap. put("image", myUrl);
                                ref.child(Prevalent.currentUser.getPhone()).updateChildren(userMap);

                                progressDialog.dismiss();

                                Toast.makeText(SettingsActivity.this, "Информация успешно сохранена", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SettingsActivity.this, UserActivity.class));
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Изображение не выбрано.", Toast.LENGTH_SHORT).show();
        }
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", setting_account_name.getText().toString());
        userMap.put("address", setting_account_address.getText().toString());
        userMap.put("phone", setting_account_phone.getText().toString());
        ref.child(Prevalent.currentUser.getPhone()).updateChildren(userMap);


        Toast.makeText(this, "Успешно сохранено", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(SettingsActivity.this, UserActivity.class));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            setting_account_image.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
        }
    }
}