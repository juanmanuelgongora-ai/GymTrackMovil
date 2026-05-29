package com.example.gymtrackmovil.models;

public class LoginResponse {
    private String token;
    private User user;

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public static class User {
        @com.google.gson.annotations.SerializedName("nombre")
        private String name;

        @com.google.gson.annotations.SerializedName("email")
        private String email;

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}
