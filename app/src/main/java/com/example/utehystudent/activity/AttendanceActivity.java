package com.example.utehystudent.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class AttendanceActivity extends AppCompatActivity {
    Toolbar toolbar;
    Spinner subjectSpinner;
    TextView tvSoTC, tvNgay;
    RadioGroup TimeRadioGroup;
    EditText edtTenGV;
    RecyclerView rcvStudents;
    ImageView imgChooseDate;
    StudentAttendanceAdapter studentAttendanceAdapter;
    ArrayAdapter<String> spinnerAdapter;
    ArrayList<Subject> subjectList;
    AttendanceViewModel attendanceViewModel;
    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;

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
        tvNgay = findViewById(R.id.Attendance_tvDate);
        TimeRadioGroup = findViewById(R.id.Attendance_radioGroup);
        edtTenGV = findViewById(R.id.Attendance_edtTenGV);
        rcvStudents = findViewById(R.id.Attendance_rcv);
        imgChooseDate = findViewById(R.id.Attendance_imgChooseDate);
        //
        LocalDate currentDate = LocalDate.now();
        selectedYear = currentDate.getYear();
        selectedMonth = currentDate.getMonthValue() - 1;
        selectedDayOfMonth = currentDate.getDayOfMonth();
        //
        attendanceViewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);
        attendanceViewModel.getListSubjectInTermLiveData().observe(this, subjects -> {
            ArrayList<String> subjectNames = new ArrayList<>();
            subjectNames.add("Chọn môn học");
            subjectList = new ArrayList<>();
            spinnerAdapter = new ArrayAdapter<>(AttendanceActivity.this, android.R.layout.simple_spinner_item, subjectNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subjectSpinner.setAdapter(spinnerAdapter);
            subjectList.clear();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (subjectList.size() == 0) {
                        subjectList = subjects;
                        for (Subject sj : subjects) {
                            subjectNames.add(sj.getSubject_name());
                        }
                        spinnerAdapter.notifyDataSetChanged();
                        handler.removeCallbacks(this::run);
                    } else {
                        handler.postDelayed(this, 500);
                    }
                }
            }, 500);
        });
        tvNgay.setText(attendanceViewModel.GetCurrentDate());
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
                tvSoTC.setText(subject.getNum_cred() + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tvSoTC.setText("---");
            }
        });

        imgChooseDate.setOnClickListener(view -> {
            ShowDatePickerDialog();
        });
    }

    private void ShowDatePickerDialog() {
        // Date Select Listener.
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                String strDate = format.format(calendar.getTime());
                tvNgay.setText(strDate);

                selectedDayOfMonth = dayOfMonth;
                selectedMonth = monthOfYear;
                selectedYear = year;
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                dateSetListener, selectedYear, selectedMonth, selectedDayOfMonth);
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.show();
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