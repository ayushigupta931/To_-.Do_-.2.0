package com.example.todo20;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;
//    public static String token = null;
    public static Retrofit getRetrofit(){

        if(retrofit == null){

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

            retrofit = new Retrofit.Builder().baseUrl("https://todo-app-csoc.herokuapp.com/").addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();

        }

        return retrofit;
    }





}
