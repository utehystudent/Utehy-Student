package com.example.utehystudent.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.ClassManagementViewModel;
import com.example.utehystudent.adapters.StudentAdapter;
import com.example.utehystudent.model.User;

import java.util.ArrayList;

public class ClassManagementActivity extends AppCompatActivity {
    Dialog dialog;
    Toolbar toolbar;
    RecyclerView rcv;
    StudentAdapter studentAdapter;
    ArrayList<User> UsersList;
    ArrayList<User> UsersList_Clone;
    ClassManagementViewModel classManagementViewModel;
    EditText edtTimKiem;
    ImageView btnAddStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_management);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        InitView();
        Events();
    }

    private void InitView() {
        toolbar = findViewById(R.id.ClassManagement_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("QUẢN LÝ LỚP HỌC");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edtTimKiem = findViewById(R.id.ClassManagement_edtSearch);
        rcv = findViewById(R.id.ClassManagement_rcv);

        btnAddStudent = findViewById(R.id.ClassManagement_btnAdd);

        classManagementViewModel = new ViewModelProvider(ClassManagementActivity.this).get(ClassManagementViewModel.class);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ClassManagementActivity.this);
        rcv.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(ClassManagementActivity.this, DividerItemDecoration.VERTICAL);
        rcv.addItemDecoration(itemDecoration);
        ShowLoadingDialog();
        classManagementViewModel.getListUserLiveData().observe(ClassManagementActivity.this, new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                UsersList = users;
                UsersList_Clone = UsersList;
                Log.d("qqq", "onChanged: "+UsersList_Clone.size());
                studentAdapter = new StudentAdapter(UsersList);
                rcv.setAdapter(studentAdapter);
                dialog.dismiss();
            }
        });
    }

    private void Events() {

        btnAddStudent.setOnClickListener(view -> {
            Intent it = new Intent(this, AddStudent_Activity.class);
            startActivity(it);
        });

        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<User> list = new ArrayList<>();
                String text = edtTimKiem.getText().toString().toLowerCase().trim();

                for (User user : UsersList_Clone){
                    if (user.getName().toLowerCase().contains(text) || user.getUsername().contains(text)) {
                        list.add(user);
                    }
                }
                UsersList = list;
                studentAdapter = new StudentAdapter(UsersList);
                rcv.setAdapter(studentAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void ShowLoadingDialog() {
        dialog = new Dialog(ClassManagementActivity.this);
        dialog.setContentView(R.layout.loading_layout1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}