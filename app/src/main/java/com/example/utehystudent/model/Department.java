package com.example.utehystudent.model;

public class Department {
    private String faculty_ID, department_id, department_name;

    public Department(String faculty_ID, String department_id, String department_name) {
        this.faculty_ID = faculty_ID;
        this.department_id = department_id;
        this.department_name = department_name;
    }

    public Department() {
    }

    @Override
    public String toString() {
        return "Department{" +
                "faculty_ID='" + faculty_ID + '\'' +
                ", department_id='" + department_id + '\'' +
                ", department_name='" + department_name + '\'' +
                '}';
    }

    public String getFaculty_ID() {
        return faculty_ID;
    }

    public void setFaculty_ID(String faculty_ID) {
        this.faculty_ID = faculty_ID;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }
}
