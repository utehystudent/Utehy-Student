package com.example.utehystudent.model;

public class ThoiKhoaBieu {
    private String thu, sang, chieu, toi;

    public ThoiKhoaBieu() {
    }

    public ThoiKhoaBieu(String thu, String sang, String chieu, String toi) {
        this.thu = thu;
        this.sang = sang;
        this.chieu = chieu;
        this.toi = toi;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public String getSang() {
        return sang;
    }

    public void setSang(String sang) {
        this.sang = sang;
    }

    public String getChieu() {
        return chieu;
    }

    public void setChieu(String chieu) {
        this.chieu = chieu;
    }

    public String getToi() {
        return toi;
    }

    public void setToi(String toi) {
        this.toi = toi;
    }

    @Override
    public String toString() {
        return "ThoiKhoaBieu{" +
                "thu='" + thu + '\'' +
                ", sang='" + sang + '\'' +
                ", chieu='" + chieu + '\'' +
                ", toi='" + toi + '\'' +
                '}';
    }
}
