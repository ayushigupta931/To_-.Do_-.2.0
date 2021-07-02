package com.example.todo20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {


    private TextView username;
    private TextView name;
    private TextView email;

    private Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = (TextView) findViewById(R.id.username);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);




//        ApiClient.token = t;

//        token.setToken(t);


        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        Call<UserProfile> call = apiInterface.getUserProfile(Login.tokenn);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if(response.isSuccessful()){
                    Toast.makeText(Profile.this,"Success",Toast.LENGTH_SHORT).show();
                    UserProfile userProfile = response.body();
                    username.setText(userProfile.getUsername());
                    name.setText(userProfile.getName());
                    email.setText(userProfile.getEmail());
                }
                else{
                    Toast.makeText(Profile.this,"Failure",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(Profile.this,"Throwable" + t.getLocalizedMessage() ,Toast.LENGTH_SHORT).show();

            }
        });
    }
}