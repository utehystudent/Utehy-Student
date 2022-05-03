package com.example.utehystudent.model;

public class SubjectsOfSemester_Detail {
    private String class_ID, subject_ID;

    public SubjectsOfSemester_Detail() {
    }

    public SubjectsOfSemester_Detail(String class_ID, String subject_ID) {
        this.class_ID = class_ID;
        this.subject_ID = subject_ID;
    }

    public String getClass_ID() {
        return class_ID;
    }

    public void setClass_ID(String class_ID) {
        this.class_ID = class_ID;
    }

    public String getSubject_ID() {
        return subject_ID;
    }

    public void setSubject_ID(String subject_ID) {
        this.subject_ID = subject_ID;
    }

    @Override
    public String toString() {
        return "SubjectsOfSemester_Detail{" +
                "class_ID='" + class_ID + '\'' +
                ", subject_ID='" + subject_ID + '\'' +
                '}';
    }
}
