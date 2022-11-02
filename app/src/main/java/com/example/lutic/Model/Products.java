package com.example.lutic.Model;


import android.util.Log;

public class Products {
    private String product_name, description, price, image, id, date, time, storeName;

    public Products() {

    }

    public Products(String name, String description, String price, String image, String id, String date, String time, String storeName) {
        this.product_name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.id = id;
        this.date = date;
        this.time = time;
        this.storeName = storeName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        Log.d("myLog", "SETTER: "+storeName);
        this.storeName = storeName;
    }

    public String getName() {
        return product_name;
    }

    public void setName(String name) {
        this.product_name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
