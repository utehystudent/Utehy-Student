package com.example.utehystudent.activity.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.utehystudent.R;
import com.example.utehystudent.model.Faculty;
import com.example.utehystudent.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailStudent_Activity extends AppCompatActivity implements Serializable {
    CircleImageView img;
    Toolbar toolbar;
    TextView tvName, tvMaSV, tvMaLop, tvKhoa, tvChucVu;
    Button btnCapQuyen, btnResetMK, btnXoa;
    FirebaseFirestore db;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail_student);

        Init();
        Events();
    }

    private void Events() {
        btnCapQuyen.setOnClickListener(view -> {
            String rule = "";
            if (user.getRegency().equals("lt")) {
                rule = "tv";
            }else {
                rule = "lt";
            }
            changeRule(rule);
        });

        btnResetMK.setOnClickListener(view -> {
            db.collection("Account")
                    .document(user.getUsername())
                    .update("password", user.getUsername())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DetailStudent_Activity.this, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailStudent_Activity.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
        });
    }

    private void changeRule(String rule) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Bạn có chắc chắn muốn thay đổi quyền sinh viên này không ?");
        alert.setPositiveButton("CÓ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.collection("User")
                        .document(user.getUsername())
                        .update("regency", rule)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (rule.equals("lt")) {
                                    tvChucVu.setText("Chức vụ: CÁN BỘ LỚP");
                                    btnCapQuyen.setText("Thu quyền");
                                }else {
                                    tvChucVu.setText("Chức vụ: THÀNH VIÊN LỚP");
                                    btnCapQuyen.setText("Cấp quyền");
                                }
                                user.setRegency(rule);
                                Toast.makeText(DetailStudent_Activity.this, "Thay đổi quyền thành công!", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        });
            }
        });
        alert.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.create();
        alert.show();
    }


    private void Init() {
        toolbar = findViewById(R.id.detailStudent_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("THÔNG TIN SINH VIÊN");
        toolbar.setNavigationOnClickListener(v -> finish());


        img = findViewById(R.id.detailStudent_img);
        tvName = findViewById(R.id.detailStudent_tvTen);
        tvMaSV = findViewById(R.id.detailStudent_tvMaSV);
        tvMaLop = findViewById(R.id.detailStudent_tvMaLop);
        tvKhoa = findViewById(R.id.detailStudent_tvKhoa);
        tvChucVu = findViewById(R.id.detailStudent_tvChucVu);
        btnCapQuyen = findViewById(R.id.detailStudent_btnCapQuyen);
        btnResetMK = findViewById(R.id.detailStudent_btnRSMK);
        btnXoa = findViewById(R.id.detailStudent_btnXoa);

        db = FirebaseFirestore.getInstance();

        setData();

    }

    private void setData() {
        Intent it = getIntent();
        user = (User) it.getSerializableExtra("user");

        try{
            Picasso.get().load(user.getAvt_link()).noFade().into(img);
        }catch (Exception e){
            Picasso.get().load(R.drawable.ic_student).noFade().into(img);
        }

        tvName.setText("Họ tên: "+user.getName());
        tvMaSV.setText("Mã SV: "+user.getUsername());
        getKhoa(user.getFaculty_ID());
        if (user.getRegency().equals("tv")) {
            tvChucVu.setText("Chức vụ: THÀNH VIÊN LỚP");
        }else {
            tvChucVu.setText("Chức vụ: CÁN BỘ LỚP");
            btnCapQuyen.setText("Thu quyền");
        }
    }

    private void getKhoa(String faculty_id) {
        db.collection("Faculty")
                .whereEqualTo("class_ID", faculty_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                tvKhoa.setText("Khoa: "+doc.toObject(Faculty.class).getFaculty_name());
                            }
                        }else {
                            tvKhoa.setText("Khoa: N/A");
                        }
                    }
                });
    }
}