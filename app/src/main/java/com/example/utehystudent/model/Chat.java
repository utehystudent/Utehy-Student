package com.example.utehystudent.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Chat implements Comparable<Chat>{
    private String idChat, username, linkImage, classID, noiDung, timeStamp;

    public Chat() {
    }

    public Chat(String idChat, String username, String linkImage, String classID, String noiDung) {
        this.idChat = idChat;
        this.username = username;
        this.linkImage = linkImage;
        this.classID = classID;
        this.noiDung = noiDung;
        this.timeStamp = getCurrentTimeStamp();
    }

    @Override
    public String toString() {
        return "Chat{" +
                "idChat='" + idChat + '\'' +
                ", username='" + username + '\'' +
                ", linkImage='" + linkImage + '\'' +
                ", classID='" + classID + '\'' +
                ", noiDung='" + noiDung + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNgayDang() {
        Timestamp ts = new Timestamp(Long.parseLong(this.getTimeStamp()));
        Date date = new Date(ts.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String strDate = formatter.format(date);

        //get current day
        Timestamp ts2 = new Timestamp(System.currentTimeMillis());
        Date date2 = new Date(ts2.getTime());
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
        formatter2.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String strDate2 = formatter2.format(date2);

        if (strDate.equalsIgnoreCase(strDate2)) {
            formatter = new SimpleDateFormat("HH:mm");
            return formatter.format(date);
        }else {
            formatter = new SimpleDateFormat("HH:mm dd/MM,yyyy");
            return formatter.format(date);
        }

//        return strDate;
    }

    private String getCurrentTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        return String.valueOf(timestamp.getTime());
    }

    @Override
    public int compareTo(Chat chat) {
        return this.getTimeStamp().compareTo(chat.getTimeStamp());
    }
}
