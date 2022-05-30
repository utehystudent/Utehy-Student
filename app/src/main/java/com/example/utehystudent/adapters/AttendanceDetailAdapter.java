package com.example.utehystudent.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.utehystudent.R;
import com.example.utehystudent.model.Attendance;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class AttendanceDetailAdapter extends RecyclerView.Adapter<AttendanceDetailAdapter.AttendanceDetailViewHolder> {
    final String TAG = "AttendanceDetailAdapter";
    SharedPreferences pref;
    Activity activity;
    private ArrayList<Attendance> attendances;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public AttendanceDetailAdapter(Activity activity, ArrayList<Attendance> attendances) {
        this.activity = activity;
        this.attendances = attendances;
        pref = activity.getSharedPreferences("User", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public AttendanceDetailAdapter.AttendanceDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_attendance_detail, parent, false);
        return new AttendanceDetailAdapter.AttendanceDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceDetailViewHolder holder, int position) {
        Attendance attendance = attendances.get(position);
        if (attendance == null) {
            return;
        }
        String studentID = pref.getString("username", "");
        String date = attendance.getAttendance_Date();
        String time = attendance.getTime();
        String status = "";
        if (attendance.getList_Absent().contains(studentID)) {
            status = "Nghỉ";
            holder.tvInfo.setTextColor(Color.parseColor("#f51000"));
            holder.tvStatus.setTextColor(Color.parseColor("#f51000"));
        }else {
            status = "Có mặt";
            holder.tvInfo.setTextColor(Color.parseColor("#1a5e2d"));
            holder.tvStatus.setTextColor(Color.parseColor("#1a5e2d"));
        }
        holder.tvInfo.setText(getDayString(attendance.getAttendance_Date())+"  |  Ngày "+date+"  |  "+time);
        holder.tvStatus.setText(status);
    }


    @Override
    public int getItemCount() {
        if (attendances != null) {
            return attendances.size();
        }
        return 0;
    }

    public class AttendanceDetailViewHolder extends RecyclerView.ViewHolder {
        private TextView tvInfo, tvStatus;

        public AttendanceDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfo = itemView.findViewById(R.id.row_attendance_detail_tvInfo);
            tvStatus = itemView.findViewById(R.id.row_attendance_detail_tvTinhTrang);
        }
    }

    public String getDayString(String s){
        String dayVietnamese = "";
        Locale locale = Locale.getDefault();
        LocalDate date = LocalDate.parse(s, DateTimeFormatter.ofPattern( "dd-MM-yyyy"));
        DayOfWeek day = date.getDayOfWeek();
        String dayString = day.getDisplayName(TextStyle.FULL, locale);
        switch (dayString.toLowerCase()) {
            case "monday":
                dayVietnamese = "T2";
                break;
            case "tuesday":
                dayVietnamese = "T3";
                break;
            case "wednesday":
                dayVietnamese = "T4";
                break;
            case "thursday":
                dayVietnamese = "T5";
                break;
            case "friday":
                dayVietnamese = "T6";
                break;
            case "saturday":
                dayVietnamese = "T7";
                break;
            case "sunday":
                dayVietnamese = "CN";
                break;
        }
        return dayVietnamese;
    }
}
