package com.example.utehystudent.model;

public class Schedule_detail{
    private String morning;
    private String afternoon;
    private String evening;

    public Schedule_detail() {
    }

    public Schedule_detail(String morning, String afternoon, String evening) {
        this.morning = morning;
        this.afternoon = afternoon;
        this.evening = evening;
    }

    public String getEvening() {
        return evening;
    }

    public void setEvening(String evening) {
        this.evening = evening;
    }

    public String getMorning() {
        return morning;
    }

    public void setMorning(String morning) {
        this.morning = morning;
    }

    public String getAfternoon() {
        return afternoon;
    }

    public void setAfternoon(String afternoon) {
        this.afternoon = afternoon;
    }

    @Override
    public String toString() {
        return "Schedule_detail{" +
                "morning='" + morning + '\'' +
                ", afternoon='" + afternoon + '\'' +
                '}';
    }
}
