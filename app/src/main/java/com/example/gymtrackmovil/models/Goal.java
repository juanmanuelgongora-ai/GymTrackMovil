package com.example.gymtrackmovil.models;

import com.google.gson.annotations.SerializedName;

public class Goal {
    @SerializedName(value = "titulo", alternate = { "title", "name", "nombre" })
    private String title;
    @SerializedName(value = "meta", alternate = { "target", "description", "descripcion" })
    private String target;
    @SerializedName(value = "progreso", alternate = { "progress", "percentage" })
    private int progress;
    @SerializedName(value = "fecha_limite", alternate = { "deadline", "end_date", "expiry" })
    private String deadline;

    public Goal(String title, String target, int progress, String deadline) {
        this.title = title;
        this.target = target;
        this.progress = progress;
        this.deadline = deadline;
    }

    public String getTitle() {
        return title;
    }

    public String getTarget() {
        return target;
    }

    public int getProgress() {
        return progress;
    }

    public String getDeadline() {
        return deadline;
    }
}
