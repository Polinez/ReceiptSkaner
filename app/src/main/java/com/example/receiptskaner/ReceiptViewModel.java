package com.example.receiptskaner;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class ReceiptViewModel extends AndroidViewModel {

    private final ReceiptDao receiptDao;
    private final LiveData<List<Receipt>> allReceipts;

    public ReceiptViewModel(@NonNull Application application) {
        super(application);
        ReceiptDatabase db = ReceiptDatabase.getDatabase(application);
        receiptDao = db.receiptDao();

        allReceipts = receiptDao.getAllReceipts();
    }

    public LiveData<List<Receipt>> getAllReceipts() {
        return allReceipts;
    }

    public void insert(Receipt receipt) {
        new Thread(() -> {
            receiptDao.insert(receipt);
        }).start();
    }

    public void delete(Receipt receipt) {
        new Thread(() -> {
            receiptDao.delete(receipt);
        }).start();
    }
}