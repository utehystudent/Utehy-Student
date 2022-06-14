package com.example.utehystudent.model;

public class Class {
    private String class_ID, faculty_ID, class_name, course;

    public Class() {
    }

    public Class(String class_ID, String faculty_ID, String class_name, String course) {
        this.class_ID = class_ID;
        this.faculty_ID = faculty_ID;
        this.class_name = class_name;
        this.course = course;
    }

    public String getClass_ID() {
        return class_ID;
    }

    public void setClass_ID(String class_ID) {
        this.class_ID = class_ID;
    }

    public String getFaculty_ID() {
        return faculty_ID;
    }

    public void setFaculty_ID(String faculty_ID) {
        this.faculty_ID = faculty_ID;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return this.getClass_name();
    }
}
