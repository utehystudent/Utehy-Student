package com.example.utehystudent.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.AttendanceViewModel;
import com.example.utehystudent.adapters.StudentAttendanceAdapter;
import com.example.utehystudent.model.Subject;

import java.util.ArrayList;

public class AttendanceActivity extends AppCompatActivity {
    Toolbar toolbar;
    Spinner subjectSpinner;
    TextView tvSoTC;
    RadioGroup TimeRadioGroup;
    EditText edtTenGV;
    RecyclerView rcvStudents;
    ImageView imgChooseDate;
    StudentAttendanceAdapter studentAttendanceAdapter;
    ArrayAdapter<String> spinnerAdapter;
    ArrayList<Subject> subjectList;
    AttendanceViewModel attendanceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Init();
    }

    private void Init() {
        toolbar = findViewById(R.id.Attendance_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ĐIỂM DANH");
        toolbar.setNavigationOnClickListener(v -> finish());
        //
        subjectSpinner = findViewById(R.id.Attendance_spinner);
        tvSoTC = findViewById(R.id.Attendance_tvSoTC);
        TimeRadioGroup = findViewById(R.id.Attendance_radioGroup);
        edtTenGV = findViewById(R.id.Attendance_edtTenGV);
        rcvStudents = findViewById(R.id.Attendance_rcv);
        imgChooseDate = findViewById(R.id.Attendance_imgChooseDate);
        //
        attendanceViewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);
        attendanceViewModel.getListSubjectInTermLiveData().observe(this, subjects -> {
            subjectList = subjects;
            ArrayList<String> subjectNames = new ArrayList<>();
            subjectNames.add("Chọn môn học");
            for (Subject sj: subjects) {
                subjectNames.add(sj.getSubject_name());
            }
            spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subjectSpinner.setAdapter(spinnerAdapter);
        });
        Events();
    }

    private void Events() {
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    tvSoTC.setText("---");
                    return;
                }
                Subject subject = attendanceViewModel.GetSubjectInfo(i);
                tvSoTC.setText(subject.getNum_cred()+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attendance_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}