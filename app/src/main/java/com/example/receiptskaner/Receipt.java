package com.example.receiptskaner;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "receipt")
public class Receipt {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "store_name")
    private String storeName;

    @ColumnInfo(name = "purchase_date")
    private String purchaseDate;

    @ColumnInfo(name = "warranty_date")
    private String warrantyDate;

    @ColumnInfo(name = "amount")
    private double amount;


    // Path to photo
    @ColumnInfo(name = "photo_uri")
    private String photoUri;

    public Receipt(String storeName, String purchaseDate, double amount, String warrantyDate, String photoUri) {
        this.storeName = storeName;
        this.purchaseDate = purchaseDate;
        this.amount = amount;
        this.warrantyDate = warrantyDate;
        this.photoUri = photoUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getWarrantyDate() {
        return warrantyDate;
    }
    public void setWarrantyDate(String warrantyDate) {
        this.warrantyDate = warrantyDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
