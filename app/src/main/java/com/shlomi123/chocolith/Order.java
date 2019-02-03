package com.shlomi123.chocolith;

import java.util.Date;

public class Order {
    private Date _date;
    private String _product;
    private int _quantity;
    private String _distributor;
    private String _url;

    public Order(){}

    Order(Date date, String product, int quantity, String distributor, String url)
    {
        _date = date;
        _product = product;
        _quantity = quantity;
        _distributor = distributor;
        _url = url;
    }

    public Date get_date() {
        return _date;
    }

    public int get_quantity() {
        return _quantity;
    }

    public String get_product() {
        return _product;
    }

    public String get_distributor() {
        return _distributor;
    }

    public String get_url() {
        return _url;
    }
}
