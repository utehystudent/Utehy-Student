package com.example.utehystudent.model;

public class Activity {
    private String class_ID, date, content;

    public Activity() {
    }

    public Activity(String class_ID, String date, String content) {
        this.class_ID = class_ID;
        this.date = date;
        this.content = content;
    }

    public String getClass_ID() {
        return class_ID;
    }

    public void setClass_ID(String class_ID) {
        this.class_ID = class_ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "class_ID='" + class_ID + '\'' +
                ", date='" + date + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
