package com.example.utehystudent.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Comment implements Comparable<Comment> {
    private String idBaiViet, idComment, idNguoiCmt, linkAnhNguoiCmt, noiDung, tenNguoiCmt, timeStamp;

    public Comment(String idBaiViet, String idComment, String idNguoiCmt, String linkAnhNguoiCmt, String noiDung, String tenNguoiCmt) {
        this.idBaiViet = idBaiViet;
        this.idComment = idComment;
        this.idNguoiCmt = idNguoiCmt;
        this.linkAnhNguoiCmt = linkAnhNguoiCmt;
        this.noiDung = noiDung;
        this.tenNguoiCmt = tenNguoiCmt;
        this.timeStamp = getCurrentTimeStamp();
    }

    public Comment(String idBaiViet, String idComment, String idNguoiCmt, String linkAnhNguoiCmt, String noiDung, String tenNguoiCmt, String timeStamp) {
        this.idBaiViet = idBaiViet;
        this.idComment = idComment;
        this.idNguoiCmt = idNguoiCmt;
        this.linkAnhNguoiCmt = linkAnhNguoiCmt;
        this.noiDung = noiDung;
        this.tenNguoiCmt = tenNguoiCmt;
        this.timeStamp = timeStamp;
    }


    public Comment() {
    }

    public String getIdComment() {
        return idComment;
    }

    public void setIdComment(String idComment) {
        this.idComment = idComment;
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
                ", idComment='" + idComment + '\'' +
                ", idNguoiCmt='" + idNguoiCmt + '\'' +
                ", linkAnhNguoiCmt='" + linkAnhNguoiCmt + '\'' +
                ", noiDung='" + noiDung + '\'' +
                ", tenNguoiCmt='" + tenNguoiCmt + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }

    private String getCurrentTimeStamp() {
        /*Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return String.valueOf(timestamp.getTime());*/
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        return String.valueOf(timestamp.getTime());
    }

    @Override
    public int compareTo(Comment comment) {
        return comment.getTimeStamp().compareTo(this.getTimeStamp());
    }

    public String getNgayCmt() {
        Timestamp ts = new Timestamp(Long.parseLong(this.getTimeStamp()));
        Date date = new Date(ts.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm 'ngày' dd/MM, yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String strDate = formatter.format(date);
        return strDate;
    }
}
