package com.example.gymtrackmovil.models;

import com.google.gson.annotations.SerializedName;

public class Routine {
    private int id;
    private String name;
    private String description;

    // Campos extra que puede tener la API de Hostinger
    @SerializedName("duration_minutes")
    private int durationMinutes;

    @SerializedName("calories_burned")
    private int caloriesBurned;

    @SerializedName("exercise_count")
    private int exerciseCount;

    // Día de la semana (lunes, martes, etc.) – puede venir de la API o calcularse
    @SerializedName("day_label")
    private String dayLabel;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getDurationMinutes() { return durationMinutes; }
    public int getCaloriesBurned() { return caloriesBurned; }
    public int getExerciseCount() { return exerciseCount; }
    public String getDayLabel() { return dayLabel; }

    // Constructor principal
    public Routine(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
