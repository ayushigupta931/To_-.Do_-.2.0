package com.example.todo20.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TOKENS")
public class TokenDb {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="token")
    public String token;


}
