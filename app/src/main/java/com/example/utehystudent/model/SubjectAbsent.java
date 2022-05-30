package com.example.utehystudent.model;

public class SubjectAbsent implements Comparable<SubjectAbsent>{
    private String subject_ID, subject_Name;
    private int num_Absent, num_Cred;

    public SubjectAbsent() {
    }

    public SubjectAbsent(String subject_ID, String subject_Name, int num_Absent, int num_Cred) {
        this.subject_ID = subject_ID;
        this.subject_Name = subject_Name;
        this.num_Absent = num_Absent;
        this.num_Cred = num_Cred;
    }

    public String getSubject_ID() {
        return subject_ID;
    }

    public void setSubject_ID(String subject_ID) {
        this.subject_ID = subject_ID;
    }

    public String getSubject_Name() {
        return subject_Name;
    }

    public void setSubject_Name(String subject_Name) {
        this.subject_Name = subject_Name;
    }

    public int getNum_Absent() {
        return num_Absent;
    }

    public void setNum_Absent(int num_Absent) {
        this.num_Absent = num_Absent;
    }

    public int getNum_Cred() {
        return num_Cred;
    }

    public void setNum_Cred(int num_Cred) {
        this.num_Cred = num_Cred;
    }

    @Override
    public String toString() {
        return "SubjectAbsent{" +
                "subject_ID='" + subject_ID + '\'' +
                ", subject_Name='" + subject_Name + '\'' +
                ", num_Absent=" + num_Absent +
                ", num_Cred=" + num_Cred +
                '}';
    }

    @Override
    public int compareTo(SubjectAbsent subjectAbsent) {
        return this.getSubject_Name().compareTo(subjectAbsent.subject_Name);
    }
}
