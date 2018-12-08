package com.shlomi123.chocolith;
 //TODO cost per unit, number of units per package
public class Product {
    private String name;
    private String imageUrl;
    private int cost;
    private int units_per_package;

    public Product() {
        //empty constructor needed
    }

    public Product(String name, String imageUrl, int cost, int units_per_package) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.cost = cost;
        this.units_per_package = units_per_package;
    }

     public int getCost() {
         return cost;
     }

     public void setCost(int cost) {
         this.cost = cost;
     }

     public int getUnits_per_package() {
         return units_per_package;
     }

     public void setUnits_per_package(int units_per_package) {
         this.units_per_package = units_per_package;
     }

     public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
