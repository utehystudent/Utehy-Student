package com.example.utehystudent.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.AttendanceViewModel;
import com.example.utehystudent.adapters.StudentAttendanceAdapter;
import com.example.utehystudent.model.Attendance;
import com.example.utehystudent.model.StudentAttendance;
import com.example.utehystudent.model.Subject;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AttendanceActivity extends AppCompatActivity {
    final String TAG = "AttendanceActivity";
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
    ArrayList<StudentAttendance> listStudentAttendance;
    AttendanceViewModel attendanceViewModel;
    Subject subject_chose;
    String timeAttendance;
    String time2;
    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;
    String classID, studentID, subject_ID, date, time, attendance_ID;

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
            listStudentAttendance = new ArrayList<>();
            listStudentAttendance.addAll(studentAttendances);
            studentAttendanceAdapter = new StudentAttendanceAdapter(AttendanceActivity.this,listStudentAttendance);
            rcvStudents.setAdapter(studentAttendanceAdapter);
        });

        attendanceViewModel.getIsAttendanceExisted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isExisted) {
                if (isExisted == true) {
                    Toast.makeText(AttendanceActivity.this, "Môn học đã được điểm danh vào ngày hôm nay. Vui lòng vào lịch sử để chỉnh sửa !", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(AttendanceActivity.this);
                    alert.setMessage("Xác nhận lưu bảng điểm danh ?");
                    alert.setPositiveButton("OK", (dialogInterface, i) -> CreateAttendance());
                    alert.setNegativeButton("HỦY", (dialogInterface, i) -> dialogInterface.dismiss());
                    alert.create().show();
                }
            }
        });

        attendanceViewModel.getIsCreateStressful().observe(this, value -> {
            if (value == true) {
                ShowSuccessSnackBar("Lưu bảng điểm danh thành công !");
                return;
            }
            Toast.makeText(this, "Đã xảy ra lỗi. Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
        });

        Events();
    }

    private void CreateAttendance() {
        Attendance attendance = new Attendance();
        attendance.setAttendance_ID(attendance_ID);
        attendance.setClass_ID(classID);
        attendance.setSubject_ID(subject_ID);
        attendance.setAttendance_Date(date);
        attendance.setStudent_Made(studentID);
        attendance.setTeacher_Name(edtTenGV.getText().toString());
        attendance.setTime(time);
        ArrayList<String> listStudentAbsent = new ArrayList<>();
        for (StudentAttendance student : listStudentAttendance) {
            if (student.getChosen() == false) {
                listStudentAbsent.add(student.getUsername());
            }
        }
        attendance.setList_Absent(listStudentAbsent);
        attendanceViewModel.CreateAttendance(attendance);
        Log.d("AttendanceActivity", "CreateAttendance: "+attendance.toString());
        subjectSpinner.setSelection(0);
        edtTenGV.setText("");
        attendanceViewModel.ResetListStudentAttendance();
    }

    public void SetCheckedStudent(String studentID, Boolean checked) {
        for (StudentAttendance student : listStudentAttendance) {
            if (student.getUsername().equals(studentID)) {
                student.setChosen(checked);
                Log.d("AttendanceActivity", "SetCheckedStudent: "+student.toString());
                break;
            }
        }
    }

    private void SetCurrentTimeToRadioGroup() {
        switch (attendanceViewModel.GetCurrentTimeInDay()) {
            case 1:
                timeRadioGroup.check(R.id.Attendance_radioSang);
                timeAttendance = "S";
                time2 = "SÁNG";
                break;
            case 2:
                timeRadioGroup.check(R.id.Attendance_radioChieu);
                timeAttendance = "C";
                time2 = "CHIỀU";
                break;
            case 3:
                timeRadioGroup.check(R.id.Attendance_radioToi);
                timeAttendance = "T";
                time2 = "TỐI";
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
                    subject_chose = new Subject("", "", "", 0);
                    return;
                }
                subject_chose = attendanceViewModel.GetSubjectInfo(i);
                tvSoTC.setText(subject_chose.getNum_cred() + "");
                Toast.makeText(AttendanceActivity.this, ""+subject_chose.getSubject_ID(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tvSoTC.setText("---");
                subject_chose = new Subject("", "", "", 0);
            }
        });

        imgChooseDate.setOnClickListener(view -> ShowDatePickerDialog());

        timeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.Attendance_radioSang:
                        timeAttendance = "S";
                        time2 = "SÁNG";
                        break;
                    case R.id.Attendance_radioChieu:
                        timeAttendance = "C";
                        time2 = "CHIỀU";
                        break;
                    case R.id.Attendance_radioToi:
                        timeAttendance = "T";
                        time2 = "TỐI";
                        break;
                }
            }
        });
    }

    private void ShowDatePickerDialog() {
        // Date Select Listener.
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
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
        switch (item.getItemId()) {
            case R.id.AttendanceMenu_icXong:
                SaveAttendance();
                break;
            case R.id.AttendanceMenu_icXemLSDiemDanh:
                startActivity(new Intent(AttendanceActivity.this, AttendanceHistory_Activity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowSuccessSnackBar(String message){
        View view = findViewById(R.id.AttendanceActivity);
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        TextView textView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.snack_bar_text));
        snackBarView.setBackgroundColor(getResources().getColor(R.color.snack_bar_background));
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_white, 0, 0, 0);
        snackbar.show();
    }

    private void SaveAttendance() {
        if (subject_chose.getSubject_ID().equals("")) {
            Toast.makeText(this, "Vui lòng chọn môn học cần điểm danh", Toast.LENGTH_SHORT).show();
            return;
        }
        if (edtTenGV.getText().toString().equals("")) {
            edtTenGV.setError("Nhập tên giảng viên");
            return;
        }
        //get current classID
        SharedPreferences pref = this.getSharedPreferences("User", Context.MODE_PRIVATE);

        try {
            classID = pref.getString("class_ID", null);
            studentID = pref.getString("username", null);
        } catch (NullPointerException e) {
            Toast.makeText(this, "Đã xảy ra lỗi! Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "SaveAttendance: Lỗi lấy mã lớp hoặc mã sinh viên");
            return;
        }

        subject_ID = subject_chose.getSubject_ID();
        date = tvNgay.getText().toString().trim();
        time = time2;
        Log.d("xxx", "SaveAttendance: day:" + selectedDayOfMonth + " month: " + selectedMonth);
        String d = tvNgay.getText().toString().replace("-", "");
        attendance_ID = "DD" + classID + subject_ID + "" + d + "" + timeAttendance;
        //Check if this subject in this day is made before
        attendanceViewModel.CheckExistedAttendance(attendance_ID);
    }
}