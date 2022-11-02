package com.example.lutic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lutic.Model.Products;
import com.example.lutic.Prevalent.Prevalent;
import com.example.lutic.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import io.paperdb.Paper;

public class SellerActivity extends AppCompatActivity {

    private Button addProduct, exitButton;
    private TextView nameShop;


    DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        addProduct = (Button) findViewById(R.id.addProductBtn);
        exitButton = (Button) findViewById(R.id.exitBtnSeller);
        nameShop = (TextView) findViewById(R.id.nameShop);



        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        recyclerView = findViewById(R.id.recycler_menu_seller);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        nameShop.setText(Prevalent.currentUser.getName());
        Toast.makeText(this, "Добро пожаловать, продавец!", Toast.LENGTH_SHORT).show();

        addProduct.setOnClickListener(view -> {
            Intent AddProductIntent = new Intent(SellerActivity.this, AddProductActivity.class);
            startActivity(AddProductIntent);
        });
        exitButton.setOnClickListener(view -> {
            Paper.book().destroy();
            Intent registerIntant = new Intent(SellerActivity.this, MainActivity.class);
            startActivity(registerIntant);
            this.finish();
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef, Products.class).build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ProductViewHolder holder, int position, @NotNull @NonNull Products model) {
                holder.txtProductName.setText(model.getName());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText("Стоимость: " + model.getPrice() + " руб.");
                Picasso.get().load(model.getImage()).into(holder.imageView);

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layouts, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


}