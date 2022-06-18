package com.example.utehystudent.activity.admin;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.QLMonHoc_Adapter;
import com.example.utehystudent.model.Faculty;
import com.example.utehystudent.model.Subject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class QLMonHoc_Activity extends AppCompatActivity {
    private static final String TAG = "QLMonHoc_Activity";
    Spinner spnKhoa;
    Toolbar toolbar;
    FirebaseFirestore db;
    RecyclerView rcvMH;
    QLMonHoc_Adapter adapterQLMH;
    ArrayList<Subject> listSubject;
    ArrayList<Subject> listSubjectAll;
    ArrayAdapter<Faculty> spinnerAdapter;
    ArrayList<Faculty> listKhoa;
    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_qlmon_hoc);

        Init();
        Event();
    }

    private void Event() {
        spnKhoa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onSelectedHandler(adapterView, view, i, l);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void onSelectedHandler(AdapterView<?> adapterView, View view, int position, long l) {
        if (position == 0) {
            getAllSubject();
            return;
        }
        Faculty faculty = listKhoa.get(position);
        getSubjectByFaculty(faculty.getFaculty_ID());
    }

    private void getSubjectByFaculty(String faculty_id) {
        listSubject = new ArrayList<>();
        db.collection("Subject")
                .whereEqualTo("faculty_ID", faculty_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                listSubject.add(doc.toObject(Subject.class));
                            }
                            adapterQLMH.setData(listSubject);
                            if (listSubject.size() == 0) {
                                rcvMH.setVisibility(View.GONE);
                            }else {
                                rcvMH.setVisibility(View.VISIBLE);
                            }
                        } else {
                            return;
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ql_lienhe, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_QLLH_itAdd:
                addSubject();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addSubject() {
        if (spnKhoa.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Hãy chọn một khoa trước", Toast.LENGTH_SHORT).show();
            return;
        }
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_them_monhoc);

        TextInputEditText edtMaMH, edtTenMH, edtSoTC;
        TextView tvTenKhoa;
        Button btnXong;

        tvTenKhoa = dialog.findViewById(R.id.dialogThemMH_tvTenKhoa);
        edtMaMH = dialog.findViewById(R.id.dialogThemMH_edtMaMH);
        edtTenMH = dialog.findViewById(R.id.dialogThemMH_edtTenMH);
        edtSoTC = dialog.findViewById(R.id.dialogThemMH_edtSoTC);
        btnXong = dialog.findViewById(R.id.dialogThemMH_btnXong);

        tvTenKhoa.setText("Khoa: " + listKhoa.get(spnKhoa.getSelectedItemPosition()).getFaculty_name());

        btnXong.setOnClickListener(view -> {
            if (edtMaMH.getText().toString().trim().length() < 4) {
                edtMaMH.setError("Mã môn học không hợp lệ");
                edtMaMH.requestFocus();
                return;
            }
            if (edtTenMH.getText().toString().trim().length() < 4) {
                edtTenMH.setError("Tên môn học không hợp lệ");
                edtTenMH.requestFocus();
                return;
            }

            if (checkSubjectExisted(edtMaMH.getText().toString().trim()) == true) {
                edtMaMH.setError("Môn học đã tồn tại");
                edtMaMH.requestFocus();
                return;
            }

            if (edtSoTC.getText().toString().trim().length() < 1) {
                edtSoTC.setError("Số tín chỉ không hợp lệ");
                edtSoTC.requestFocus();
                return;
            }

            int soTC = 0;

            try {
                soTC = Integer.parseInt(edtSoTC.getText().toString().trim());
            } catch (Exception e) {
                edtSoTC.setError("Số tín chỉ không hợp lệ");
                edtSoTC.requestFocus();
                return;
            }

            Subject subject = new Subject(listKhoa.get(spnKhoa.getSelectedItemPosition()).getFaculty_ID()
                    , edtMaMH.getText().toString().trim()
                    , edtTenMH.getText().toString().trim()
                    , soTC);

            db.collection("Subject")
                    .add(subject)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(QLMonHoc_Activity.this, "Thêm môn học thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                spnKhoa.setSelection(0);
                                return;
                            } else {
                                Toast.makeText(QLMonHoc_Activity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
        });

        dialog.create();
        dialog.show();
    }

    private Boolean checkSubjectExisted(String id) {
        for (Subject subject : listSubjectAll) {
            if (subject.getSubject_ID().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    private void getAllSubject() {
        listSubject = new ArrayList<>();
        listSubjectAll.clear();
        db.collection("Subject")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                listSubject.add(doc.toObject(Subject.class));
                                listSubjectAll.add(doc.toObject(Subject.class));
                            }
                            Collections.sort(listSubject);
                            adapterQLMH.setData(listSubject);
                            if (listSubject.size() == 0) {
                                rcvMH.setVisibility(View.GONE);
                            }
                        } else {
                            return;
                        }
                    }
                });
    }

    private void Init() {
        toolbar = findViewById(R.id.QLMH_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("QUẢN LÝ MÔN HỌC");
        toolbar.setNavigationOnClickListener(v -> finish());
        //
        bottomSheetDialog = new BottomSheetDialog(this);
        db = FirebaseFirestore.getInstance();
        spnKhoa = findViewById(R.id.QLMH_spinnerKhoa);
        listSubject = new ArrayList<>();
        listSubjectAll = new ArrayList<>();
        listKhoa = new ArrayList<>();
        rcvMH = findViewById(R.id.QLMH_rcv);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvMH.addItemDecoration(itemDecoration);
        adapterQLMH = new QLMonHoc_Adapter(this);
        adapterQLMH.setData(listSubject);
        rcvMH.setAdapter(adapterQLMH);
        getListKhoa();
    }

    private void getListKhoa() {
        listKhoa.clear();
        listKhoa.add(new Faculty("khoa", "  Tất cả"));
        db.collection("Faculty")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                listKhoa.add(snapshot.toObject(Faculty.class));
                            }
                            spinnerAdapter = new ArrayAdapter<>(QLMonHoc_Activity.this, android.R.layout.simple_spinner_dropdown_item, listKhoa);
                            spnKhoa.setAdapter(spinnerAdapter);
                        } else {
                            Log.d(TAG, "onComplete: GET FACULTY FAILED");
                        }
                    }
                });
    }

    public void openBottomSheetDialog(Subject subject) {
        bottomSheetDialog.setContentView(R.layout.bottom_sheet2);

        Button btnXoa = bottomSheetDialog.findViewById(R.id.bottomSheet2_btnXoa);
        Button btnHuy = bottomSheetDialog.findViewById(R.id.bottomSheet2_btnHuy);

        btnHuy.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            return;
        });

        btnXoa.setOnClickListener(view -> {
            db.collection("Subject")
                    .whereEqualTo("subject_ID", subject.getSubject_ID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                String docID = doc.getId();
                                db.collection("Subject")
                                        .document(docID)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(QLMonHoc_Activity.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                                                bottomSheetDialog.dismiss();
                                                return;
                                            }
                                        });
                            }
                        }
                    });
            //delete attendance
            db.collection("Attendance")
                    .whereEqualTo("subject_ID", subject.getSubject_ID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                String docID = doc.getId();
                                db.collection("Attendance")
                                        .document(docID)
                                        .delete();
                            }
                        }
                    });
            //delete subject of semester detail
            db.collection("SubjectsOfSemester_Detail")
                    .whereEqualTo("subject_ID", subject.getSubject_ID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                String docID = doc.getId();
                                db.collection("SubjectsOfSemester_Detail")
                                        .document(docID)
                                        .delete();
                            }
                        }
                    });
            Toast.makeText(QLMonHoc_Activity.this, "Xóa dữ liệu môn học thành công", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.create();
        bottomSheetDialog.show();

    }
}