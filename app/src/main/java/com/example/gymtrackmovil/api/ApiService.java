package com.example.gymtrackmovil.api;

import com.example.gymtrackmovil.models.Goal;
import com.example.gymtrackmovil.models.LoginRequest;
import com.example.gymtrackmovil.models.LoginResponse;
import com.example.gymtrackmovil.models.ProgressEntry;
import com.example.gymtrackmovil.models.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("goals")
    Call<List<Goal>> getGoals();

    @POST("goals")
    Call<Void> addGoal(@Body Goal goal);

    @GET("progress")
    Call<List<ProgressEntry>> getProgress();

    @POST("progress")
    Call<Void> addProgress(@Body ProgressEntry progress);

    @GET("users")
    Call<List<User>> getAllUsers();
}
