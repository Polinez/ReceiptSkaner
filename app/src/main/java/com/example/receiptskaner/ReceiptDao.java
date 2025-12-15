package com.example.receiptskaner;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReceiptDao {

    @Insert
    void insert(Receipt receipt);

    @Delete
    void delete(Receipt receipt);

    // Returns LiveData
    @Query("SELECT * FROM receipt ORDER BY id DESC")
    LiveData<List<Receipt>> getAllReceipts();
}