package com.example.utehystudent.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.StudentAttendance_History_Adapter;
import com.example.utehystudent.model.Attendance;
import com.example.utehystudent.model.StudentAttendance;
import com.example.utehystudent.model.Subject;
import com.example.utehystudent.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AttendanceHistoryDetail_Activity extends AppCompatActivity implements Serializable {
    Toolbar toolbar;
    FirebaseFirestore db;
    SharedPreferences pref;
    TextView tvTenMH, tvSoTC, tvNgay, tvTime;
    EditText edtTenGV;
    RecyclerView rcv;
    Attendance attendance;
    StudentAttendance_History_Adapter adapter;
    ArrayList<StudentAttendance> listStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_attendance_history_detail);

        Init();
        Events();
    }

    private void Init() {
        toolbar = findViewById(R.id.AttendanceHisDetail_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CHI TIẾT ĐIỂM DANH");
        toolbar.setNavigationOnClickListener(
                v -> {
                    Intent it = new Intent(this, AttendanceHistory_Activity.class);
                    startActivity(it);
                    finish();
                }
        );
        //
        db = FirebaseFirestore.getInstance();
        pref = getSharedPreferences("User", Context.MODE_PRIVATE);
        //
        tvTenMH = findViewById(R.id.AttendanceHisDetail_tvTenMH);
        tvSoTC = findViewById(R.id.AttendanceHisDetail_tvSoTC);
        tvNgay = findViewById(R.id.AttendanceHisDetail_tvNgay);
        tvTime = findViewById(R.id.AttendanceHisDetail_tvTime);
        edtTenGV = findViewById(R.id.AttendanceHisDetail_edtTenGV);
        //
        rcv = findViewById(R.id.AttendanceHisDetail_rcv);

        //set data for views
        setData();
    }

    private void setData() {
        Intent intent = getIntent();
        attendance = (Attendance) intent.getSerializableExtra("attendance");
        String subID = attendance.getSubject_ID();
        db.collection("Subject")
                .whereEqualTo("subject_ID", subID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                tvTenMH.setText(doc.toObject(Subject.class).getSubject_name());
                                tvSoTC.setText(doc.toObject(Subject.class).getNum_cred()+"");
                            }
                        }else {
                            tvTenMH.setText("Môn học không còn tồn tại");
                            tvSoTC.setText("N/A");
                            return;
                        }
                    }
                });
        tvNgay.setText(attendance.getAttendance_Date());
        tvTime.setText(attendance.getTime());
        edtTenGV.setText(attendance.getTeacher_Name());

        ArrayList<String> listAbsent = attendance.getList_Absent();
        listStudent = new ArrayList<>();

        //get all student of class
        String classID = pref.getString("class_ID", "");
        db.collection("User")
                .whereEqualTo("class_ID", classID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                Boolean isAttend = true;
                                User user = doc.toObject(User.class);
                                if (listAbsent.contains(user.getUsername())) {
                                    isAttend = false;
                                }
                                listStudent.add(new StudentAttendance(user.getUsername(), user.getFaculty_ID(), user.getClass_ID(), user.getName(), user.getRegency(), user.getAvt_link(), isAttend));
                            }
                            Collections.sort(listStudent);
                            adapter = new StudentAttendance_History_Adapter(AttendanceHistoryDetail_Activity.this, listStudent);
                            rcv.setAdapter(adapter);
                        }
                    }
                });
    }

    private void Events() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ct_diemdanh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_ctdiemdanh_itXong:
                saveData();
                break;
            case R.id.menu_ctdiemdanh_itXoa:
                deleteAttendance();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAttendance() {
        db.collection("Attendance")
                .whereEqualTo("attendance_ID", attendance.getAttendance_ID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                String id = doc.getId();
                                //remove
                                db.collection("Attendance")
                                        .document(id)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AttendanceHistoryDetail_Activity.this, "Xóa phiếu điểm danh thành công", Toast.LENGTH_SHORT).show();
                                                    AttendanceHistoryDetail_Activity.this.finish();
                                                }else {
                                                    Toast.makeText(AttendanceHistoryDetail_Activity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }
                                        });
                            }
                        }else {
                            Toast.makeText(AttendanceHistoryDetail_Activity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }

    private void saveData() {
        db.collection("Attendance")
                .whereEqualTo("attendance_ID", attendance.getAttendance_ID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                String id = doc.getId();
                                ArrayList<String> list = new ArrayList<>();
                                for (StudentAttendance s : listStudent) {
                                    if (s.getChosen() == false) {
                                        list.add(s.getUsername());
                                    }
                                }
                                db.collection("Attendance")
                                        .document(id)
                                        .update("list_Absent", list)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AttendanceHistoryDetail_Activity.this, "Lưu lại thành công", Toast.LENGTH_SHORT).show();
                                                    AttendanceHistoryDetail_Activity.this.finish();
                                                }else {
                                                    Toast.makeText(AttendanceHistoryDetail_Activity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }
                                        });
                            }
                        }else {
                            Toast.makeText(AttendanceHistoryDetail_Activity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}