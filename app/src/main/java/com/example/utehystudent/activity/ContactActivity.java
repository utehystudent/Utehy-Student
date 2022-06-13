package com.example.utehystudent.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.ContactAdapter;
import com.example.utehystudent.adapters.PhongBanAdapter;
import com.example.utehystudent.model.Contact;
import com.example.utehystudent.model.Department;
import com.example.utehystudent.model.Faculty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {
    private static final String TAG = "ContactActivity";
    Toolbar toolbar;
    FirebaseFirestore db;
    SharedPreferences pref;
    RecyclerView rcvDepartment, rcvContact;
    ArrayList<Department> listDepartment;
    PhongBanAdapter phongBanAdapter;
    Spinner spinnerKhoa;
    ArrayList<Faculty> listKhoa;
    ArrayList<Contact> listContact;
    ContactAdapter contactAdapter;
    ArrayAdapter<Faculty> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_contact);

        Init();
        Events();
    }

    private void Init() {
        toolbar = findViewById(R.id.Contact_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("DANH SÁCH LIÊN HỆ");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        pref = getSharedPreferences("User", Context.MODE_PRIVATE);

        rcvContact = findViewById(R.id.Contact_rcvGV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvContact.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvContact.addItemDecoration(itemDecoration);

        listContact = new ArrayList<>();
        rcvDepartment = findViewById(R.id.Contact_rcvPhongBan);
        listDepartment = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        rcvDepartment.setLayoutManager(gridLayoutManager);
        rcvDepartment.setFocusable(false);
        contactAdapter = new ContactAdapter(this);
        rcvContact.setAdapter(contactAdapter);
        phongBanAdapter = new PhongBanAdapter(this);
        rcvDepartment.setAdapter(phongBanAdapter);

        listKhoa = new ArrayList<>();
        spinnerKhoa = findViewById(R.id.Contact_spinnerKhoa);
        getListKhoa();
    }

    private void Events() {
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

    private void onSelectedHandler(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            listDepartment = new ArrayList<>();
            listContact = new ArrayList<>();
            contactAdapter.setData(listContact);
            phongBanAdapter.setData(listDepartment);
            return;
        }
        listContact = new ArrayList<>();
        contactAdapter.setData(listContact);
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
                            phongBanAdapter = new PhongBanAdapter(ContactActivity.this);
                            phongBanAdapter.setData(listDepartment);
                            rcvDepartment.setAdapter(phongBanAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getListKhoa() {
        listKhoa.clear();
        listKhoa.add(new Faculty("khoa", "  ---/---"));
        db.collection("Faculty")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                listKhoa.add(snapshot.toObject(Faculty.class));
                            }
                            spinnerAdapter = new ArrayAdapter<>(ContactActivity.this, android.R.layout.simple_spinner_dropdown_item, listKhoa);
                            spinnerKhoa.setAdapter(spinnerAdapter);
                        } else {
                            Log.d(TAG, "onComplete: GET FAULTY FAILED");
                        }
                    }
                });
    }

    public void clickDepartment(Department department) {
        listContact = new ArrayList<>();
        db.collection("Contact")
                .whereEqualTo("department", department.getDepartment_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                listContact.add(snapshot.toObject(Contact.class));
                            }
                            contactAdapter.setData(listContact);
                        } else {
                            Log.d(TAG, "onComplete: GET FAULTY FAILED");
                        }
                    }
                });
    }
}