package com.example.lutic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;


import com.example.lutic.Model.Products;
import com.example.lutic.Prevalent.Prevalent;
import com.example.lutic.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import io.paperdb.Paper;

public class UserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ImageView openNav;
    DatabaseReference ProductsRef;
    private RecyclerView recyclerView2;
    RecyclerView.LayoutManager layoutManager;


    @SuppressLint("RtlHardcoded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Toolbar toolbar = findViewById(R.id.appBarUsers);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        drawer.addDrawerListener(toogle);
        toogle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.accountName);
        ImageView profileImageView = headerView.findViewById(R.id.accountImg);
        userNameTextView.setText(Prevalent.currentUser.getName());
        Picasso.get().load(Prevalent.currentUser.getImage()).placeholder(R.drawable.account_image).into(profileImageView);

        recyclerView2 = findViewById(R.id.recycler_menu);
        recyclerView2.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView2.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef, Products.class).build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ProductViewHolder holder, int position, @NotNull @NonNull Products model) {
                Log.d("myLog", "????????????"+model.getStoreName());
                Log.d("myLog", "????????????????????????"+(model.getStoreName() != Prevalent.currentUser.getName()));

                if(model.getStoreName() != Prevalent.currentUser.getName()) {
                    holder.txtProductName.setText(model.getName());
                    holder.txtProductDescription.setText(model.getDescription());
                    holder.txtProductPrice.setText("??????????????????: " + model.getPrice() + " ??????.");
                    Picasso.get().load(model.getImage()).into(holder.imageView);
                }


            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layouts, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView2.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_orders) {

        } else if(id == R.id.nav_products) {
            Intent loginIntent = new Intent(UserActivity.this, UserActivity.class);
            startActivity(loginIntent);
            this.finish();
        } else if(id == R.id.nav_profile) {
            Intent loginIntent = new Intent(UserActivity.this, SettingsActivity.class);
            startActivity(loginIntent);
            this.finish();
        } else if(id == R.id.nav_exit) {
            Paper.book().destroy();
            Intent loginIntent = new Intent(UserActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            this.finish();
        }
        DrawerLayout drawerLayout= findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}