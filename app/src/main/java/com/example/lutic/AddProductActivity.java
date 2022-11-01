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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lutic.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {

    private String Description, Price, Name, SaveCurrentDate, SaveCurrentTime, productRandomKey;
    private String downloadImageUrl;
    private ImageView productImage;
    private EditText productName, productAbout, productPrice;
    private Button AddNewProductBtn;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private StorageReference ProductImageRef;
    private DatabaseReference ProductRef;
    private ProgressDialog loadingBar;
    private String Seller = Prevalent.currentUser.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Init();
        
        productImage.setOnClickListener(view -> {
            OpenGallery();
        });

        AddNewProductBtn.setOnClickListener(view -> {
            ValidateProductData();
        });
    }

    public void Init() {
        productImage = (ImageView) findViewById(R.id.select_product_image);
        productName = (EditText) findViewById(R.id.productName);
        productAbout = (EditText) findViewById(R.id.productAbout);
        productPrice = (EditText) findViewById(R.id.productPrice);
        AddNewProductBtn = (Button) findViewById(R.id.addProductSave);
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");
        loadingBar = new ProgressDialog(this);
    }

    private void ValidateProductData() {
        Description = productAbout.getText().toString();
        Name = productName.getText().toString();
        Price = productPrice.getText().toString();

        if(ImageUri == null) {
            Toast.makeText(this, "Добавьте изображение товара", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description)) {
            Toast.makeText(this, "Добавьте описание товара", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Name)) {
            Toast.makeText(this, "Добавьте название товара", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Добавьте стоимость товара", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Добавления товара");
        loadingBar.setMessage("Ждите");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        SaveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        SaveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = SaveCurrentDate + SaveCurrentTime;
        Log.d("myTag", ""+productRandomKey);

        final StorageReference filePath = ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".webp");
        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(e -> {
            String message = e.toString();
            Toast.makeText(AddProductActivity.this, "Ошибка: "+message, Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(AddProductActivity.this, "Изображение успешно загружено", Toast.LENGTH_SHORT).show();
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                downloadImageUrl = filePath.getDownloadUrl().toString();
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    downloadImageUrl = task.getResult().toString();
                    Toast.makeText(AddProductActivity.this, "Фото сохранено", Toast.LENGTH_SHORT).show();

                    SaveProductInfoToDatabase();
                }
            });
        });
    }
    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("storeName", Seller);
        productMap.put("pid", productRandomKey);
        productMap.put("date", SaveCurrentDate);
        productMap.put("time", SaveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("price", Price);
        productMap.put("name", Name);

        ProductRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                loadingBar.dismiss();
                Toast.makeText(AddProductActivity.this, "Товар добавлен", Toast.LENGTH_SHORT).show();
                Intent AddProductIntent = new Intent(AddProductActivity.this, SellerActivity.class);
                startActivity(AddProductIntent);
            } else {
                String message = task.getException().toString();
                Toast.makeText(AddProductActivity.this, "Ошибка: "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            productImage.setImageURI(ImageUri);
        }
    }
}