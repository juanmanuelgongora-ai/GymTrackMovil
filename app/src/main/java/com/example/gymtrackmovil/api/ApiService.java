package com.example.gymtrackmovil.api;

import com.example.gymtrackmovil.models.LoginRequest;
import com.example.gymtrackmovil.models.LoginResponse;
import com.example.gymtrackmovil.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("register")
    Call<Void> register(@Body RegisterRequest request);

    @retrofit2.http.GET("routines")
    Call<java.util.List<com.example.gymtrackmovil.models.Routine>> getRoutines();
}
