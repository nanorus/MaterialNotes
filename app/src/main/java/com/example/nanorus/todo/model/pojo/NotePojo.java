package com.example.nanorus.todo.model.pojo;

public class NotePojo {

    private String mName;
    private DateTimePojo mDateTimePojo;
    private String mDescription;
    private int mPriority;

    public NotePojo(String name, DateTimePojo dateTimePojo, String description, int priority) {
        this.mName = name;
        this.mDateTimePojo = dateTimePojo;
        this.mDescription = description;
        this.mPriority = priority;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public DateTimePojo getDateTimePojo() {
        return mDateTimePojo;
    }

    public void setDateTimePojo(DateTimePojo dateTimePojo) {
        mDateTimePojo = dateTimePojo;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int priority) {
        this.mPriority = priority;
    }
}
