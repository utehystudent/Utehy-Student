package com.example.utehystudent.model;

public class SubjectsOfSemester {
    private String class_ID, school_year;
    private int semester;

    public SubjectsOfSemester() {
    }

    public SubjectsOfSemester(String class_ID, String school_year, int semester) {
        this.class_ID = class_ID;
        this.school_year = school_year;
        this.semester = semester;
    }

    public String getClass_ID() {
        return class_ID;
    }

    public void setClass_ID(String class_ID) {
        this.class_ID = class_ID;
    }

    public String getSchool_year() {
        return school_year;
    }

    public void setSchool_year(String school_year) {
        this.school_year = school_year;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return "SubjectsOfSemester{" +
                "class_ID='" + class_ID + '\'' +
                ", school_year='" + school_year + '\'' +
                ", semester=" + semester +
                '}';
    }
}
