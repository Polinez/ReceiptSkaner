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
        // Download instance of db
        ReceiptDatabase db = ReceiptDatabase.getDatabase(application);
        receiptDao = db.receiptDao();
        // LiveData
        allReceipts = receiptDao.getAllReceipts();
    }

    // This method will be observed by MainActivity (Fragment)
    public LiveData<List<Receipt>> getAllReceipts() {
        return allReceipts;
    }

    // Method to add - runs on a background thread (doesn't block the UI!)
    public void addReceipt(Receipt receipt) {
        ReceiptDatabase.databaseWriteExecutor.execute(() -> {
            receiptDao.insert(receipt);
        });
    }

    // Method to delete
    public void deleteReceipt(Receipt receipt) {
        ReceiptDatabase.databaseWriteExecutor.execute(() -> {
            receiptDao.delete(receipt);
        });
    }
}
