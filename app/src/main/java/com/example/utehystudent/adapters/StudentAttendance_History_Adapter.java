package com.example.utehystudent.adapters;

import android.app.Activity;
import android.graphics.Color;
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
import com.example.utehystudent.model.StudentAttendance;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StudentAttendance_History_Adapter extends RecyclerView.Adapter<StudentAttendance_History_Adapter.StudentAttendance_HistoryViewHolder> {
    final String TAG = "StudentAttendanceHistoryAdapter";
    ArrayList<StudentAttendance> studentList;
    Activity activity;

    public StudentAttendance_History_Adapter(Activity activity, ArrayList<StudentAttendance> studentList) {
        this.activity = activity;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentAttendance_History_Adapter.StudentAttendance_HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_student_attendance, parent, false);
        return new StudentAttendance_History_Adapter.StudentAttendance_HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAttendance_HistoryViewHolder holder, int position) {
        StudentAttendance student = studentList.get(position);
        if(student == null) {
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

        Boolean isAttendance = student.getChosen();
        if (isAttendance == false) {
            holder.tvName.setTextColor(Color.RED);
        }else {
            holder.tvName.setTextColor(Color.parseColor("#2F80ED"));
        }

        holder.ckb.setChecked(student.getChosen());

        holder.ckb.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b == true) {
                holder.tvName.setTextColor(Color.parseColor("#2F80ED"));
                student.setChosen(true);
            }else {
                holder.tvName.setTextColor(Color.RED);
                student.setChosen(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(studentList != null) {
            return studentList.size();
        }
        return 0;
    }

    public class StudentAttendance_HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvID;
        private ImageView imgAvt;
        private CheckBox ckb;
        public StudentAttendance_HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.rowStudentAttendance_tvName);
            tvID = itemView.findViewById(R.id.rowStudentAttendance_tvID);
            imgAvt = itemView.findViewById(R.id.rowStudentAttendance_imgAvt);
            ckb = itemView.findViewById(R.id.rowStudentAttendance_ckb);
        }
    }
}
