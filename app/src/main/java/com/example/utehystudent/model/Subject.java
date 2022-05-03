package com.example.utehystudent.model;

public class Subject {
    private String faculty_ID, subject_ID, subject_name;
    private int num_cred;

    public Subject() {
    }

    public Subject(String faculty_ID, String subject_ID, String subject_name, int num_cred) {
        this.faculty_ID = faculty_ID;
        this.subject_ID = subject_ID;
        this.subject_name = subject_name;
        this.num_cred = num_cred;
    }

    public String getFaculty_ID() {
        return faculty_ID;
    }

    public void setFaculty_ID(String faculty_ID) {
        this.faculty_ID = faculty_ID;
    }

    public String getSubject_ID() {
        return subject_ID;
    }

    public void setSubject_ID(String subject_ID) {
        this.subject_ID = subject_ID;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public int getNum_cred() {
        return num_cred;
    }

    public void setNum_cred(int num_cred) {
        this.num_cred = num_cred;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "faculty_ID='" + faculty_ID + '\'' +
                ", subject_ID='" + subject_ID + '\'' +
                ", subject_name='" + subject_name + '\'' +
                ", num_cred=" + num_cred +
                '}';
    }
}
