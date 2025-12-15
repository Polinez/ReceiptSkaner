package com.example.receiptskaner;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class AddReceiptFragment extends Fragment {

    private ImageView imgPreview;
    private EditText editStore, editDate, editWarranty, editAmount;
    private ReceiptViewModel receiptViewModel;
    private Bitmap takenImageBitmap;

    private Calendar alarmCalendar = Calendar.getInstance();
    private boolean alarmSet = false;

    ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    imgPreview.setImageBitmap(imageBitmap);
                    takenImageBitmap = imageBitmap;
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_receipt, container, false);

        imgPreview = view.findViewById(R.id.imgPreview);
        editStore = view.findViewById(R.id.editStore);
        editDate = view.findViewById(R.id.editDate);
        editWarranty = view.findViewById(R.id.editWarranty);
        editAmount = view.findViewById(R.id.editAmount);
        Button btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        Button btnSave = view.findViewById(R.id.btnSave);

        receiptViewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);

        // buy Date
        editDate.setOnClickListener(v -> showCalendar(editDate, null));

        // Warranty date
        editWarranty.setOnClickListener(v -> showCalendar(editWarranty, alarmCalendar));

        btnTakePhoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureLauncher.launch(takePictureIntent);
        });

        btnSave.setOnClickListener(v -> saveReceipt());

        return view;
    }

    private void showCalendar(EditText target, Calendar calendarToSet) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            target.setText(year + "-" + (month + 1) + "-" + day);
            if (calendarToSet != null) {
                // Set alarm on 9:00
                calendarToSet.set(year, month, day, 9, 0, 0);
                alarmSet = true;
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private String savePhotoToJPG(Bitmap bitmap) {
        String fileName = "img_" + System.currentTimeMillis() + ".jpg";
        File file = new File(requireContext().getFilesDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (IOException e) {
            return "";
        }
    }

    private void saveReceipt() {
        String store = editStore.getText().toString();
        String date = editDate.getText().toString();
        String warranty = editWarranty.getText().toString();
        String amountStr = editAmount.getText().toString();

        if (store.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Uzupełnij pola!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String photoPath = (takenImageBitmap != null) ? savePhotoToJPG(takenImageBitmap) : "";

        // Save to db
        receiptViewModel.insert(new Receipt(store, date, amount, warranty, photoPath));

        // Set alarm
        if (alarmSet) {
            setAlarm(store);
        }

        Toast.makeText(getContext(), "Zapisano!", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }

    private void setAlarm(String storeName) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        // Initial receiver
        Intent intent = new Intent(requireContext(), WarrantyReceiver.class);
        intent.putExtra("store_name", storeName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
        }
    }
}