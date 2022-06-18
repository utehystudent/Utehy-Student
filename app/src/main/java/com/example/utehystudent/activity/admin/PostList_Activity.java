package com.example.utehystudent.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class PostList_Activity extends AppCompatActivity {
    ThongBaoAdmin_Adapter adapter;
    ArrayList<BaiViet> listPost;
    RecyclerView rcv;
    Toolbar toolbar;
    TextView tvDangBai;
    ProgressBar prg;
    FirebaseFirestore db;
    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_post_list);

        Init();

        tvDangBai.setOnClickListener(view -> {
            startActivity(new Intent(this, DangBai_Activity.class));
            finish();
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

        bottomSheetDialog = new BottomSheetDialog(this);

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
                            Collections.sort(listPost);
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

    public void openBottomSheetDialog(BaiViet post) {
        bottomSheetDialog.setContentView(R.layout.bottom_sheet2);

        Button btnXoa = bottomSheetDialog.findViewById(R.id.bottomSheet2_btnXoa);
        Button btnHuy = bottomSheetDialog.findViewById(R.id.bottomSheet2_btnHuy);

        btnHuy.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            return;
        });

        btnXoa.setOnClickListener(view -> {
            db.collection("Post")
                    .document(post.getIdBaiViet())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(PostList_Activity.this, "Gỡ thông báo thành công", Toast.LENGTH_SHORT).show();
                            for (BaiViet bv : listPost) {
                                if (bv.getIdBaiViet().equals(post.getIdBaiViet())) {
                                    listPost.remove(bv);
                                    break;
                                }
                            }
                            bottomSheetDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostList_Activity.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();
                            return;
                        }
                    });
        });

        bottomSheetDialog.create();
        bottomSheetDialog.show();

    }
}