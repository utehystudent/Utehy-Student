package com.example.utehystudent.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Attendance implements Serializable, Comparable<Attendance>{

    private String attendance_ID, class_ID, subject_ID, student_Made, attendance_Date, teacher_Name, time;
    private ArrayList<String> list_Absent;

    public Attendance() {
    }

    public Attendance(String attendance_ID, String class_ID, String subject_ID, String student_Made, String attendance_Date, String teacher_Name, String time, ArrayList<String> list_Absent) {
        this.attendance_ID = attendance_ID;
        this.class_ID = class_ID;
        this.subject_ID = subject_ID;
        this.student_Made = student_Made;
        this.attendance_Date = attendance_Date;
        this.teacher_Name = teacher_Name;
        this.list_Absent = list_Absent;
        this.time = time;
    }

    public String getAttendance_ID() {
        return attendance_ID;
    }

    public void setAttendance_ID(String attendance_ID) {
        this.attendance_ID = attendance_ID;
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

    public String getStudent_Made() {
        return student_Made;
    }

    public void setStudent_Made(String student_Made) {
        this.student_Made = student_Made;
    }

    public String getAttendance_Date() {
        return attendance_Date;
    }

    public void setAttendance_Date(String attendance_Date) {
        this.attendance_Date = attendance_Date;
    }

    public String getTeacher_Name() {
        return teacher_Name;
    }

    public void setTeacher_Name(String teacher_Name) {
        this.teacher_Name = teacher_Name;
    }

    public ArrayList<String> getList_Absent() {
        return list_Absent;
    }

    public void setList_Absent(ArrayList<String> list_Absent) {
        this.list_Absent = list_Absent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendance_ID='" + attendance_ID + '\'' +
                ", class_ID='" + class_ID + '\'' +
                ", subject_ID='" + subject_ID + '\'' +
                ", student_Made='" + student_Made + '\'' +
                ", attendance_Date='" + attendance_Date + '\'' +
                ", teacher_Name='" + teacher_Name + '\'' +
                ", time='" + time + '\'' +
                ", list_Absent=" + list_Absent +
                '}';
    }

    @Override
    public int compareTo(Attendance attendance) {
        Date date1 = new Date();
        Date date2 = new Date();
        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy").parse(this.getAttendance_Date());
            date2 = new SimpleDateFormat("dd-MM-yyyy").parse(attendance.getAttendance_Date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date2.compareTo(date1);
    }


}
