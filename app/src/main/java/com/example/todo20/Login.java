package com.example.todo20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private TextView registerHere;
    private TextInputEditText username, password;
    private CheckBox keepMeLoggedIn;
    private Button signIn;
    public static String tokenn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (TextInputEditText) findViewById(R.id.loginUsername);
        password = (TextInputEditText) findViewById(R.id.loginPassword);
        signIn = (Button) findViewById(R.id.signIn);




        registerHere = (TextView) findViewById(R.id.registerHere);
        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Register.class);
                startActivity(i);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(Login.this, "Username / Password required", Toast.LENGTH_SHORT).show();
                } else {
                    login(createLoginRequest());
                }
            }
        });


    }

    public LoginRequest createLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username.getText().toString());
        loginRequest.setPassword(password.getText().toString());

        return loginRequest;
    }

    public void login(LoginRequest loginRequest) {

//        ApiClient.token = null;
        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        Call<Token> call = apiInterface.loginUser(loginRequest);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {

                if(response.isSuccessful()){
                    Token token = response.body();
                    tokenn = "Token " + token.getToken();
                    Toast.makeText(Login.this,"Logged in successfully!" ,Toast.LENGTH_SHORT).show();
                    (Login.this).finishAffinity();
                    Intent i = new Intent(Login.this,MainActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(Login.this,"Login failed!",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(Login.this,"Throwable" + t.getLocalizedMessage() ,Toast.LENGTH_SHORT).show();

            }
        });



    }
}