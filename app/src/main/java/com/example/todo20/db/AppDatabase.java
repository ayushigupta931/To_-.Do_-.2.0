package com.example.todo20.db;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TokenDb.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract tokenDao tokenDao();

    private static AppDatabase instance;

    public static AppDatabase getDatabase(Context context){

        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"DATABASE").allowMainThreadQueries().build() ;
        }
        return instance;
    }

}
