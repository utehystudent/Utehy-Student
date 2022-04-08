package com.example.utehystudent.model;

public class Faculty {
    private String faculty_ID, faculty_name;

    public Faculty() {
    }

    public Faculty(String faculty_ID, String faculty_name) {
        this.faculty_ID = faculty_ID;
        this.faculty_name = faculty_name;
    }

    public String getFaculty_ID() {
        return faculty_ID;
    }

    public void setFaculty_ID(String faculty_ID) {
        this.faculty_ID = faculty_ID;
    }

    public String getFaculty_name() {
        return faculty_name;
    }

    public void setFaculty_name(String faculty_name) {
        this.faculty_name = faculty_name;
    }
}
