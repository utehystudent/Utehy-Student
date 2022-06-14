package com.example.utehystudent.activity.admin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.SinhVienQL_Adapter;
import com.example.utehystudent.model.Account;
import com.example.utehystudent.model.Class;
import com.example.utehystudent.model.Faculty;
import com.example.utehystudent.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class QLSV_Activity extends AppCompatActivity implements Serializable {
    private static final String TAG = "QLSV_Activity";
    Toolbar toolbar;
    Spinner spinnerKhoa;
    EditText edtTK;
    RecyclerView rcv;
    SinhVienQL_Adapter adapterSVQL;
    ArrayList<Faculty> listKhoa;
    ArrayList<User> listUser;
    ArrayList<User> listUser_clone;
    FirebaseFirestore db;
    ArrayAdapter<Faculty> spinnerAdapter;
    ArrayList<String> listClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_qlsv);

        Init();
    }

    private void Init() {
        toolbar = findViewById(R.id.adminQLSV_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("QUẢN LÝ SINH VIÊN");
        toolbar.setNavigationOnClickListener(v -> finish());



        edtTK = findViewById(R.id.adminQLSV_edtSearch);
        rcv = findViewById(R.id.adminQLSV_rcv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcv.addItemDecoration(itemDecoration);
        db = FirebaseFirestore.getInstance();

        listClassName = new ArrayList<>();
        listUser = new ArrayList<>();
        listUser_clone = new ArrayList<>();
        adapterSVQL = new SinhVienQL_Adapter(this);
        adapterSVQL.setData(listUser);

        spinnerKhoa = findViewById(R.id.adminQLSV_spinnerKhoa);
        listKhoa = new ArrayList<>();
        getListKhoa();
        getListAllStudent();

        Events();
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

        edtTK.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUser(edtTK.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void searchUser(String s) {
        listUser.clear();
        for (User user : listUser_clone) {
            if (user.getUsername().contains(s) || user.getName().toLowerCase().contains(s.toLowerCase()) || user.getClass_ID().toLowerCase().contains(s.toLowerCase())) {
                listUser.add(user);
            }
        }
        adapterSVQL.setData(listUser);
    }

    private void onSelectedHandler(AdapterView<?> adapterView, View view, int position, long l) {
        edtTK.setText("");
        if (position == 0) {
            getListAllStudent();
            adapterSVQL.setData(listUser);
            return;
        }
        listUser = new ArrayList<>();
        Faculty faculty = listKhoa.get(position);
        getStudentByFaculty(faculty.getFaculty_ID());
    }

    private void getStudentByFaculty(String faculty_id) {
        listUser.clear();
        listUser_clone.clear();
        db.collection("User")
                .whereEqualTo("faculty_ID", faculty_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listUser.add(document.toObject(User.class));
                                listUser_clone.add(document.toObject(User.class));
                            }
                            Collections.sort(listUser);
                            adapterSVQL.setData(listUser);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getListKhoa() {
        listKhoa.clear();
        listKhoa.add(new Faculty("khoa", "Chọn khoa"));
        db.collection("Faculty")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                listKhoa.add(snapshot.toObject(Faculty.class));
                            }
                            spinnerAdapter = new ArrayAdapter<>(QLSV_Activity.this, android.R.layout.simple_spinner_dropdown_item, listKhoa);
                            spinnerKhoa.setAdapter(spinnerAdapter);
                        } else {
                            Log.d(TAG, "onComplete: GET FAULTY FAILED");
                        }
                    }
                });
    }

    private void getListClassName() {
        listClassName.clear();
        db.collection("Class")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                listClassName.add(doc.get("class_ID").toString());
                            }
                        } else {
                            return;
                        }
                    }
                });
    }

    private void getListAllStudent() {
        listUser.clear();
        listUser_clone.clear();
        db.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            User user;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user = document.toObject(User.class);
                                if (user.getUsername().equals("admin")) {
                                    continue;
                                }
                                listUser.add(user);
                                listUser_clone.add(user);
                            }
                            Collections.sort(listUser);
                            adapterSVQL.setData(listUser);
                            rcv.setAdapter(adapterSVQL);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_qlsv, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.QLSVMenu_itThemLop:
                DialogThemLop();
                break;
            case R.id.QLSVMenu_itThemSV:
                DialogThemSV();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DialogThemLop() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_themlop_admin);

        Spinner spinner;
        TextInputEditText edtMaLop, edtTenLop, edtNamVao, edtNamRa;
        Button btnXong;

        spinner = dialog.findViewById(R.id.dialogThemLop_spinnerKhoa);

        edtMaLop = dialog.findViewById(R.id.dialogThemLop_edtMaLop);
        edtTenLop = dialog.findViewById(R.id.dialogThemLop_edtTenLop);
        edtNamVao = dialog.findViewById(R.id.dialogThemLop_edtNamVao);
        edtNamRa = dialog.findViewById(R.id.dialogThemLop_namRa);
        btnXong = dialog.findViewById(R.id.dialogThemLop_btnXong);

        spinner.setAdapter(spinnerAdapter);

        final Faculty[] faculty_chose = {new Faculty()};

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    faculty_chose[0] = new Faculty();
                }
                faculty_chose[0] = listKhoa.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnXong.setOnClickListener(view -> {
            if (faculty_chose[0].getFaculty_ID() == null || faculty_chose[0].getFaculty_ID().equals("")) {
                Toast.makeText(dialog.getContext(), "Vui lòng chọn một khoa", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkClassIsExisted(edtMaLop.getText().toString().trim()) == true) {
                edtMaLop.setError("Lớp đã tồn tại");
                edtMaLop.requestFocus();
                return;
            }
            if (checkNamVaoRa(edtNamVao.getText().toString().trim(), edtNamRa.getText().toString().trim()) == false) {
                edtNamVao.setError("");
                edtNamVao.requestFocus();
                edtNamRa.setError("");
                edtNamRa.requestFocus();
                return;
            }
            if (edtTenLop.getText().toString().equals("")) {
                edtTenLop.setError("Thông tin trống");
                edtTenLop.requestFocus();
                return;
            }
            String course = edtNamVao.getText().toString() + "-" + edtNamRa.getText().toString().trim();
            Class classNew = new Class(edtMaLop.getText().toString().trim(), faculty_chose[0].getFaculty_ID(), edtTenLop.getText().toString().trim(), course);
            db.collection("Class")
                    .document(classNew.getClass_ID())
                    .set(classNew)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialog.dismiss();
                            Toast.makeText(QLSV_Activity.this, "Thêm lớp thành công!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(QLSV_Activity.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
        });

        dialog.create();
        dialog.show();
    }

    private Boolean checkNamVaoRa(String sVao, String sRa) {
        int namVao, namRa;
        try {
            namVao = Integer.parseInt(sVao);
            namRa = Integer.parseInt(sRa);
        } catch (Exception e) {
            return false;
        }
        if (namVao >= namRa) {
            return false;
        }
        return true;
    }

    private boolean checkClassIsExisted(String id) {
        for (String s : listClassName) {
            if (id.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    private void DialogThemSV() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_themsv_admin);

        Spinner spinnerK, spinnerL;
        TextInputEditText edtMaSV, edtTen;
        Button btnXong;
        final SpinnerAdapter[] adapterLop = new SpinnerAdapter[1];
        ArrayList<Class> listLop = new ArrayList<>();

        spinnerK = dialog.findViewById(R.id.dialogThemSV_spinnerKhoa);
        spinnerL = dialog.findViewById(R.id.dialogThemSV_spinnerLop);
        edtMaSV = dialog.findViewById(R.id.dialogThemSV_edtMaSV);
        edtTen = dialog.findViewById(R.id.dialogThemSV_edtHoTen);

        btnXong = dialog.findViewById(R.id.dialogThemSV_btnXong);

        spinnerK.setAdapter(spinnerAdapter);

        adapterLop[0] = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_spinner_dropdown_item, listLop);
        spinnerL.setAdapter(adapterLop[0]);

        final Faculty[] faculty_chose = {new Faculty()};

        spinnerK.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    faculty_chose[0] = new Faculty();
                    listLop.clear();
                    adapterLop[0] = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_spinner_dropdown_item, listLop);
                } else {
                    faculty_chose[0] = listKhoa.get(i);
                    getClassByFaculty(listKhoa.get(i).getFaculty_ID());
                }
            }

            private void getClassByFaculty(String faculty_id) {
                listLop.clear();
                db.collection("Class")
                        .whereEqualTo("faculty_ID", faculty_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot doc : task.getResult()) {
                                        listLop.add(doc.toObject(Class.class));
                                    }
                                    adapterLop[0] = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_spinner_dropdown_item, listLop);
                                    spinnerL.setAdapter(adapterLop[0]);
                                } else {
                                    return;
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Class[] class_chose = {new Class()};

        spinnerL.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                class_chose[0] = listLop.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnXong.setOnClickListener(view -> {
            if (checkStudentIsExisted(edtMaSV.getText().toString().trim()) == true || edtMaSV.getText().toString().trim().length() < 8) {
                edtMaSV.setError("Thông tin không hợp lệ");
                edtMaSV.requestFocus();
                return;
            }
            if (spinnerK.getSelectedItemPosition() == 0 || listLop.size() == 0) {
                Toast.makeText(dialog.getContext(), "Chưa có lớp hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (edtTen.getText().toString().trim().length() < 3) {
                edtTen.setError("Thông tin không hợp lệ");
                edtTen.requestFocus();
                return;
            }
            Account account = new Account(edtMaSV.getText().toString().trim()
                    , edtMaSV.getText().toString().trim()
                    , "user"
                    , "");
            User user = new User(account.getUsername(), faculty_chose[0].getFaculty_ID(), class_chose[0].getClass_ID(), edtTen.getText().toString().trim(), "tv", "");
            createStudent(dialog, account, user);
        });

        dialog.create();
        dialog.show();
    }


    private void createStudent(Dialog d ,Account account, User user) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tạo tài khoản...");
        dialog.show();
        //add account
        db.collection("Account")
                .document(account.getUsername())
                .set(account);

        //add user
        db.collection("User")
                .document(user.getUsername())
                .set(user);

        dialog.dismiss();
        d.dismiss();
        Toast.makeText(this, "Thêm sinh viên thành công!", Toast.LENGTH_SHORT).show();
    }

    private Boolean checkStudentIsExisted(String id) {
        for (User user : listUser_clone) {
            if (user.getUsername().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void changeToDetail(User user) {
        Intent intent = new Intent(this, DetailStudent_Activity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}