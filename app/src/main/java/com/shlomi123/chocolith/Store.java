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
    //private ArrayList<Object> orders;

    public Store()
    {

    }

    Store(String name, String email, String address, int phone)
    {
        _name = name;
        _email = email;
        _address = address;
        _phone = phone;
        //orders = null;
    }

    Store(String name, String email, String address, int phone, String authUID, ArrayList<Object> orders)
    {
        _name = name;
        _email = email;
        _address = address;
        _phone = phone;
        //this.orders = orders;
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

    /*public ArrayList<Object> getOrders() {
        return orders;
    }*/

    public void set_name(String _name) {
        this._name = _name;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public void set_phone(int _phone) {
        this._phone = _phone;
    }

    /*public void setOrders(ArrayList<Object> orders) {
        this.orders = orders;
    }*/
}
