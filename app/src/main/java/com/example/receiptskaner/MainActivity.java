package com.example.receiptskaner;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ReceiptViewModel receiptViewModel;
    private Button btnList;
    private Button btnAdd;

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnList = findViewById(R.id.btnList);
        btnAdd = findViewById(R.id.btnAdd);

        createNotificationChannel();

        receiptViewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);

        if (checkPermission()) {
            checkWarranty();
        } else {
            requestPermission();
        }

        if (savedInstanceState == null) {
            loadFragment(new ListFragment(), false);
        }

        btnList.setOnClickListener(v -> {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            loadFragment(new ListFragment(), false);
        });

        btnAdd.setOnClickListener(v -> {
            loadFragment(new AddReceiptFragment(), true);
        });
    }


    private void checkWarranty() {
        // Download all receipts from db
        receiptViewModel.getAllReceipts().observe(this, receipts -> {
            long today = System.currentTimeMillis();
            long sevenDays = 7L * 24 * 60 * 60 * 1000;

            for (Receipt receipt : receipts) {
                long endOfWarranty = changeDateToMillis(receipt.getWarrantyDate());

                if (endOfWarranty >= today && (endOfWarranty - today) < sevenDays) {
                    Intent intent = new Intent(this, WarrantyReceiver.class);
                    intent.putExtra("store_name", receipt.getStoreName());
                    sendBroadcast(intent);
                }
            }
        });
    }



    private boolean checkPermission() {
            return ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE
            );
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Denied")
                .setMessage("This app requires permission to show notifications about warranty. Please grant the permission in app settings.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                checkWarranty();
            } else {
                showPermissionDeniedDialog();
            }
        }
    }

    private long changeDateToMillis(String dataStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dataStr);
            return date != null ? date.getTime() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private void loadFragment(Fragment fragment, boolean addToStack) {
        var transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment);
        if (addToStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("KanalGwarancji", "Gwarancje", NotificationManager.IMPORTANCE_DEFAULT);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}