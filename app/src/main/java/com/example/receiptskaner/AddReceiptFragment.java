package com.example.receiptskaner;

import android.app.Activity;
import android.app.DatePickerDialog;
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
    private EditText editStore, editDate, editAmount;
    // Usunęliśmy Spinner spinnerCategory
    private ReceiptViewModel receiptViewModel;
    private Bitmap takenImageBitmap;

    // Louncher for camera
    ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imgPreview.setImageBitmap(imageBitmap);
                    takenImageBitmap = imageBitmap;
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_receipt, container, false);

        // Load views
        imgPreview = view.findViewById(R.id.imgPreview);
        editStore = view.findViewById(R.id.editStore);
        editDate = view.findViewById(R.id.editDate);
        editAmount = view.findViewById(R.id.editAmount);

        Button btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        Button btnSave = view.findViewById(R.id.btnSave);

        receiptViewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);


        // Date picker
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });

        // button to take photo
        btnTakePhoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureLauncher.launch(takePictureIntent);
        });

        btnSave.setOnClickListener(v -> saveReceipt());

        return view;
    }

    private void showCalendar() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        editDate.setText(selectedDate);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    private String savePhotoToJPG(Bitmap bitmap) {
        String fileName = "img_" + System.currentTimeMillis() + ".jpg";

        File file = new File(requireContext().getFilesDir(), fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            // returning path to photo
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void saveReceipt() {
        String store = editStore.getText().toString();
        String date = editDate.getText().toString();
        String amountStr = editAmount.getText().toString();

        if (store.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(getContext(), "Uzupełnij wszystkie pola!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        String pathToPhoto = "";
        if (takenImageBitmap != null) {
            pathToPhoto = savePhotoToJPG(takenImageBitmap);
        }

        Receipt newReceipt = new Receipt(store, date, amount, pathToPhoto);

        receiptViewModel.insert(newReceipt);

        Toast.makeText(getContext(), "Zapisano paragon!", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }
}