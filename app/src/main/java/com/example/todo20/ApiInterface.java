package com.example.todo20;

import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("todo/")
    Call<List<TasksResponse>> getAllTasks(@Header("Authorization") String token);

    @POST("todo/create/")
    Call<ResponseBody> createTask(@Header("Authorization") String token,@Body TaskRequest task);

    @GET("todo/{id}/")
    Call<TasksResponse> getTaskById(@Header("Authorization") String token, @Path("id") int id);

    @PATCH("todo/{id}/")
    Call<TasksResponse> patchTask(@Header("Authorization") String token,@Body TaskRequest task,@Path("id") int id );

    @DELETE("todo/{id}/")
    Call<ResponseBody> deleteTask(@Header("Authorization") String token, @Path("id") int id);

    @POST("auth/register/")
    Call<Token> saveUser(@Body UserRequest userRequest);

    @POST("auth/login/")
    Call<Token> loginUser(@Body LoginRequest loginRequest);

    @GET("auth/profile/")
    Call<UserProfile> getUserProfile(@Header("Authorization") String token);


}
