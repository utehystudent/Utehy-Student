package com.example.utehystudent.model;

import java.io.Serializable;

public class User implements Comparable<User>, Serializable {
    private String username, faculty_ID, class_ID, name, regency, avt_link;

    public User() {
    }

    public User(String username, String faculty_ID, String class_ID, String name, String regency, String avt_link) {
        this.username = username;
        this.faculty_ID = faculty_ID;
        this.class_ID = class_ID;
        this.name = name;
        this.regency = regency;
        this.avt_link = avt_link;
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


    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", faculty_ID='" + faculty_ID + '\'' +
                ", class_ID='" + class_ID + '\'' +
                ", name='" + name + '\'' +
                ", regency='" + regency + '\'' +
                ", avt_link='" + avt_link + '\'' +
                '}';
    }

    @Override
    public int compareTo(User user) {
        int idxName1 = this.getName().lastIndexOf(' ');
        if (idxName1 == -1) {
            throw new IllegalArgumentException("Only a single name: " + this.getName());
        }
        String lastName1  = this.getName().substring(idxName1 + 1);
        idxName1 = user.getName().lastIndexOf(' ');
        if (idxName1 == -1) {
            throw new IllegalArgumentException("Only a single name: " + user.getName());
        }
        String lastName2  = user.getName().substring(idxName1 + 1);

        return lastName1.compareTo(lastName2);
    }
}
