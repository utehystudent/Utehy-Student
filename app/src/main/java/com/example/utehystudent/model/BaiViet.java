package com.example.utehystudent.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class BaiViet implements Comparable<BaiViet>, Serializable {
    private String idBaiViet;
    private String idNguoiDang;
    private String tenNguoiDang;
    private String linkAnhNguoiDang;
    private String maLop;
    private String noiDung;
    private String ngayDang;
    private String timeStamp;
    private ArrayList<String> listLike;
    private int soBinhLuan;
    private ArrayList<String> linkAnh;

    public BaiViet() {

    }

    public BaiViet(String idBaiViet, String idNguoiDang, String tenNguoiDang, String linkAnhNguoiDang, String maLop, String noiDung, String ngayDang, ArrayList<String> listLike, int soBinhLuan, ArrayList<String> linkAnh) {
        this.idBaiViet = idBaiViet;
        this.idNguoiDang = idNguoiDang;
        this.tenNguoiDang = tenNguoiDang;
        this.linkAnhNguoiDang = linkAnhNguoiDang;
        this.maLop = maLop;
        this.noiDung = noiDung;
        this.ngayDang = ngayDang;
        this.timeStamp = getCurrentTimeStamp();
        this.listLike = listLike;
        this.soBinhLuan = soBinhLuan;
        this.linkAnh = linkAnh;
    }

    public BaiViet(String idBaiViet, String idNguoiDang, String tenNguoiDang, String linkAnhNguoiDang, String maLop, String noiDung, String ngayDang, String timeStamp, ArrayList<String> listLike, int soBinhLuan, ArrayList<String> linkAnh) {
        this.idBaiViet = idBaiViet;
        this.idNguoiDang = idNguoiDang;
        this.tenNguoiDang = tenNguoiDang;
        this.linkAnhNguoiDang = linkAnhNguoiDang;
        this.maLop = maLop;
        this.noiDung = noiDung;
        this.ngayDang = ngayDang;
        this.timeStamp = timeStamp;
        this.listLike = listLike;
        this.soBinhLuan = soBinhLuan;
        this.linkAnh = linkAnh;
    }

    public String getTenNguoiDang() {
        return tenNguoiDang;
    }

    public void setTenNguoiDang(String tenNguoiDang) {
        this.tenNguoiDang = tenNguoiDang;
    }

    public String getLinkAnhNguoiDang() {
        return linkAnhNguoiDang;
    }

    public void setLinkAnhNguoiDang(String linkAnhNguoiDang) {
        this.linkAnhNguoiDang = linkAnhNguoiDang;
    }

    public String getIdBaiViet() {
        return idBaiViet;
    }

    public void setIdBaiViet(String idBaiViet) {
        this.idBaiViet = idBaiViet;
    }

    public String getIdNguoiDang() {
        return idNguoiDang;
    }

    public void setIdNguoiDang(String idNguoiDang) {
        this.idNguoiDang = idNguoiDang;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getNgayDang() {
        return ngayDang;
    }

    public void setNgayDang(String ngayDang) {
        this.ngayDang = ngayDang;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ArrayList<String> getListLike() {
        return listLike;
    }

    public void setListLike(ArrayList<String> listLike) {
        this.listLike = listLike;
    }

    public int getSoBinhLuan() {
        return soBinhLuan;
    }

    public void setSoBinhLuan(int soBinhLuan) {
        this.soBinhLuan = soBinhLuan;
    }

    public ArrayList<String> getLinkAnh() {
        return linkAnh;
    }

    public void setLinkAnh(ArrayList<String> linkAnh) {
        this.linkAnh = linkAnh;
    }

    private String getCurrentTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return String.valueOf(timestamp.getTime());
    }

    @Override
    public String toString() {
        return "BaiViet{" +
                "idBaiViet='" + idBaiViet + '\'' +
                ", idNguoiDang='" + idNguoiDang + '\'' +
                ", tenNguoiDang='" + tenNguoiDang + '\'' +
                ", linkAnhNguoiDang='" + linkAnhNguoiDang + '\'' +
                ", maLop='" + maLop + '\'' +
                ", noiDung='" + noiDung + '\'' +
                ", ngayDang='" + ngayDang + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", listLike=" + listLike +
                ", soBinhLuan=" + soBinhLuan +
                ", linkAnh=" + linkAnh +
                '}';
    }

    @Override
    public int compareTo(BaiViet baiViet) {
        return baiViet.getTimeStamp().compareTo(this.getTimeStamp());
    }
}