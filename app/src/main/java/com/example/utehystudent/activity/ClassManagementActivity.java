package com.example.utehystudent.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
    ClassManagementViewModel classManagementViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_management);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        InitView();
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
        rcv = findViewById(R.id.ClassManagement_rcv);
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
                studentAdapter = new StudentAdapter(UsersList);
                rcv.setAdapter(studentAdapter);
                dialog.dismiss();
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
}