package com.example.todo20.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface tokenDao {

    @Query("SELECT * FROM TOKENS")
    List<TokenDb> getAllTokens();

//    @Query("SELECT * FROM Token WHERE id =:taskID")
//    TokenDb getTokenById(int tokID);

    @Insert
    void insertToken(TokenDb... token);

    @Delete
    void deleteToken(TokenDb token);

    @Update
    void updateToken(TokenDb token);



}
