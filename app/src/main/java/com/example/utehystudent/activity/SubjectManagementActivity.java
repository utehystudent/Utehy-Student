package com.example.utehystudent.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.SubjectManagementViewModel;
import com.example.utehystudent.adapters.SubjectAdapter;
import com.example.utehystudent.model.Subject;
import com.example.utehystudent.model.SubjectsOfSemester;

import java.util.ArrayList;

public class SubjectManagementActivity extends AppCompatActivity {
    TextView tvSchoolYear, tvSemester;
    Dialog dialog;
    Toolbar toolbar;
    RecyclerView rcv;
    SubjectAdapter subjectAdapter;
    ArrayList<Subject> SubjectsList;
    SubjectManagementViewModel subjectManagementViewModel;
    GridLayoutManager gridLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_management);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Init();
    }

    private void Init() {
        toolbar = findViewById(R.id.SubjectManagement_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("HỌC PHẦN");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSchoolYear = findViewById(R.id.SubjectManagement_tvSchoolYear);
        tvSemester = findViewById(R.id.SubjectManagement_tvSemester);
        rcv = findViewById(R.id.SubjectManagement_rcv);
        SubjectsList = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(SubjectManagementActivity.this, 2);
        rcv.setLayoutManager(gridLayoutManager);
        ShowLoadingDialog();

        subjectManagementViewModel = new ViewModelProvider(SubjectManagementActivity.this).get(SubjectManagementViewModel.class);
        subjectManagementViewModel.getSubjectOfSemesterLiveData().observe(this, new Observer<SubjectsOfSemester>() {
            @Override
            public void onChanged(SubjectsOfSemester subjectsOfSemester) {
                tvSchoolYear.setText("Năm học: "+subjectsOfSemester.getSchool_year());
                tvSemester.setText("Học kì: "+subjectsOfSemester.getSemester());
            }
        });
        subjectManagementViewModel.listSubjectLiveData.observe(this, new Observer<ArrayList<Subject>>() {
            @Override
            public void onChanged(ArrayList<Subject> subjects) {
                SubjectsList.clear();
                SubjectsList = subjects;
                subjectAdapter = new SubjectAdapter(SubjectsList);
                rcv.setAdapter(subjectAdapter);
                subjectAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

    }

    private void ShowLoadingDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_layout1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subject_management_option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}