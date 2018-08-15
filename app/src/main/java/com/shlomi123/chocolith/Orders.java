package com.shlomi123.chocolith;

import java.util.Date;

public class Orders {
    private Date Date;
    private String Product;
    private int Quantity;

    public Orders(){}

    Orders(Date date, String product, int quantity)
    {
        Date = date;
        Product = product;
        Quantity = quantity;
    }

    public Date get_date() {
        return Date;
    }

    public int get_quantity() {
        return Quantity;
    }

    public String get_product() {
        return Product;
    }
}
