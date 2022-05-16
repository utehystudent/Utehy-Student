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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;

public class SubjectInTermManagementActivity extends AppCompatActivity {
    static BottomSheetDialog bottomSheetDialog;
    TextView tvSchoolYear, tvSemester;
    Dialog loadingDialog, dialog_add_subject;
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
        toolbar.setNavigationOnClickListener(v -> finish());
        tvSchoolYear = findViewById(R.id.SubjectManagement_tvSchoolYear);
        tvSemester = findViewById(R.id.SubjectManagement_tvSemester);
        rcv = findViewById(R.id.SubjectManagement_rcv);
        subjectsList = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(SubjectInTermManagementActivity.this, 2);
        rcv.setLayoutManager(gridLayoutManager);

        bottomSheetDialog = new BottomSheetDialog(SubjectInTermManagementActivity.this);

        subjectInTermManagementViewModel = new ViewModelProvider(SubjectInTermManagementActivity.this).get(SubjectInTermManagementViewModel.class);
        subjectInTermManagementViewModel.getSubjectOfSemesterLiveData().observe(this, subjectsOfSemester -> {
            tvSchoolYear.setText("Năm học: " + subjectsOfSemester.getSchool_year());
            tvSemester.setText("Học kì: " + subjectsOfSemester.getSemester());
        });

        CreateLoadingDialog();

        subjectInTermManagementViewModel.listSubjectInTermLiveData.observe(this, new Observer<ArrayList<Subject>>() {
            @Override
            public void onChanged(ArrayList<Subject> subjects) {
                loadingDialog.show();
                subjectsList.clear();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (subjectsList.size() == 0) {
                            subjectsList = subjects;
                            subjectAdapter = new SubjectAdapter(SubjectInTermManagementActivity.this, subjectsList);
                            rcv.setAdapter(subjectAdapter);
                            subjectAdapter.notifyDataSetChanged();
                            loadingDialog.dismiss();
                            handler.removeCallbacks(this::run);
                        } else {
                            handler.postDelayed(this, 500);
                        }
                    }
                }, 500);
            }
        });
    }

    private void CreateLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_layout1);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadingDialog.create();
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
        dialog_add_subject = new Dialog(this);
        dialog_add_subject.setContentView(R.layout.dialog_add_subject);
        dialog_add_subject.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvSoTC = dialog_add_subject.findViewById(R.id.DialogAddSubject_tvSoTC);
        TextView tvMaMH = dialog_add_subject.findViewById(R.id.DialogAddSubject_tvMaMH);
        Button btnThem = dialog_add_subject.findViewById(R.id.DialogAddSubject_btnThem);
        AutoCompleteTextView edtSubjectName = dialog_add_subject.findViewById(R.id.DialogAddSubject_edtTenMH);
        edtSubjectName.setThreshold(1);

        ArrayList<String> listSubjectName = new ArrayList<>();
        ArrayList<Subject> listSubject = new ArrayList<>();

        subjectInTermManagementViewModel.getListAllSubjectLiveData().observe(SubjectInTermManagementActivity.this, subjects -> {
            listSubject.addAll(subjects);
            for (Subject sj : listSubject) {
                listSubjectName.add(sj.getSubject_name() + " - Số TC: " + sj.getNum_cred());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(loadingDialog.getContext(), android.R.layout.simple_dropdown_item_1line, listSubjectName);
            edtSubjectName.setAdapter(adapter);
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
            AddSubject(subjectName);
            loadingDialog.dismiss();
        });

        edtSubjectName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Subject subject = subjectInTermManagementViewModel.GetSubjectInfo(edtSubjectName.getText().toString());
                if (subject.getSubject_ID() != null) {
                    tvSoTC.setText("Số TC: " + subject.getNum_cred() + "");
                    tvMaMH.setText("Mã MH: " + subject.getSubject_ID() + " (" + subject.getFaculty_ID() + ")");
                } else {
                    tvSoTC.setText("Số TC: N/A");
                    tvMaMH.setText("Mã môn học: N/A");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        dialog_add_subject.create();
        dialog_add_subject.show();
    }

    private void AddSubject(String subjectName) {
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
        alert.setNegativeButton("KHÔNG", (dialogInterface, i) -> dialogInterface.dismiss());
        alert.setPositiveButton("CÓ", (dialogInterface, i) -> {
            subjectInTermManagementViewModel.AddSubjectToTerm(subject);
            dialogInterface.dismiss();
            CloseDialog(dialog_add_subject);
            Toast.makeText(SubjectInTermManagementActivity.this, "Thêm môn học thành công!", Toast.LENGTH_SHORT).show();
        });
        alert.create();
        alert.show();
    }

    public void LongClickItemSubject(Subject subject) {
        showBottomSheetDialog(subject);
    }

    public void showBottomSheetDialog(Subject subject) {
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_subject_management);
        Button btnXoa = bottomSheetDialog.findViewById(R.id.bottomSheetSubject_btnXoa);
        Button btnXemLS = bottomSheetDialog.findViewById(R.id.bottomSheetSubject_btnXemLS);
        TextView tvTenMH = bottomSheetDialog.findViewById(R.id.bottomSheetSubject_tvTenMH);
        TextView tvSoTC = bottomSheetDialog.findViewById(R.id.bottomSheetSubject_tvSoTC);
        tvTenMH.setText(subject.getSubject_name() + "");
        tvSoTC.setText("Số TC: " + subject.getNum_cred() + "");

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialogXoaMonHoc();
            }

            private void ShowDialogXoaMonHoc() {
                AlertDialog.Builder alert = new AlertDialog.Builder(bottomSheetDialog.getContext());
                alert.setMessage("Bạn có chắc muốn xóa môn học này không?");
                alert.setNegativeButton("KHÔNG", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
                alert.setPositiveButton("CÓ", (dialogInterface, i) -> {
                    subjectInTermManagementViewModel.DeleteSubjectFromTerm(subject.getSubject_ID());
                    dialogInterface.dismiss();
                    CloseDialog(bottomSheetDialog);
                });
                alert.create();
                alert.show();
            }
        });
        bottomSheetDialog.show();
    }

    public void CloseDialog(Dialog dialog) {
        dialog.dismiss();
    }
}