package com.example.utehystudent.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    ArrayList<Subject> subjectsList;
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
        subjectsList = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(SubjectInTermManagementActivity.this, 2);
        rcv.setLayoutManager(gridLayoutManager);

        ShowLoadingDialog();

        subjectInTermManagementViewModel = new ViewModelProvider(SubjectInTermManagementActivity.this).get(SubjectInTermManagementViewModel.class);
        subjectInTermManagementViewModel.getSubjectOfSemesterLiveData().observe(this, new Observer<SubjectsOfSemester>() {
            @Override
            public void onChanged(SubjectsOfSemester subjectsOfSemester) {
                tvSchoolYear.setText("Năm học: " + subjectsOfSemester.getSchool_year());
                tvSemester.setText("Học kì: " + subjectsOfSemester.getSemester());
            }
        });

        subjectInTermManagementViewModel.listSubjectInTermLiveData.observe(this, new Observer<ArrayList<Subject>>() {
            @Override
            public void onChanged(ArrayList<Subject> subjects) {
                subjectsList.clear();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (subjectsList.size() == 0) {
                            subjectsList = subjects;
                            subjectAdapter = new SubjectAdapter(subjectsList);
                            rcv.setAdapter(subjectAdapter);
                            subjectAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                            handler.removeCallbacks(this::run);
                        }else {
                            handler.postDelayed(this, 500);
                        }
                    }
                }, 500);
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
        getMenuInflater().inflate(R.menu.subject_management_option_menu, menu);
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
        TextView tvMaMH = dialog_add_subject.findViewById(R.id.DialogAddSubject_tvMaMH);
        Button btnThem = dialog_add_subject.findViewById(R.id.DialogAddSubject_btnThem);
        AutoCompleteTextView edtSubjectName = dialog_add_subject.findViewById(R.id.DialogAddSubject_edtTenMH);
        edtSubjectName.setThreshold(1);

        ArrayList<String> listSubjectName = new ArrayList<>();
        ArrayList<Subject> listSubject = new ArrayList<>();

        subjectInTermManagementViewModel.getListAllSubjectLiveData().observe(SubjectInTermManagementActivity.this, new Observer<ArrayList<Subject>>() {
            @Override
            public void onChanged(ArrayList<Subject> subjects) {
                listSubject.addAll(subjects);
                for (Subject sj : listSubject) {
                    listSubjectName.add(sj.getSubject_name() + " - Số TC: " + sj.getNum_cred());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(dialog.getContext(), android.R.layout.simple_dropdown_item_1line, listSubjectName);
                edtSubjectName.setAdapter(adapter);
            }
        });

        edtSubjectName.setOnItemClickListener((adapterView, view, i, l) -> {
            String s = edtSubjectName.getText().toString();
            String subject_name = s.substring(0, s.lastIndexOf("-")).trim();
            edtSubjectName.setText(subject_name);
        });

        btnThem.setOnClickListener(view -> {
            if (edtSubjectName.getText().toString().equals("")) {
                edtSubjectName.setError("Thông tin trống!");
                return;
            }
            String subjectName = edtSubjectName.getText().toString();
            AddSubject(subjectName, dialog);
            dialog.dismiss();
        });

        edtSubjectName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Subject subject = subjectInTermManagementViewModel.GetSubjectInfo(edtSubjectName.getText().toString());
                if (subject.getSubject_ID() != null) {
                    tvSoTC.setText("Số TC: "+subject.getNum_cred()+"");
                    tvMaMH.setText("Mã MH: "+subject.getSubject_ID()+" ("+subject.getFaculty_ID()+")");
                }else {
                    tvSoTC.setText("Số TC: N/A");
                    tvMaMH.setText("Mã môn học: N/A");
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dialog_add_subject.create();
        dialog_add_subject.show();
    }

    private void AddSubject(String subjectName, Dialog dialog1) {
        if (subjectInTermManagementViewModel.CheckSubjectExistedInTerm(subjectName, subjectsList)) {
            Toast.makeText(SubjectInTermManagementActivity.this, "Môn học đã có trong kì học hiện tại !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!subjectInTermManagementViewModel.CheckSubjectExistInListAll(subjectName)) {
            Toast.makeText(SubjectInTermManagementActivity.this, "Môn học không tồn tại !", Toast.LENGTH_SHORT).show();
            return;
        }

        Subject subject = subjectInTermManagementViewModel.GetSubjectInfo(subjectName);
        AlertDialog.Builder alert = new AlertDialog.Builder(SubjectInTermManagementActivity.this);
        alert.setMessage("Bạn có chắc muốn thêm môn học này không?");
        alert.setNegativeButton("KHÔNG", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        alert.setPositiveButton("CÓ", (dialogInterface, i) -> {
            subjectInTermManagementViewModel.AddSubjectToTerm(subject);
            dialogInterface.dismiss();
            Toast.makeText(SubjectInTermManagementActivity.this, "Thêm môn học thành công!", Toast.LENGTH_SHORT).show();
        });
        alert.create();
        alert.show();
    }
}