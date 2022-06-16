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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.DepartmentAdmin_Adapter;
import com.example.utehystudent.model.Department;
import com.example.utehystudent.model.Faculty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QLPhongBan_Activity extends AppCompatActivity {
    Toolbar toolbar;
    Spinner spinnerKhoa;
    ArrayList<Faculty> listKhoa;
    ArrayAdapter<Faculty> spinnerAdapter;
    RecyclerView rcvPB;
    FirebaseFirestore db;
    ArrayList<Department> listDepartment;
    DepartmentAdmin_Adapter adapterPB;
    LinearLayout linearNothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_qlphong_ban);

        Init();
        Event();
    }

    private void Event() {
        spinnerKhoa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        Faculty faculty = listKhoa.get(position);
        getDepartmentByFaculty(faculty.getFaculty_ID());
    }

    private void getDepartmentByFaculty(String faculty_id) {
        listDepartment.clear();
        db.collection("Department")
                .whereEqualTo("faculty_ID", faculty_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listDepartment.add(document.toObject(Department.class));
                            }
                            adapterPB.setData(listDepartment);
                            rcvPB.setAdapter(adapterPB);
                            if (listDepartment.size() == 0) {
                                rcvPB.setVisibility(View.GONE);
                            }else {
                                rcvPB.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d("getpb", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void Init() {
        toolbar = findViewById(R.id.QLPB_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("QUẢN LÝ PHÒNG BAN");
        toolbar.setNavigationOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        linearNothing = findViewById(R.id.QLPB_linearNothing);

        listDepartment = new ArrayList<>();
        adapterPB = new DepartmentAdmin_Adapter(this);
        adapterPB.setData(listDepartment);
        rcvPB = findViewById(R.id.QLPB_rcv);
        rcvPB.setAdapter(adapterPB);

        listKhoa = new ArrayList<>();
        spinnerKhoa = findViewById(R.id.QLPB_spinnerKhoa);
        getListKhoa();
    }

    private void getListKhoa() {
        listKhoa.clear();
        db.collection("Faculty")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                listKhoa.add(snapshot.toObject(Faculty.class));
                            }
                            spinnerAdapter = new ArrayAdapter<>(QLPhongBan_Activity.this, android.R.layout.simple_spinner_dropdown_item, listKhoa);
                            spinnerKhoa.setAdapter(spinnerAdapter);
                        } else {
                            Log.d("get pb", "onComplete: GET FACULTY FAILED");
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
                addDepartment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDepartment() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_department);

        TextView tvTenKhoa = dialog.findViewById(R.id.DialogAddPB_tvTenKhoa);
        EditText edtTenPB = dialog.findViewById(R.id.DialogAddPB_edtTenPB);
        EditText edtMaPB = dialog.findViewById(R.id.DialogAddPB_edtMaPB);
        Button btnXong = dialog.findViewById(R.id.DialogAddPB_btnThem);

        Faculty faculty = listKhoa.get(spinnerKhoa.getSelectedItemPosition());

        tvTenKhoa.setText("Khoa: "+faculty.getFaculty_name().toUpperCase());

        btnXong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtTenPB.getText().toString().trim().equals("") || edtMaPB.getText().toString().trim().equals("")) {
                    Toast.makeText(QLPhongBan_Activity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("Department")
                        .whereEqualTo("department_id", edtMaPB.getText().toString().trim())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.size() > 0) {
                                    edtMaPB.setError("Mã phòng ban đã tồn tại");
                                    edtMaPB.requestFocus();
                                    return;
                                }else {
                                    addDepartmentToDB();
                                    rcvPB.setVisibility(View.VISIBLE);
                                }
                            }


                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QLPhongBan_Activity.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

            }

            private void addDepartmentToDB() {
                Department department = new Department(faculty.getFaculty_ID(), edtMaPB.getText().toString().trim(), edtTenPB.getText().toString().trim());
                db.collection("Department")
                        .add(department)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(QLPhongBan_Activity.this, "Tạo phòng ban thành công", Toast.LENGTH_SHORT).show();
                                    listDepartment.add(department);
                                    adapterPB.notifyDataSetChanged();
                                    dialog.dismiss();
                                }else {
                                    Toast.makeText(QLPhongBan_Activity.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });
            }
        });

        dialog.create();
        dialog.show();
    }
}