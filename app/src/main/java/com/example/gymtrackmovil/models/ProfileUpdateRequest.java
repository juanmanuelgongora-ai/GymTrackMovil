package com.example.gymtrackmovil.models;

import com.google.gson.annotations.SerializedName;

public class ProfileUpdateRequest {
    @SerializedName("nombre")
    private String name;
    @SerializedName("apellido")
    private String lastName;
    @SerializedName("telefono")
    private String phone;
    @SerializedName("direccion")
    private String address;

    public ProfileUpdateRequest(String name, String lastName, String phone, String address) {
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
    }
}
