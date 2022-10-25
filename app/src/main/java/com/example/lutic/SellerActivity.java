package com.example.lutic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class SellerActivity extends AppCompatActivity {

    private Button addProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);
        addProduct = (Button) findViewById(R.id.addProductBtn);
        Toast.makeText(this, "Добро пожаловать, продавец!", Toast.LENGTH_SHORT).show();

        addProduct.setOnClickListener(view -> {
            Intent AddProductIntent = new Intent(SellerActivity.this, AddProductActivity.class);
            startActivity(AddProductIntent);
        });
    }


}