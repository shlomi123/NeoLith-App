package com.shlomi123.chocolith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store {
    private String _name;
    private String _email;
    private String _address;
    private int _phone;
    private ArrayList<HashMap<String, Object>> orders;

    public Store()
    {

    }

    Store(String name, String email, String address, int phone)
    {
        _name = name;
        _email = email;
        _address = address;
        _phone = phone;
        orders = null;
    }

    Store(String name, String email, String address, int phone, ArrayList<HashMap<String, Object>> orders)
    {
        _name = name;
        _email = email;
        _address = address;
        _phone = phone;
        this.orders = orders;
    }


    public String get_name() {
        return _name;
    }

    public String get_email() {
        return _email;
    }

    public String get_address() {
        return _address;
    }

    public int get_phone() {
        return _phone;
    }

    public ArrayList<HashMap<String, Object>> getOrders() {
        return orders;
    }
}
