package com.example.gymtrackmovil.models;

public class LoginResponse {
    @com.google.gson.annotations.SerializedName(value = "token", alternate = { "access_token" })
    private String token;
    private User user;

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}
