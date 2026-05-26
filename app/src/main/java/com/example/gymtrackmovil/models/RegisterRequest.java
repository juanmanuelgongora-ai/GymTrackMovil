package com.example.gymtrackmovil.models;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private int age;
    private String sex;
    private String eps;
    private String goal;
    private String address;
    private String phone;
    private String familyPhone;

    public RegisterRequest(String name, String email, String password, int age, String sex, String eps, String goal,
            String address, String phone, String familyPhone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.sex = sex;
        this.eps = eps;
        this.goal = goal;
        this.address = address;
        this.phone = phone;
        this.familyPhone = familyPhone;
    }
}
