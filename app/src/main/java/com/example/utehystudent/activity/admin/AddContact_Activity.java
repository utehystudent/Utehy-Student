package com.example.utehystudent.activity.admin;

import android.Manifest;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.utehystudent.R;
import com.example.utehystudent.model.Contact;
import com.example.utehystudent.model.Department;
import com.example.utehystudent.model.Faculty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

public class AddContact_Activity extends AppCompatActivity {

    private static final String TAG = "AddContact_Activity";
    Toolbar toolbar;

    Spinner spnKhoa, spnBoMon;
    SpinnerAdapter adapterKhoa, adapterBoMon;
    ArrayList<Faculty> listKhoa;
    ArrayList<Department> listBoMon;

    FirebaseFirestore db;

    RadioGroup rdGroup;

    CircleImageView imgAvt;
    TextInputEditText edtHoTen, edtChucVu, edtEmail, edtSdt, edtAvtLink;
    TextInputLayout textLayout_link;

    Boolean isChooseImage = true;

    Uri uriAvt;

    String departmentChose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_contact);

        Init();
        Events();
    }

    private void Init() {
        toolbar = findViewById(R.id.AddContact_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("THÊM LIÊN HỆ MỚI");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();

        departmentChose = "";

        //init views
        spnKhoa = findViewById(R.id.AddContact_spnKhoa);
        spnBoMon = findViewById(R.id.AddContact_spnPhongBan);

        rdGroup = findViewById(R.id.AddContact_radioGroup);

        imgAvt = findViewById(R.id.AddContact_imgAvt);

        edtHoTen = findViewById(R.id.AddContact_edtHoTen);
        edtChucVu = findViewById(R.id.AddContact_edtChucVu);
        edtEmail = findViewById(R.id.AddContact_edtEmail);
        edtSdt = findViewById(R.id.AddContact_edtSDT);
        edtAvtLink = findViewById(R.id.AddContact_edtLinkAnh);
        textLayout_link = findViewById(R.id.AddContact_textLayoutLink);

        listKhoa = new ArrayList<>();
        listBoMon = new ArrayList<>();

        rdGroup.check(R.id.AddContact_radioChonAnh);
        textLayout_link.setVisibility(View.GONE);

        getListKhoa();
    }

    private void getListKhoa() {
        listKhoa.clear();
        db.collection("Faculty")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                listKhoa.add(snapshot.toObject(Faculty.class));
                            }
                            adapterKhoa = new ArrayAdapter<>(AddContact_Activity.this, android.R.layout.simple_spinner_dropdown_item, listKhoa);
                            spnKhoa.setAdapter(adapterKhoa);
                        } else {
                            Log.d(TAG, "onComplete: GET FACULTY FAILED");
                        }
                    }
                });
    }

    private void Events() {
        spnKhoa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onSelectedHandler(adapterView, view, i, l);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnBoMon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (listBoMon.size() == 0) {
                    departmentChose = "";
                }else {
                    departmentChose = listBoMon.get(i).getDepartment_id();
                    Toast.makeText(AddContact_Activity.this, ""+departmentChose, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (rdGroup.getCheckedRadioButtonId()) {
                    case R.id.AddContact_radioChonAnh:
                        isChooseImage = true;
                        textLayout_link.setVisibility(View.GONE);
                        imgAvt.setVisibility(View.VISIBLE);
                        break;
                    case R.id.AddContact_radioLink:
                        isChooseImage = false;
                        textLayout_link.setVisibility(View.VISIBLE);
                        imgAvt.setVisibility(View.GONE);
                        break;
                }
            }
        });

        imgAvt.setOnClickListener(view -> {
            requestPermission();
        });


    }

    private void onSelectedHandler(AdapterView<?> parent, View view, int position, long id) {
        listBoMon.clear();
        Faculty faculty = listKhoa.get(position);
        getDepartmentByFaculty(faculty.getFaculty_ID());
    }

    private void getDepartmentByFaculty(String faculty_id) {
        listBoMon.clear();
        db.collection("Department")
                .whereEqualTo("faculty_ID", faculty_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listBoMon.add(document.toObject(Department.class));
                            }
                            adapterBoMon = new ArrayAdapter<>(AddContact_Activity.this, android.R.layout.simple_spinner_dropdown_item, listBoMon);
                            spnBoMon.setAdapter(adapterBoMon);
                            if (listBoMon.size() > 0) {
                                departmentChose = listBoMon.get(0).getDepartment_id();
                            }else {
                                departmentChose = "";
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_them_lienhe, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_themLH_itXong:
                addContact();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addContact() {
        if (departmentChose.equals("") || departmentChose == null) {
            Toast.makeText(this, "Chọn khoa và bộ môn thích hợp", Toast.LENGTH_SHORT).show();
            return;
        }
        if (edtHoTen.getText().toString().trim().equals("")) {
            edtHoTen.setError("Thông tin trống");
            edtHoTen.requestFocus();
            return;
        }

        String faculty_ID = listKhoa.get(spnKhoa.getSelectedItemPosition()).getFaculty_ID();
        String name = edtHoTen.getText().toString().trim();
        String position = edtChucVu.getText().toString().trim().equals("") ? "Giảng viên" : edtChucVu.getText().toString().trim();
        String email = edtEmail.getText().toString().trim().equals("") ? "Không có" : edtChucVu.getText().toString().trim();
        String phone = edtSdt.getText().toString().trim().equals("") ? "Không có" : edtChucVu.getText().toString().trim();
        Contact contact = new Contact(faculty_ID, departmentChose, name, position, email, phone, "");
        if (isChooseImage == true) {
            saveContactWithImage(contact);
            clearUI();
            Toast.makeText(this, "Lưu liên hệ thành công", Toast.LENGTH_SHORT).show();
            return;
        }else {
            saveContactWithLink(contact);
            clearUI();
            return;
        }
    }

    private void clearUI(){
        edtHoTen.setText("");
        edtSdt.setText("");
        edtEmail.setText("");
        edtAvtLink.setText("");
        edtChucVu.setText("");
    }

    private void saveContactWithLink(Contact contact) {
        db.collection("Contact")
                .add(contact)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddContact_Activity.this, "Lưu liên hệ thành công", Toast.LENGTH_SHORT).show();
                        return;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddContact_Activity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
    }

    private void saveContactWithImage(Contact contact) {
        StorageReference storage = FirebaseStorage.getInstance().getReference("contact");
        Uri uri = uriAvt;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (uri != null) {
            String fileName =  "contact"+timestamp.getTime()+"."+getFileExtension(uri);
            StorageReference fileRef = storage.child(fileName);
            UploadTask uploadTask = fileRef.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(AddContact_Activity.this,"Lưu ảnh thất bại",Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String link_img = String.valueOf(uri);
                            contact.setAvt_link(link_img);
                            db.collection("Contact")
                                    .add(contact);
                        }
                    });
                }
            });
        }
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
        //them file Manifest dau tien
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openBottomPicker();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(AddContact_Activity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
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
                Picasso.get().load(uri).noFade().into(imgAvt);
                uriAvt = uri;
            }
        };

        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(this)
                .setOnImageSelectedListener(listener)
                .setCompleteButtonText("DONE")
                .setEmptySelectionText("NO IMAGE")
                .create();
        tedBottomPicker.show(getSupportFragmentManager());
    }
}