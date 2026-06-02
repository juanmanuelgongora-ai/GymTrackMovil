package com.example.gymtrackmovil.models;

public class Routine {
    private int id;
    private String name;
    private String description;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Routine(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
