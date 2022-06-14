package com.example.utehystudent.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import me.pushy.sdk.Pushy;

public class AccountSetting_Activity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imgAvt;
    Button btnDoiMK, btnDangXuat;
    TextView tvName, tvLop;
    FirebaseFirestore db;
    StorageReference storage;
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
        storage = FirebaseStorage.getInstance().getReference(userPref.getString("class_ID", ""));

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
        imgAvt.setOnClickListener(view -> {
            requestPermission();
        });
    }

    private void signOut() {
        Pushy.unregister(this);
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
        tvName.setText(name);

        String s = "Mã SV: "+username;
        if (regency.equals("lt")) {
            s = s+ "\nLớp "+classID+" - LỚP TRƯỞNG";
        }else {
            s = s + "\nLớp "+classID+" - THÀNH VIÊN";
        }

        tvLop.setText(s);

    }

    private void requestPermission() {
        //them file Manifest dau tien
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openBottomPicker();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(AccountSetting_Activity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void openBottomPicker() {
        String username = userPref.getString("username", "");
        TedBottomPicker.OnImageSelectedListener listener = new TedBottomPicker.OnImageSelectedListener() {
            @Override
            public void onImageSelected(Uri uri) {
                Picasso.get().load(uri).resize(280, 280).centerCrop().into(imgAvt);
                if (uri != null) {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    String fileName = "avt"+username+""+timestamp.getTime()+""+getFileExtension(uri);
                    StorageReference fileRef = storage.child(fileName);
                    UploadTask uploadTask = fileRef.putFile(uri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(AccountSetting_Activity.this,"Lưu ảnh thất bại",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String link_img = String.valueOf(uri);
                                    db.collection("User")
                                            .document(username)
                                            .update("avt_link", FieldValue.arrayUnion(link_img));
                                    Toast.makeText(AccountSetting_Activity.this, "Đổi ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        };

        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(this)
                .setOnImageSelectedListener(listener)
                .setCompleteButtonText("DONE")
                .setEmptySelectionText("NO IMAGE")
                .create();
        tedBottomPicker.show(getSupportFragmentManager());
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(cR.getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }
        return extension;
    }

}