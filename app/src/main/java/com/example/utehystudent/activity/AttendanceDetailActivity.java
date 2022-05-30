package com.example.utehystudent.activity;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.AttendanceDetailAdapter;
import com.example.utehystudent.model.Attendance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AttendanceDetailActivity extends AppCompatActivity {
    private static final String TAG = "detail";
    Toolbar toolbar;
    ArrayList<Attendance> attendances;
    RecyclerView rcv;
    AttendanceDetailAdapter attendanceDetailAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init();
    }

    private void Init() {
        toolbar = findViewById(R.id.AttendanceDetail_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CHI TIẾT ĐIỂM DANH");
        toolbar.setNavigationOnClickListener(v -> finish());
        //
        rcv = findViewById(R.id.AttendanceDetail_rcv);
        //
        attendances = (ArrayList<Attendance>) getIntent().getSerializableExtra("attendances");
        String subjectID = getIntent().getStringExtra("subject_ID");
        //
        ArrayList<Attendance> listAttendance = new ArrayList<>();
        for (Attendance attendance : attendances) {
            if (attendance.getSubject_ID().equals(subjectID)) {
                listAttendance.add(attendance);
            }
        }
        Collections.sort(listAttendance);
        attendanceDetailAdapter = new AttendanceDetailAdapter(this, listAttendance);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcv.addItemDecoration(itemDecoration);
        rcv.setAdapter(attendanceDetailAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}