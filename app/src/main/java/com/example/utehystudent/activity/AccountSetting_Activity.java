package com.example.utehystudent.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.utehystudent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class AccountSetting_Activity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imgAvt;
    Button btnDoiMK, btnDangXuat;
    TextView tvName, tvLop;
    FirebaseFirestore db;
    SharedPreferences userPref, accountPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_account_setting);

        toolbar = findViewById(R.id.AccountSetting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("TÀI KHOẢN");
        toolbar.setNavigationOnClickListener(v -> finish());

        imgAvt = findViewById(R.id.AccountSetting_imgAvt);

        btnDoiMK = findViewById(R.id.AccountSetting_btnDoiMK);
        btnDangXuat = findViewById(R.id.AccountSetting_btnDangXuat);

        tvName = findViewById(R.id.AccountSetting_tvName);
        tvLop = findViewById(R.id.AccountSetting_tvLop);

        userPref = getSharedPreferences("User", Context.MODE_PRIVATE);
        accountPref = getSharedPreferences("Account", Context.MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();

        getDataUser();
        Events();

    }

    private void Events() {
        btnDoiMK.setOnClickListener(view -> {
            openPasswordChangeDialog();
        });
        btnDangXuat.setOnClickListener(view -> {
            signOut();
        });
    }

    private void signOut() {
        //Delete user data from SharedPreferences
        accountPref.edit().clear().commit();
        userPref.edit().clear().commit();
        MainActivity.DeleteDataAttendance();

        //delete device_token
        String username = accountPref.getString("username", "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Account")
                .document(username)
                .update("device_token", "");

        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);
        finish();
    }

    private void openPasswordChangeDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_doi_mk);
        EditText edtMKCu, edtMKMoi, edtXacNhan;
        Button btnLuu;

        edtMKCu = dialog.findViewById(R.id.dialogPassChange_edtMKCu);
        edtMKMoi = dialog.findViewById(R.id.dialogPassChange_edtMKMoi);
        edtXacNhan = dialog.findViewById(R.id.dialogPassChange_edtMKXacNhan);

        btnLuu = dialog.findViewById(R.id.dialogPassChange_btnLuu);

        String currentPassword = accountPref.getString("password", "");
        String currentID = accountPref.getString("username", "");

        btnLuu.setOnClickListener(view -> {
            if (edtMKCu.getText().toString().equals("")) {
                edtMKCu.setError("Thông tin trống");
                edtMKCu.requestFocus();
                return;
            }
            if (edtMKMoi.getText().toString().equals("")) {
                edtMKMoi.setError("Thông tin trống");
                edtMKMoi.requestFocus();
                return;
            }
            if (edtXacNhan.getText().toString().equals("")) {
                edtXacNhan.setError("Thông tin trống");
                edtXacNhan.requestFocus();
                return;
            }
            if (!edtMKCu.getText().toString().equals(currentPassword)) {
                edtMKCu.setError("Mật khẩu hiện tại chưa đúng");
                edtMKCu.requestFocus();
                edtMKCu.setText("");
                return;
            }
            if (!edtMKMoi.getText().toString().equals(edtXacNhan.getText().toString()) || edtMKMoi.getText().toString().length() < 8) {
                edtMKCu.setError("Mật khẩu không hợp lệ");
                edtMKCu.requestFocus();
                edtMKCu.setText("");
                edtXacNhan.setText("");
                return;
            }
            db.collection("Account")
                    .document(currentID)
                    .update("password", edtMKMoi.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                changePasswordLocal(edtMKMoi.getText().toString().trim());
                                dialog.dismiss();
                            }else {
                                Toast.makeText(AccountSetting_Activity.this, "Đã có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                return;
                            }
                        }
                    });
        });

        dialog.create();
        dialog.show();
    }

    private void changePasswordLocal(String pass) {
        SharedPreferences.Editor editor = accountPref.edit();
        editor.putString("password", pass);
        editor.apply();
        Toast.makeText(AccountSetting_Activity.this, "Thay đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
    }

    private void getDataUser() {
        String avt_link = userPref.getString("avt_link", "");
        String username = userPref.getString("username", "");
        String regency = userPref.getString("regency", "");
        String classID = userPref.getString("class_ID", "");
        try {
            Picasso.get().load(avt_link).resize(280, 280).centerCrop().into(imgAvt);
        } catch (Exception e) {
            imgAvt.setImageResource(R.drawable.ic_student);
        }

        String name = userPref.getString("name", "");
        tvName.setText(name+" ("+username+")");

        String s = "";
        if (regency.equals("lt")) {
            s = "Lớp "+classID+" - LỚP TRƯỞNG";
        }else {
            s = "Lớp "+classID+" - THÀNH VIÊN";
        }

        tvLop.setText(s);

    }
}