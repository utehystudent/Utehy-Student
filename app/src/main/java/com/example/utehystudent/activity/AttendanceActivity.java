package com.example.utehystudent.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.AttendanceViewModel;
import com.example.utehystudent.adapters.StudentAttendanceAdapter;
import com.example.utehystudent.model.Subject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AttendanceActivity extends AppCompatActivity {
    Toolbar toolbar;
    Spinner subjectSpinner;
    TextView tvSoTC, tvNgay;
    RadioGroup timeRadioGroup;
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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ĐIỂM DANH");
        toolbar.setNavigationOnClickListener(v -> finish());
        //
        subjectSpinner = findViewById(R.id.Attendance_spinner);
        tvSoTC = findViewById(R.id.Attendance_tvSoTC);
        tvNgay = findViewById(R.id.Attendance_tvDate);
        timeRadioGroup = findViewById(R.id.Attendance_radioGroup);
        edtTenGV = findViewById(R.id.Attendance_edtTenGV);
        rcvStudents = findViewById(R.id.Attendance_rcv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvStudents.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvStudents.addItemDecoration(itemDecoration);
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
                        handler.removeCallbacks(this);
                    } else {
                        handler.postDelayed(this, 500);
                    }
                }
            }, 500);
        });
        tvNgay.setText(attendanceViewModel.GetCurrentDate());

        //set current time to radio group
        SetCurrentTimeToRadioGroup();

        //set data student to recycle view
        attendanceViewModel.getListStudentAttendanceLiveData().observe(this, studentAttendances -> {
            Log.d("xxx", "Init: "+studentAttendances.size());
            studentAttendanceAdapter = new StudentAttendanceAdapter(studentAttendances);
            rcvStudents.setAdapter(studentAttendanceAdapter);
        });

        Events();
    }

    private void SetCurrentTimeToRadioGroup() {
        switch (attendanceViewModel.GetCurrentTimeInDay()) {
            case 1:
                timeRadioGroup.check(R.id.Attendance_radioSang);
                break;
            case 2:
                timeRadioGroup.check(R.id.Attendance_radioChieu);
                break;
            case 3:
                timeRadioGroup.check(R.id.Attendance_radioToi);
                break;
            default:
        }
    }

    private void Events() {
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
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

        imgChooseDate.setOnClickListener(view -> ShowDatePickerDialog());
    }

    private void ShowDatePickerDialog() {
        // Date Select Listener.
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = format.format(calendar.getTime());
            tvNgay.setText(strDate);

            selectedDayOfMonth = dayOfMonth;
            selectedMonth = monthOfYear;
            selectedYear = year;
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