package com.example.receiptskaner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.util.List;

public class ListFragment extends Fragment {

    private ReceiptViewModel receiptViewModel;
    private LinearLayout container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, parent, false);
        container = view.findViewById(R.id.container_list);
        receiptViewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);

        receiptViewModel.getAllReceipts().observe(getViewLifecycleOwner(), new Observer<List<Receipt>>() {
            @Override
            public void onChanged(List<Receipt> receipts) {
                updateView(receipts, inflater);
            }
        });
        return view;
    }

    private void updateView(List<Receipt> receipts, LayoutInflater inflater) {
        container.removeAllViews();

        for (Receipt receipt : receipts) {
            View box = inflater.inflate(R.layout.item_receipt, container, false);

            TextView txtStore = box.findViewById(R.id.txtStore);
            TextView txtAmount = box.findViewById(R.id.txtAmount);
            TextView txtDate = box.findViewById(R.id.txtDate);
            TextView txtWarranty = box.findViewById(R.id.txtWarranty);
            ImageView imgReceipt = box.findViewById(R.id.imgReceipt);

            txtStore.setText(receipt.getStoreName());
            txtAmount.setText(receipt.getAmount() + " zł");
            txtDate.setText(receipt.getPurchaseDate());

            if (receipt.getWarrantyDate() != null && !receipt.getWarrantyDate().isEmpty()) {
                txtWarranty.setText("Gwarancja: " + receipt.getWarrantyDate());
            } else {
                txtWarranty.setText("");
            }

            String path = receipt.getPhotoUri();
            if (path != null && !path.isEmpty()) {
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imgReceipt.setImageBitmap(myBitmap);
                }
            } else {
                imgReceipt.setImageResource(android.R.drawable.ic_menu_camera);
            }

            // click in element
            box.setOnClickListener(v -> {
                ReceiptDetailsFragment detailsFragment = new ReceiptDetailsFragment();

                Bundle args = new Bundle();
                args.putInt("RECEIPT_ID", receipt.getId());
                detailsFragment.setArguments(args);

                // open fragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailsFragment)
                        .addToBackStack(null)
                        .commit();
            });

            container.addView(box);
        }
    }
}