package com.example.utehystudent.activity.admin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.LoginActivity;
import com.example.utehystudent.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import me.pushy.sdk.Pushy;

public class BaoMat_Activity extends AppCompatActivity {
    Toolbar toolbar;
    Button btnDoiMK, btnDX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bao_mat);

        Init();
        Events();
    }

    private void Events() {
        btnDoiMK.setOnClickListener(view -> {
            dialogDoiMK();
        });
        btnDX.setOnClickListener(view -> {
            singOut();
        });
    }

    private void singOut() {
        Pushy.unregister(this);
        SharedPreferences accountPref = this.getSharedPreferences("Account", Context.MODE_PRIVATE);
        SharedPreferences userPref = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        Pushy.unregister(this);
        //Delete user data from SharedPreferences
        accountPref.edit().clear().commit();
        userPref.edit().clear().commit();
        MainActivity.DeleteDataAttendance();

        //delete device_token
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Account")
                .document("admin")
                .update("device_token", "");

        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);
        finish();
    }

    private void dialogDoiMK() {
        SharedPreferences accountPref = this.getSharedPreferences("Account", Context.MODE_PRIVATE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                                changePasswordLocal(accountPref, edtMKMoi.getText().toString().trim());
                                dialog.dismiss();
                            }else {
                                Toast.makeText(BaoMat_Activity.this, "Đã có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                return;
                            }
                        }
                    });
        });

        dialog.create();
        dialog.show();
    }

    private void changePasswordLocal(SharedPreferences accountPref, String pass) {
        SharedPreferences.Editor editor = accountPref.edit();
        editor.putString("password", pass);
        editor.apply();
        Toast.makeText(this, "Thay đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
    }

    private void Init() {
        toolbar = findViewById(R.id.baoMat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CÀI ĐẶT");
        toolbar.setNavigationOnClickListener(v -> finish());

        btnDoiMK = findViewById(R.id.baoMat_btnDoiMK);
        btnDX = findViewById(R.id.baoMat_btnDangXuat);
    }
}