package com.example.gymtrackmovil.models;

import com.google.gson.annotations.SerializedName;

public class ProgressEntry {
    @SerializedName(value = "peso", alternate = { "weight", "weight_kg" })
    private double weight;
    @SerializedName(value = "estatura", alternate = { "height", "height_cm" })
    private double height;
    @SerializedName(value = "grasa_corporal", alternate = { "grasa", "body_fat", "fat_percentage" })
    private double bodyFat;
    @SerializedName(value = "masa_muscular", alternate = { "musculo", "muscle_mass", "muscle" })
    private double muscleMass;
    @SerializedName(value = "fecha", alternate = { "date", "created_at" })
    private String date;

    public ProgressEntry(double weight, double height, double bodyFat, double muscleMass, String date) {
        this.weight = weight;
        this.height = height;
        this.bodyFat = bodyFat;
        this.muscleMass = muscleMass;
        this.date = date;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public double getBodyFat() {
        return bodyFat;
    }

    public double getMuscleMass() {
        return muscleMass;
    }

    public String getDate() {
        return date;
    }
}
