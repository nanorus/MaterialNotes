package com.example.nanorus.todo.model.pojo;

public class NoteRecyclerPojo {

    private String name;
    private DateTimePojo date;
    private int priority;

    public NoteRecyclerPojo(String name, DateTimePojo date, int priority) {
        this.name = name;
        this.date = date;
        this.priority = priority;
    }

    public NoteRecyclerPojo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTimePojo getDate() {
        return date;
    }

    public void setDate(DateTimePojo date) {
        this.date = date;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
