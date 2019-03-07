package com.shlomi123.chocolith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store {
    private String _name;
    private String _email;

    public Store()
    {

    }

    Store(String name, String email)
    {
        _name = name;
        _email = email;
    }


    public String get_name() {
        return _name;
    }

    public String get_email() {
        return _email;
    }


}
