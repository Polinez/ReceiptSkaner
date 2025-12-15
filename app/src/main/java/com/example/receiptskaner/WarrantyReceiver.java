package com.example.receiptskaner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class WarrantyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // downlad shop name from intent
        String storeName = intent.getStringExtra("store_name");

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "KanalGwarancji")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // clock icon
                .setContentTitle("Koniec gwarancji")
                .setContentText("Kończy się gwarancja na produkt z: " + storeName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Używamy unikalnego ID (np. hashCode nazwy sklepu), żeby powiadomienia się nie nadpisywały
            notificationManager.notify(storeName.hashCode(), builder.build());
        }
    }
}
