package com.example.utehystudent.model;

public class Contact {
    private String faculty_ID, department, name, position, email, phone, avt_link;

    public Contact(String faculty_ID, String department, String name, String position, String email, String phone, String avt_link) {
        this.faculty_ID = faculty_ID;
        this.department = department;
        this.name = name;
        this.position = position;
        this.email = email;
        this.phone = phone;
        this.avt_link = avt_link;
    }

    public Contact() {
    }

    @Override
    public String toString() {
        return "Contact{" +
                "faculty_ID='" + faculty_ID + '\'' +
                ", department='" + department + '\'' +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", avt_link='" + avt_link + '\'' +
                '}';
    }

    public String getFaculty_ID() {
        return faculty_ID;
    }

    public void setFaculty_ID(String faculty_ID) {
        this.faculty_ID = faculty_ID;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvt_link() {
        return avt_link;
    }

    public void setAvt_link(String avt_link) {
        this.avt_link = avt_link;
    }
}


