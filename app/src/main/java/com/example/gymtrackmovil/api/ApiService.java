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
    @retrofit2.http.PUT("me/perfil")
    Call<Void> updateProfile(@Body com.example.gymtrackmovil.models.ProfileUpdateRequest request);
    @retrofit2.http.GET("rutinas/latest")
    Call<java.util.List<com.example.gymtrackmovil.models.Routine>> getRoutines();
    @retrofit2.http.GET("metricas")
    Call<java.util.List<com.example.gymtrackmovil.models.ProgressEntry>> getProgress();
    @retrofit2.http.POST("metricas")
    Call<Void> addProgress(@Body com.example.gymtrackmovil.models.ProgressEntry entry);
    @retrofit2.http.GET("hitos")
    Call<java.util.List<com.example.gymtrackmovil.models.Goal>> getGoals();
    @retrofit2.http.POST("hitos")
    Call<Void> addGoal(@Body com.example.gymtrackmovil.models.Goal goal);
}

