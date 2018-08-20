package com.shlomi123.chocolith;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Helper {
    static public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    static public ArrayList<Orders> getOrdersFromStore(Store store)
    {
        Gson gson = new Gson();
        String jsonAsString = gson.toJson(store.getOrders());
        Type type = new TypeToken<ArrayList<Orders>>(){}.getType();
        return gson.fromJson(jsonAsString, type);
    }
}
