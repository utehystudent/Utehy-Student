package com.example.utehystudent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.AttendanceHistory_Activity;
import com.example.utehystudent.model.Attendance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AttendanceHistory_Adapter extends RecyclerView.Adapter<AttendanceHistory_Adapter.AttendanceHistoryViewHolder> {

    Context context;
    ArrayList<Attendance> listAttendance;
    FirebaseFirestore db;

    public AttendanceHistory_Adapter(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    public void setData(ArrayList<Attendance> list) {
        this.listAttendance = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AttendanceHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_attendance_history, parent, false);
        return new AttendanceHistory_Adapter.AttendanceHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceHistoryViewHolder holder, int position) {
        Attendance attendance = listAttendance.get(position);
        if (attendance == null) {
            return;
        }
        //get subject name
        db.collection("Subject")
                .whereEqualTo("subject_ID", attendance.getSubject_ID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                holder.tvTenMH.setText(doc.getString("subject_name"));
                            }
                        } else {
                            holder.tvTenMH.setText("Lỗi hiển thị");
                            return;
                        }
                    }
                });

        //set text date

        holder.tvNgay.setText(attendance.getTime()+" "+attendance.getAttendance_Date());

        //set so nguoi nghi
        holder.tvSoNghi.setText("Số người nghỉ: " + attendance.getList_Absent().size());

        holder.item.setOnClickListener(view ->{
            ((AttendanceHistory_Activity) context).viewDetail(attendance);
        });
    }

    @Override
    public int getItemCount() {
        if (listAttendance.size() != 0) {
            return listAttendance.size();
        }
        return 0;
    }

    public class AttendanceHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTenMH, tvNgay, tvSoNghi;
        private LinearLayout item;
        public AttendanceHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTenMH = itemView.findViewById(R.id.rowAttendanceHis_tvTenMH);
            tvNgay = itemView.findViewById(R.id.rowAttendanceHis_tvNgay);
            tvSoNghi = itemView.findViewById(R.id.rowAttendanceHis_tvSoNghi);
            item = itemView.findViewById(R.id.rowAttendanceHis_item);
        }
    }

}
