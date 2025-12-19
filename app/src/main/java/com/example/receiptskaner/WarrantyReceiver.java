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
        String storeName = intent.getStringExtra("store_name");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "KanalGwarancji")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Koniec gwarancji")
                .setContentText("Kończy się gwarancja w: " + storeName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Check for permission
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
