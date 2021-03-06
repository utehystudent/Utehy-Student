package com.example.utehystudent.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.AttendanceActivity;
import com.example.utehystudent.model.StudentAttendance;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.StudentAttendanceViewHolder>{
    final String TAG = "StudentAttendanceAdapter";
    ArrayList<StudentAttendance> studentList;
    Activity activity;

    public StudentAttendanceAdapter(Activity activity, ArrayList<StudentAttendance> studentList) {
        this.activity = activity;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentAttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_student_attendance, parent, false);
        return new StudentAttendanceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StudentAttendanceViewHolder holder, int position) {
        StudentAttendance student = studentList.get(position);
        if(student == null) {
            Log.d(TAG, "onBindViewHolder: NULL");
            return;
        }
        try {
            Picasso.get().load(student.getAvt_link()).resize(270, 270).centerCrop().into(holder.imgAvt);
        }catch (Exception e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
            holder.imgAvt.setImageResource(R.drawable.ic_student);
        }
        holder.tvName.setText(student.getName());
        holder.tvID.setText("MSV: " + student.getUsername());
        holder.ckb.setChecked(true);
        holder.ckb.setOnCheckedChangeListener((compoundButton, b) -> {
            String studentID = student.getUsername()+"";
            ((AttendanceActivity) this.activity).SetCheckedStudent(studentID, b);
        });
    }

    @Override
    public int getItemCount() {
        if(studentList!=null) {
            return studentList.size();
        }
        return 0;
    }

    public class StudentAttendanceViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvID;
        private ImageView imgAvt;
        private CheckBox ckb;
        public StudentAttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.rowStudentAttendance_tvName);
            tvID = itemView.findViewById(R.id.rowStudentAttendance_tvID);
            imgAvt = itemView.findViewById(R.id.rowStudentAttendance_imgAvt);
            ckb = itemView.findViewById(R.id.rowStudentAttendance_ckb);
        }
    }
}
