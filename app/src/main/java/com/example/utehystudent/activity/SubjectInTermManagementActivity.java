package com.example.utehystudent.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.SubjectInTermManagementViewModel;
import com.example.utehystudent.adapters.SubjectAdapter;
import com.example.utehystudent.model.Subject;
import com.example.utehystudent.model.SubjectsOfSemester;

import java.util.ArrayList;

public class SubjectInTermManagementActivity extends AppCompatActivity {
    TextView tvSchoolYear, tvSemester;
    Dialog dialog;
    Toolbar toolbar;
    RecyclerView rcv;
    SubjectAdapter subjectAdapter;
    ArrayList<Subject> SubjectsList;
    SubjectInTermManagementViewModel subjectInTermManagementViewModel;
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
        gridLayoutManager = new GridLayoutManager(SubjectInTermManagementActivity.this, 2);
        rcv.setLayoutManager(gridLayoutManager);

        ShowLoadingDialog();

        subjectInTermManagementViewModel = new ViewModelProvider(SubjectInTermManagementActivity.this).get(SubjectInTermManagementViewModel.class);
        subjectInTermManagementViewModel.getSubjectOfSemesterLiveData().observe(this, new Observer<SubjectsOfSemester>() {
            @Override
            public void onChanged(SubjectsOfSemester subjectsOfSemester) {
                tvSchoolYear.setText("Năm học: "+subjectsOfSemester.getSchool_year());
                tvSemester.setText("Học kì: "+subjectsOfSemester.getSemester());
            }
        });

        subjectInTermManagementViewModel.listSubjectInTermLiveData.observe(this, new Observer<ArrayList<Subject>>() {
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.SubjectOptionMenu_iconAdd:
                AddSubjectDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void AddSubjectDialog() {
        Dialog dialog_add_subject = new Dialog(this);
        dialog_add_subject.setContentView(R.layout.dialog_add_subject);
        dialog_add_subject.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvSoTC = dialog_add_subject.findViewById(R.id.DialogAddSubject_tvSoTC);
        Button btnThem = dialog_add_subject.findViewById(R.id.DialogAddSubject_btnThem);
        AutoCompleteTextView edtSubjectName = dialog_add_subject.findViewById(R.id.DialogAddSubject_edtTenMH);
        edtSubjectName.setThreshold(1);

        ArrayList<String> listSubjectName = new ArrayList<>();
        ArrayList<Subject> listSubject = new ArrayList<>();

        subjectInTermManagementViewModel.getListAllSubjectLiveData().observe(SubjectInTermManagementActivity.this, new Observer<ArrayList<Subject>>() {
            @Override
            public void onChanged(ArrayList<Subject> subjects) {
                Log.d("haha", "onChanged: "+subjects.size());
                listSubject.addAll(subjects);
                for (Subject sj : listSubject) {
                    listSubjectName.add(sj.getSubject_name()+" - Số TC: "+sj.getNum_cred());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(dialog.getContext(), android.R.layout.simple_dropdown_item_1line, listSubjectName);
                edtSubjectName.setAdapter(adapter);
            }
        });

        edtSubjectName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tvSoTC.setVisibility(View.VISIBLE);
                String s = edtSubjectName.getText().toString();
                String num = s.substring(s.lastIndexOf(":")+1).trim();
                String subject_name = s.substring(0, s.lastIndexOf("-")).trim();
                edtSubjectName.setText(subject_name);
                tvSoTC.setText("Số TC: "+num);
            }
        });

        dialog_add_subject.create();
        dialog_add_subject.show();
    }
}