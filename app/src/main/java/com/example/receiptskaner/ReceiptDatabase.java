package com.example.receiptskaner;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Receipt.class}, version = 1)
public abstract class ReceiptDatabase extends RoomDatabase {
    public abstract ReceiptDao receiptDao();

    // Singleton to have 1 instance of db
    private static volatile ReceiptDatabase INSTANCE;

    // Executor to avoid using the main thread
    // to help working with the ViewModel
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    static ReceiptDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ReceiptDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ReceiptDatabase.class, "receipt_database").build();
                }
            }
        }
        return INSTANCE;
    }
}