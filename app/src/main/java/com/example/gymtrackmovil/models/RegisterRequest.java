package com.example.gymtrackmovil.models;
import com.google.gson.annotations.SerializedName;
public class RegisterRequest {
    @SerializedName("nombre")
    private String name;
    @SerializedName("apellido")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("password_confirmation")
    private String password_confirmation;
    @SerializedName("edad")
    private int age;
    @SerializedName("sexo")
    private String sex;
    @SerializedName("eps")
    private String eps;
    @SerializedName("objetivo")
    private String goal;
    @SerializedName("direccion")
    private String address;
    @SerializedName("telefono")
    private String phone;
    @SerializedName("telefono_familiar")
    private String familyPhone;
    public RegisterRequest(String name, String lastName, String email, String password, String password_confirmation,
            int age,
            String sex, String eps, String goal,
            String address, String phone, String familyPhone) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
        this.age = age;
        this.sex = sex;
        this.eps = eps;
        this.goal = goal;
        this.address = address;
        this.phone = phone;
        this.familyPhone = familyPhone;
    }
}

