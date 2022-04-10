package com.example.utehystudent.model;

public class Schedule {
    private String class_ID, dateApply;

    public Schedule() {
    }

    public Schedule(String class_ID, String dateApply) {
        this.class_ID = class_ID;
        this.dateApply = dateApply;
    }

    public String getClass_ID() {
        return class_ID;
    }

    public void setClass_ID(String class_ID) {
        this.class_ID = class_ID;
    }

    public String getDateApply() {
        return dateApply;
    }

    public void setDateApply(String dateApply) {
        this.dateApply = dateApply;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "class_ID='" + class_ID + '\'' +
                ", dateApply='" + dateApply + '\'' +
                '}';
    }
}
