package com.example.todo20;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.todo20.db.AppDatabase;
import com.example.todo20.db.TokenDb;

import java.net.Inet4Address;
import java.util.List;

public class splashActivity extends AppCompatActivity {


    private Intent intent;
    private List<TokenDb> tokenn;
    private AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        db = AppDatabase.getDatabase(this.getApplicationContext());
        getSupportActionBar().hide();

        tokenn = db.tokenDao().getAllTokens();



        if(tokenn.size()==0){
         intent = new Intent(splashActivity.this, Login.class);}
        else
            intent = new Intent(splashActivity.this, MainActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 1000);




    }
}