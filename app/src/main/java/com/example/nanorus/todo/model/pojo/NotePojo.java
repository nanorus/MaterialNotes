package com.example.nanorus.todo.model.pojo;

public class NotePojo {

    private String name;
    private String date;
    private String description;
    private int priority;

    public NotePojo(String name, String date, String description, int priority) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
