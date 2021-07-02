package com.example.todo20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    private TextInputEditText userName, name, email, password;
    private ImageView backToLoginPg;
    private Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backToLoginPg = (ImageView) findViewById(R.id.backToLoginPg);
        userName = (TextInputEditText) findViewById(R.id.registerUsername);
        name = (TextInputEditText) findViewById(R.id.registerName);
        email = (TextInputEditText) findViewById(R.id.email);
        password = (TextInputEditText) findViewById(R.id.registerPassword);
        register = (Button) findViewById(R.id.register);


        backToLoginPg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (Register.this).finishAffinity();
                Intent i1 = new Intent(Register.this,Login.class);
                startActivity(i1);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUser(createUserRequest());
            }
        });
    }




    public UserRequest createUserRequest(){
        UserRequest userRequest = new UserRequest();
        userRequest.setName(name.getText().toString());
        userRequest.setUsername(userName.getText().toString());
        userRequest.setEmail(email.getText().toString());
        userRequest.setPassword(password.getText().toString());

        return userRequest;
    }


    public void saveUser(UserRequest userRequest){
//        ApiClient.token = null;
        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        Call<Token> call = apiInterface.saveUser(userRequest);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.isSuccessful()){
                    Token token = response.body();
                    Login.tokenn = "Token " + token.getToken();
                    Toast.makeText(Register.this,"Registered Successfully!",Toast.LENGTH_SHORT).show();
                    (Register.this).finishAffinity();
                    Intent i = new Intent(Register.this,MainActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(Register.this,"Registration failed!",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(Register.this,"Throwable" + t.getLocalizedMessage() ,Toast.LENGTH_SHORT).show();
            }
        });




    }
}