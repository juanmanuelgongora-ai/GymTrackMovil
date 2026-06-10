package com.example.gymtrackmovil.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("nombre")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName(value = "role", alternate = { "rol" })
    private String role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
