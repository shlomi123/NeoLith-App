package com.shlomi123.chocolith;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Table;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.poi.*;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class Helper {
    static public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    static public ArrayList<Order> getOrdersFromStore(Store store)
    {
        Gson gson = new Gson();
        String jsonAsString = gson.toJson(store.getOrders());
        Type type = new TypeToken<ArrayList<Order>>(){}.getType();
        return gson.fromJson(jsonAsString, type);
    }

    static public boolean saveExcelFile(Context context, List<Store> stores) {

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Store Orders (" + getSimpleDate() + ")");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Store Name");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Store Address");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Store Phone Number");
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("Store Email Address");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));

        Row row1;
        int counter = 1;
        for (Store store: stores)
        {
            //TODO show products of stores
            row1 = sheet1.createRow(counter);

            c = row1.createCell(0);
            c.setCellValue(store.get_name());

            c = row1.createCell(1);
            c.setCellValue(store.get_address());

            c = row1.createCell(2);
            c.setCellValue(String.valueOf(store.get_phone()));

            c = row1.createCell(3);
            c.setCellValue(store.get_email());

            counter++;
        }

        // Create a path where we will place our List of objects on external storage
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Neoliv", "Stores.xls");
        if (!file.exists()) {
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Neoliv");
            directory.mkdirs();
        }
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    static public String getSimpleDate()
    {
        Date date = Calendar.getInstance().getTime();
        //
        // Display a date in day, month, year format
        //
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }

    static public Distributor document_to_distributor(DocumentSnapshot documentSnapshot){
        final Distributor distributor = new Distributor();

        distributor.setEmail(documentSnapshot.getString("Email"));
        distributor.setId(documentSnapshot.getId());
        distributor.setName(documentSnapshot.getString("Name"));

        return distributor;
    }
}
