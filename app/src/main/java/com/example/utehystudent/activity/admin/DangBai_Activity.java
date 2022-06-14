package com.example.utehystudent.activity.admin;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.PhotoAdapter;
import com.example.utehystudent.model.BaiViet;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;

public class DangBai_Activity extends AppCompatActivity {

    ImageButton imbCancel, imbSelectPhoto;
    EditText edtND;
    RecyclerView rcvImage;
    Button btnDang;
    ArrayList<Uri> listImageUri;
    PhotoAdapter photoAdapter;

    StorageReference storageReference;
    FirebaseFirestore db;
    SharedPreferences prefUser;
    String name = "";
    String classID = "";
    String username = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dang_bai);

        Init();
        Event();
    }

    private void Event() {
        imbCancel.setOnClickListener(view -> {
            if (listImageUri.size() == 0 && edtND.getText().toString().equals("")) {
                finish();
            } else {
                openCancelPostDialog();
            }
        });

        imbSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });

        btnDang.setOnClickListener(view -> {
            if (edtND.getText().toString().equals("") && listImageUri.size() == 0) {
                Toast.makeText(this, "Nhập nội dung cho bài viết", Toast.LENGTH_SHORT).show();
                return;
            }
            sendPost();
        });
    }

    private void sendPost() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng bài viết...");
        progressDialog.show();

        String username = prefUser.getString("username", "");
        String avtLink = prefUser.getString("avt_link", "");
        String name = prefUser.getString("name", "");
        String classID = prefUser.getString("class_ID", "");
        ArrayList<String> listLike = new ArrayList<>();
        ArrayList<String> imgList = new ArrayList<>();
        String content = edtND.getText().toString() + "";
        int numCmt = 0;

        BaiViet baiViet = new BaiViet(username, name, avtLink, classID, content, listLike, numCmt, imgList);

        String docID = baiViet.getIdBaiViet();
        db.collection("Post")
                .document(docID)
                .set(baiViet)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (listImageUri.size() > 0) {
                                uploadImages(baiViet);
                                progressDialog.dismiss();
                                Toast.makeText(DangBai_Activity.this, "Đăng bài thành công !", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(DangBai_Activity.this, "Đăng bài thành công !", Toast.LENGTH_SHORT).show();
                                listImageUri.clear();
                                photoAdapter.setData(listImageUri);
                                edtND.setText("");
                            }
                        } else {
                            Toast.makeText(DangBai_Activity.this, "Đã có lỗi xảy ra trong quá trình đăng bài", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadImages(BaiViet baiViet) {
        for (int i = 0; i < listImageUri.size(); i++) {
            Uri uri = listImageUri.get(i);
            if (uri != null) {
                String fileName = baiViet.getMaLop() + "" + baiViet.getIdNguoiDang() + "" + baiViet.getTimeStamp() + "" + i + "." + getFileExtension(uri);
                StorageReference fileRef = storageReference.child(fileName);
                UploadTask uploadTask = fileRef.putFile(uri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(DangBai_Activity.this,"Lưu ảnh thất bại",Toast.LENGTH_SHORT).show();
                        Log.d("post","thatbai");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("post","thanhcong");
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String link_img = String.valueOf(uri);
                                db.collection("Post")
                                        .document(baiViet.getIdBaiViet())
                                        .update("linkAnh", FieldValue.arrayUnion(link_img));
                            }
                        });
                    }
                });
            }
        }
        listImageUri.clear();
        photoAdapter.setData(listImageUri);
        edtND.setText("");
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

    private void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openBottomPicker();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(DangBai_Activity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void openBottomPicker() {
        TedBottomPicker.OnMultiImageSelectedListener listener = new TedBottomPicker.OnMultiImageSelectedListener() {
            @Override
            public void onImagesSelected(ArrayList<Uri> uriList) {
                listImageUri.clear();
                listImageUri.addAll(uriList);
                photoAdapter.setData(listImageUri);
            }
        };

        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(this)
                .setOnMultiImageSelectedListener(listener)
                .setCompleteButtonText("DONE")
                .setEmptySelectionText("NO IMAGE")
                .create();
        tedBottomPicker.show(getSupportFragmentManager());
    }

    private void openCancelPostDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_cancel_post);
        Button btnBo = dialog.findViewById(R.id.dialogCancelPost_btnBo);
        Button btnHuy = dialog.findViewById(R.id.dialogCancelPost_btnHuy);

        btnHuy.setOnClickListener(view -> {
            dialog.dismiss();
        });

        btnBo.setOnClickListener(view -> {
            dialog.dismiss();
            this.finish();
        });
        dialog.create();
        dialog.show();
    }

    private void Init() {
        imbSelectPhoto = findViewById(R.id.postCreate_imbAddImage_admin);
        imbCancel = findViewById(R.id.postCreate_imbCancel_admin);

        edtND = findViewById(R.id.postCreate_edtND_admin);
        rcvImage = findViewById(R.id.postCreate_rcvImage_admin);

        btnDang = findViewById(R.id.postCreate_btnDang_admin);

        listImageUri = new ArrayList<>();

        prefUser = getSharedPreferences("User", Context.MODE_PRIVATE);
        name = prefUser.getString("name", "");
        classID = "";
        username = prefUser.getString("username", "");

        storageReference = FirebaseStorage.getInstance().getReference("admin");
        db = FirebaseFirestore.getInstance();


        photoAdapter = new PhotoAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        rcvImage.setLayoutManager(gridLayoutManager);
        rcvImage.setFocusable(false);
        rcvImage.setAdapter(photoAdapter);
    }
}