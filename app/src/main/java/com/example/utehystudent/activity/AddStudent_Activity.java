package com.example.utehystudent.activity;

import android.Manifest;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.utehystudent.R;
import com.example.utehystudent.model.Account;
import com.example.utehystudent.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;

public class AddStudent_Activity extends AppCompatActivity {
    private static final String TAG = "AddStudent_Activity";
    Toolbar toolbar;
    TextInputEditText edtMaSV, edtTenSV;
    Button btnXong;
    FirebaseFirestore db;
    StorageReference storage;
    SharedPreferences pref;
    CircleImageView circleImageView;
    ArrayList<Uri> uriAvt = new ArrayList<>();
    ArrayList<String> listAccount = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_student);

        Init();
        Events();
    }

    private void Events() {
        circleImageView.setOnClickListener(view -> {
            requestPermission();
        });

        btnXong.setOnClickListener(view -> {
            String id = edtMaSV.getText().toString().trim();
            String name = edtMaSV.getText().toString().trim();

            if (id.equals("")) {
                edtMaSV.setError("Thông tin trống");
                edtMaSV.requestFocus();
                return;
            }
            if (name.equals("")) {
                edtTenSV.setError("Thông tin trống");
                edtTenSV.requestFocus();
                return;
            }
            if (id.length() < 8) {
                edtMaSV.setError("Thông tin không hợp lệ");
                edtMaSV.requestFocus();
                return;
            }
            if (checkExistedStudent(id) == true) {
                edtMaSV.setError("Sinh viên đã tồn tại");
                edtMaSV.setText("");
                edtMaSV.requestFocus();
                return;
            } else {
                Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
            }
            createStudent();
        });
    }

    private Boolean checkExistedStudent(String id) {
        for (String s : listAccount) {
            if (s.equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    private void createStudent() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tạo tài khoản...");
        dialog.show();
        //add account
        Account account = new Account(edtMaSV.getText().toString().trim()
                , edtMaSV.getText().toString().trim()
                , "user"
                , "");
        db.collection("Account")
                .document(edtMaSV.getText().toString().trim())
                .set(account);

        //add user
        String facultyID = pref.getString("faculty_ID", "");
        String classID = pref.getString("class_ID", "");
        String avtLink = "";

        String username = edtMaSV.getText().toString().trim();
        String name = edtTenSV.getText().toString().trim();
        String regency = "tv";

        User user = new User(username, facultyID, classID, name, regency, avtLink);

        if (uriAvt.get(0) != null) {
            uploadImage(user);
        }

        dialog.dismiss();
        Toast.makeText(this, "Tạo tài khoản sinh viên thành công!", Toast.LENGTH_SHORT).show();
    }

    private void uploadImage(User user) {
        Uri uri = uriAvt.get(0);
        if (uri != null) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String fileName =  "avt"+ timestamp.getTime() +""+ getFileExtension(uri);
            StorageReference fileRef = storage.child(fileName);
            UploadTask uploadTask = fileRef.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(AddStudent_Activity.this,"Lưu ảnh thất bại",Toast.LENGTH_SHORT).show();
                    return;
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String link = String.valueOf(uri);
                            user.setAvt_link(link);
                            db.collection("User")
                                    .document(user.getUsername())
                                    .set(user);
                        }
                    });
                }
            });
        }
    }

    private void getListAccount() {
        db.collection("Account")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            listAccount.add(doc.toObject(Account.class).getUsername());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Get data account failed");
                    }
                });
    }

    private void Init() {
        toolbar = findViewById(R.id.AddStudent_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("THÊM SINH VIÊN");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edtMaSV = findViewById(R.id.AddStudent_edtMaSV);
        edtTenSV = findViewById(R.id.AddStudent_edtName);

        circleImageView = findViewById(R.id.AddStudent_imgAvt);

        btnXong = findViewById(R.id.AddStudent_btnXong);

        pref = getSharedPreferences("User", Context.MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference(pref.getString("class_ID", ""));

        getListAccount();
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
                Toast.makeText(AddStudent_Activity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void openBottomPicker() {
        TedBottomPicker.OnImageSelectedListener listener = new TedBottomPicker.OnImageSelectedListener() {
            @Override
            public void onImageSelected(Uri uri) {
                if (uri != null) {
                    uriAvt.add(0, uri);
                    Picasso.get().load(uri).noFade().into(circleImageView);
                } else {
                    uriAvt.set(0, null);
                    circleImageView.setImageResource(R.drawable.ic_student);
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