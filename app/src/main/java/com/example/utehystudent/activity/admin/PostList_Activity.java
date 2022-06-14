package com.example.utehystudent.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.ThongBaoAdmin_Adapter;
import com.example.utehystudent.model.BaiViet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostList_Activity extends AppCompatActivity {
    ThongBaoAdmin_Adapter adapter;
    ArrayList<BaiViet> listPost;
    RecyclerView rcv;
    Toolbar toolbar;
    TextView tvDangBai;
    ProgressBar prg;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_post_list);

        Init();

        tvDangBai.setOnClickListener(view -> {
            startActivity(new Intent(this, DangBai_Activity.class));
        });
    }

    private void Init() {
        toolbar = findViewById(R.id.postList_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("DANH SÁCH THÔNG BÁO");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();

        listPost = new ArrayList<>();

        prg = findViewById(R.id.postList_prgBar);

        tvDangBai = findViewById(R.id.postList_tvDangBai);

        rcv = findViewById(R.id.postList_rcv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcv.addItemDecoration(itemDecoration);

        getListPost();

    }

    private void getListPost() {
        rcv.setVisibility(View.GONE);
        prg.setVisibility(View.VISIBLE);
        listPost.clear();
        db.collection("Post")
                .whereEqualTo("idNguoiDang", "admin")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                listPost.add(doc.toObject(BaiViet.class));
                            }
                            adapter = new ThongBaoAdmin_Adapter(PostList_Activity.this, listPost);
                            rcv.setAdapter(adapter);
                            rcv.setVisibility(View.VISIBLE);
                            prg.setVisibility(View.GONE);
                        }else {
                            return;
                        }
                    }
                });
    }
}