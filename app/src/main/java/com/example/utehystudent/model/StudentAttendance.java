package com.example.utehystudent.model;

public class StudentAttendance {
    private String username, faculty_ID, class_ID, name, regency, avt_link;
    private Boolean isChosen;

    public StudentAttendance() {
    }

    public StudentAttendance(String username, String faculty_ID, String class_ID, String name, String regency, String avt_link, Boolean isChosen) {
        this.username = username;
        this.faculty_ID = faculty_ID;
        this.class_ID = class_ID;
        this.name = name;
        this.regency = regency;
        this.avt_link = avt_link;
        this.isChosen = isChosen;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFaculty_ID() {
        return faculty_ID;
    }

    public void setFaculty_ID(String faculty_ID) {
        this.faculty_ID = faculty_ID;
    }

    public String getClass_ID() {
        return class_ID;
    }

    public void setClass_ID(String class_ID) {
        this.class_ID = class_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegency() {
        return regency;
    }

    public void setRegency(String regency) {
        this.regency = regency;
    }

    public String getAvt_link() {
        return avt_link;
    }

    public void setAvt_link(String avt_link) {
        this.avt_link = avt_link;
    }

    public Boolean getChosen() {
        return isChosen;
    }

    public void setChosen(Boolean chosen) {
        isChosen = chosen;
    }

    @Override
    public String toString() {
        return "StudentAttendance{" +
                "username='" + username + '\'' +
                ", faculty_ID='" + faculty_ID + '\'' +
                ", class_ID='" + class_ID + '\'' +
                ", name='" + name + '\'' +
                ", regency='" + regency + '\'' +
                ", avt_link='" + avt_link + '\'' +
                ", isChosen=" + isChosen +
                '}';
    }
}
