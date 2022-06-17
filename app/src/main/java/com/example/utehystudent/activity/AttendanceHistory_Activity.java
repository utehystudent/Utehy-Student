package com.example.utehystudent.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.AttendanceHistory_Adapter;
import com.example.utehystudent.model.Attendance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

public class AttendanceHistory_Activity extends AppCompatActivity implements Serializable {
    Toolbar toolbar;
    Spinner spnMH;
    TextView tvNgay;
    ImageView imgXoaNgay;
    RecyclerView rcv;
    ArrayList<Attendance> listAttendance;
    AttendanceHistory_Adapter adapter;
    ArrayList<String> listSubjectInTerm;
    SharedPreferences pref;
    FirebaseFirestore db;
    SpinnerAdapter spnAdapter;
    String dateChose = "";
    String subjectChose = "";
    String classID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_attendance_history);

        Init();
        getListSubjectOfClass();
        Events();
    }

    private void Events() {
        spnMH.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    listAttendance.clear();
                    adapter.setData(listAttendance);
                    subjectChose = "all";
                }else {
                    String subject = listSubjectInTerm.get(i);
                    String subID = subject.substring(0, subject.indexOf("-"));
                    subjectChose = subID;
                }
                getAttendance(subjectChose);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvNgay.setOnClickListener(view -> {
            openDatePickerDialog();
        });

        imgXoaNgay.setOnClickListener(view -> {
            tvNgay.setText("Chọn ngày");
            dateChose = "";
        });


    }

    private void getAttendance(String subjectChose) {
        tvNgay.setText("Chọn ngày");
        dateChose = "";
        if (subjectChose.equals("all")) {
            getAllAttendance();
        }else {
            getAttendanceBySubjectID(subjectChose);
        }
    }

    private void getAllAttendance() {
        listAttendance.clear();
        db.collection("Attendance")
                .whereEqualTo("class_ID", classID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                listAttendance.add(doc.toObject(Attendance.class));
                            }
                            Collections.sort(listAttendance);
                            adapter.setData(listAttendance);
                            rcv.setAdapter(adapter);
                        }else {
                            return;
                        }
                    }
                });
    }

    private void openDatePickerDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_pickdate);
        DatePicker datePicker = dialog.findViewById(R.id.dialogDatePicker_datePicker);
        Button btnOK = dialog.findViewById(R.id.dialogDatePicker_btnOK);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int[] year = {calendar.get(Calendar.YEAR)};
        final int[] month = {calendar.get(Calendar.MONTH)};
        final int[] day = {calendar.get(Calendar.DAY_OF_MONTH)};

        datePicker.init(year[0], month[0], day[0], new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int y, int m, int d) {
                day[0] = d;
                year[0] = y;
                month[0] = m;
            }
        });

        btnOK.setOnClickListener(view -> {
            dialog.dismiss();

            calendar.set(year[0], month[0], day[0]);

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            dateChose = format.format(calendar.getTime());

            tvNgay.setText("Chọn ngày: "+dateChose);

            attendanceFilter();
        });

        dialog.show();
    }

    private void attendanceFilter() {
        listAttendance.clear();
        db.collection("Attendance")
                .whereEqualTo("class_ID", classID)
                .whereEqualTo("attendance_Date", dateChose)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                listAttendance.add(doc.toObject(Attendance.class));
                            }
                            Collections.sort(listAttendance);
                            adapter.setData(listAttendance);
                            rcv.setAdapter(adapter);
                        }else {
                            return;
                        }
                    }
                });
    }

    private void getAttendanceBySubjectID(String subID) {
        listAttendance.clear();
        db.collection("Attendance")
                .whereEqualTo("class_ID", classID)
                .whereEqualTo("subject_ID", subID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                listAttendance.add(doc.toObject(Attendance.class));
                            }
                            Collections.sort(listAttendance);
                            adapter.setData(listAttendance);
                            rcv.setAdapter(adapter);
                        }else {
                            return;
                        }
                    }
                });
    }

    private void getListSubjectOfClass() {
        listSubjectInTerm = new ArrayList<>();
        listSubjectInTerm.add("Tất cả môn học");
        db.collection("SubjectsOfSemester_Detail")
                .whereEqualTo("class_ID", classID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            String subID = doc.getString("subject_ID");
                            db.collection("Subject")
                                    .whereEqualTo("subject_ID", subID)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            for (DocumentSnapshot doc1 : task1.getResult()) {
                                                String name = doc1.getString("subject_name");
                                                listSubjectInTerm.add(subID+"-"+name);
                                            }
                                        }else { }
                                    });
                        }
                        spnAdapter = new ArrayAdapter<>(AttendanceHistory_Activity.this, android.R.layout.simple_spinner_dropdown_item, listSubjectInTerm);
                        spnMH.setAdapter(spnAdapter);
                    }else { return; }
                });
    }

    private void Init() {
        toolbar = findViewById(R.id.AttendanceHis_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("LỊCH SỬ ĐIỂM DANH");
        toolbar.setNavigationOnClickListener(v -> finish());
        //
        db = FirebaseFirestore.getInstance();
        //
        pref = getSharedPreferences("User", Context.MODE_PRIVATE);
        classID = pref.getString("class_ID", "");
        //
        spnMH = findViewById(R.id.AttendanceHis_spinnerMon);
        tvNgay = findViewById(R.id.AttendanceHis_tvDate);
        imgXoaNgay = findViewById(R.id.AttendanceHis_imgDeleteDate);
        //

        rcv = findViewById(R.id.AttendanceHis_rcv);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcv.addItemDecoration(itemDecoration);
        listAttendance = new ArrayList<>();
        adapter = new AttendanceHistory_Adapter(this);
        adapter.setData(listAttendance);
    }

    public void viewDetail(Attendance attendance) {
        Intent intent = new Intent(this, AttendanceHistoryDetail_Activity.class);
        intent.putExtra("attendance", attendance);
        startActivity(intent);
    }
}