package com.example.utehystudent.model;

import java.sql.Timestamp;

public class Comment implements Comparable<Comment>{
    private String idBaiViet, idNguoiCmt, linkAnhNguoiCmt, noiDung, tenNguoiCmt, thoiGian, timeStamp;

    public Comment(String idBaiViet, String idNguoiCmt, String linkAnhNguoiCmt, String noiDung, String tenNguoiCmt, String thoiGian) {
        this.idBaiViet = idBaiViet;
        this.idNguoiCmt = idNguoiCmt;
        this.linkAnhNguoiCmt = linkAnhNguoiCmt;
        this.noiDung = noiDung;
        this.tenNguoiCmt = tenNguoiCmt;
        this.thoiGian = thoiGian;
        this.timeStamp = getCurrentTimeStamp();
    }

    public Comment(String idBaiViet, String idNguoiCmt, String linkAnhNguoiCmt, String noiDung, String tenNguoiCmt, String thoiGian, String timeStamp) {
        this.idBaiViet = idBaiViet;
        this.idNguoiCmt = idNguoiCmt;
        this.linkAnhNguoiCmt = linkAnhNguoiCmt;
        this.noiDung = noiDung;
        this.tenNguoiCmt = tenNguoiCmt;
        this.thoiGian = thoiGian;
        this.timeStamp = timeStamp;
    }

    public Comment() {
    }

    public String getIdBaiViet() {
        return idBaiViet;
    }

    public void setIdBaiViet(String idBaiViet) {
        this.idBaiViet = idBaiViet;
    }

    public String getIdNguoiCmt() {
        return idNguoiCmt;
    }

    public void setIdNguoiCmt(String idNguoiCmt) {
        this.idNguoiCmt = idNguoiCmt;
    }

    public String getLinkAnhNguoiCmt() {
        return linkAnhNguoiCmt;
    }

    public void setLinkAnhNguoiCmt(String linkAnhNguoiCmt) {
        this.linkAnhNguoiCmt = linkAnhNguoiCmt;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getTenNguoiCmt() {
        return tenNguoiCmt;
    }

    public void setTenNguoiCmt(String tenNguoiCmt) {
        this.tenNguoiCmt = tenNguoiCmt;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "idBaiViet='" + idBaiViet + '\'' +
                ", idNguoiCmt='" + idNguoiCmt + '\'' +
                ", linkAnhNguoiCmt='" + linkAnhNguoiCmt + '\'' +
                ", noiDung='" + noiDung + '\'' +
                ", tenNguoiCmt='" + tenNguoiCmt + '\'' +
                ", thoiGian='" + thoiGian + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }

    private String getCurrentTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return String.valueOf(timestamp.getTime());
    }

    @Override
    public int compareTo(Comment comment) {
        return comment.getTimeStamp().compareTo(this.getTimeStamp());
    }
}
