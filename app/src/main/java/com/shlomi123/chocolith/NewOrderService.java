package com.shlomi123.chocolith;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class NewOrderService extends Service {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String company_email;
    private SharedPreferences sharedPreferences;

    public NewOrderService() {
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);

        company_email = intent.getStringExtra("DISTRIBUTOR_EMAIL");

        db.collection("Companies")
                .document(company_email)
                .collection("Orders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        // create recycler view to show stores and their data
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                switch (documentChange.getType()){
                                    case ADDED:
                                        Order order = documentChange.getDocument().toObject(Order.class);
                                        sendNotification(order);
                                }
                            }
                        }
                    }
                });

        /*int i = 10000000;
        for (int e = 0; e < i; e++) {
            Toast.makeText(getApplicationContext(), Integer.toString(e), Toast.LENGTH_SHORT).show();
        }*/

        return Service.START_STICKY;
    }

    private void sendNotification(Order order){
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ADMIN_MAIN_PAGE.class), 0);

        String msg = order.get_store_name() + " ordered " + order.get_quantity() + " " + order.get_product();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("New order")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationManager.IMPORTANCE_MAX);

        mNotificationManager.notify(1, mBuilder.build());
    }
}
