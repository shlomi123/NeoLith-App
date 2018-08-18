package com.shlomi123.chocolith;

public class Product {
    private String mName;
    private String mImageUrl;

    public Product() {
        //empty constructor needed
    }

    public Product(String name, String imageUrl) {
        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
