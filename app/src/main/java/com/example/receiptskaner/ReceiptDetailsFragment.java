package com.example.receiptskaner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

public class ReceiptDetailsFragment extends Fragment {

    private int receiptId;
    private Receipt currentReceipt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipt_details, container, false);

        // recive ID of receipt
        if (getArguments() != null) {
            receiptId = getArguments().getInt("RECEIPT_ID");
        }

        TextView txtStore = view.findViewById(R.id.detailStore);
        TextView txtDate = view.findViewById(R.id.detailDate);
        TextView txtAmount = view.findViewById(R.id.detailAmount);
        ImageView imgView = view.findViewById(R.id.detailImage);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        // load data in background
        new Thread(() -> {
            ReceiptDatabase db = ReceiptDatabase.getDatabase(requireContext());
            currentReceipt = db.receiptDao().getReceiptById(receiptId);

            if (currentReceipt != null) {
                // update main view
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        txtStore.setText(currentReceipt.getStoreName());
                        txtDate.setText("Data: " + currentReceipt.getPurchaseDate());
                        txtAmount.setText(currentReceipt.getAmount() + " zł");

                        // Loading overview photo
                        String path = currentReceipt.getPhotoUri();
                        if (path != null && !path.isEmpty()) {
                            File imgFile = new File(path);
                            if (imgFile.exists()) {
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                imgView.setImageBitmap(myBitmap);
                            }
                        }
                    });
                }
            }
        }).start();

        // delete button
        btnDelete.setOnClickListener(v -> deleteReceipt());

        return view;
    }

    private void deleteReceipt() {
        if (currentReceipt == null) return;

        new Thread(() -> {
            ReceiptDatabase db = ReceiptDatabase.getDatabase(requireContext());

            // delete from db
            db.receiptDao().delete(currentReceipt);

            // delete from phone
            if (currentReceipt.getPhotoUri() != null && !currentReceipt.getPhotoUri().isEmpty()) {
                File file = new File(currentReceipt.getPhotoUri());
                if (file.exists()) {
                    file.delete();
                }
            }

            // back to list
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Usunięto paragon", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                });
            }
        }).start();
    }
}