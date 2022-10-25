package com.example.lutic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AddProductActivity extends AppCompatActivity {

    private ImageView productImage;
    private EditText productName, productAbout, productPrice;
    private Button addNewProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        productImage = (ImageView) findViewById(R.id.select_product_image);
//        productName = (EditText) findViewById(R.id.product_name);
//        productAbout = (EditText) findViewById(R.id.product_about);
//        productPrice = (EditText) findViewById(R.id.product_price);
//        addNewProduct = (Button) findViewById(R.id.add_product_save);
    }
}