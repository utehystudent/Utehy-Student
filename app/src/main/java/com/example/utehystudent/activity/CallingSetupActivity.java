package com.example.utehystudent.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.utehystudent.R;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallingSetupActivity extends AppCompatActivity {
    Toolbar toolbar;
    CircleImageView imgAvt;
    TextInputEditText edtMaHop, edtTen;
    Button btnXong;
    ImageView imgMic, imgCam;
    SharedPreferences pref;

    Boolean hasMic, hasCamera, useAccountAvt;
    String avtLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_calling_setup);

        Init();
        Events();
    }

    private void Events() {
        imgMic.setOnClickListener(view -> {
            if (hasMic == false) {
                Toast.makeText(this, "Sử dụng micro", Toast.LENGTH_SHORT).show();
                imgMic.setImageResource(R.drawable.ic_mic_on);
                hasMic = true;
            }else {
                Toast.makeText(this, "Đã tắt micro", Toast.LENGTH_SHORT).show();
                imgMic.setImageResource(R.drawable.ic_mic_off);
                hasMic = false;
            }
        });

        imgCam.setOnClickListener(view -> {
            if (hasCamera == false) {
                Toast.makeText(this, "Sử dụng camera", Toast.LENGTH_SHORT).show();
                imgCam.setImageResource(R.drawable.ic_cam_on);
                hasCamera = true;
            }else {
                Toast.makeText(this, "Đã tắt camera", Toast.LENGTH_SHORT).show();
                imgCam.setImageResource(R.drawable.ic_cam_off);
                hasCamera = false;
            }
        });

        imgAvt.setOnClickListener(view -> {
            if (useAccountAvt == true) {
                useAccountAvt = false;
                Picasso.get().load("https://www.kindpng.com/picc/m/22-223863_no-avatar-png-circle-transparent-png.png").noFade().into(imgAvt);
                avtLink = "https://www.kindpng.com/picc/m/22-223863_no-avatar-png-circle-transparent-png.png";
                Toast.makeText(this, "Đã ẩn ảnh đại diện", Toast.LENGTH_SHORT).show();
            }else {
                useAccountAvt = true;
                try {
                    Picasso.get().load(pref.getString("avt_link", "")).noFade().into(imgAvt);
                    avtLink = pref.getString("avt_link", "");
                    Toast.makeText(this, "Hiển thị ảnh đại diện", Toast.LENGTH_SHORT).show();
                }catch (Exception e) {
                    Picasso.get().load("https://www.kindpng.com/picc/m/22-223863_no-avatar-png-circle-transparent-png.png").noFade().into(imgAvt);
                    avtLink = "https://www.kindpng.com/picc/m/22-223863_no-avatar-png-circle-transparent-png.png";
                    Toast.makeText(this, "Có lỗi xảy ra. Sử dụng ảnh mặc định thay thế", Toast.LENGTH_SHORT).show();
                }
            }
            Log.d("kkk", "Events: AVT: "+avtLink);
        });

        btnXong.setOnClickListener(view -> {
            if (edtMaHop.getText().toString().trim().length() < 6) {
                edtMaHop.setError("");
                Toast.makeText(this, "Mã cuộc họp không hợp lệ (trên 6 kí tự)", Toast.LENGTH_SHORT).show();
                edtMaHop.requestFocus();
                return;
            }
            if (edtTen.getText().toString().trim().equals("")) {
                edtTen.setError("Nhập tên hiển thị");
                edtTen.requestFocus();
                return;
            }
            Intent intent = new Intent(this, CallingActivity.class);
            intent.putExtra("avt_link", avtLink);
            intent.putExtra("name", edtTen.getText().toString().trim());
            intent.putExtra("code", edtMaHop.getText().toString().trim());
            intent.putExtra("mic", hasMic);
            intent.putExtra("camera", hasCamera);
            startActivity(intent);
            finish();
        });

    }

    private void Init() {
        toolbar = findViewById(R.id.CallingSetup_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("THIẾT LẬP CUỘC GỌI");
        toolbar.setNavigationOnClickListener(v -> finish());
        //
        pref = getSharedPreferences("User", Context.MODE_PRIVATE);
        //
        imgAvt = findViewById(R.id.CallingSetup_imgAvt);
        edtMaHop = findViewById(R.id.CallingSetup_edtMaHop);
        edtTen = findViewById(R.id.CallingSetup_edtTen);
        btnXong = findViewById(R.id.CallingSetup_btnXong);
        imgMic = findViewById(R.id.CallingSetup_imgMic);
        imgCam = findViewById(R.id.CallingSetup_imgCam);
        //
        hasMic = false;
        hasCamera = false;
        useAccountAvt = true;
        //
        String name = pref.getString("name", "");
        edtTen.setText(name);
        //
        try {
            Picasso.get().load(pref.getString("avt_link", "")).noFade().into(imgAvt);
            avtLink = pref.getString("avt_link", "");
        }catch (Exception e) {
            Picasso.get().load("https://www.kindpng.com/picc/m/22-223863_no-avatar-png-circle-transparent-png.png").noFade().into(imgAvt);
            avtLink = "https://www.kindpng.com/picc/m/22-223863_no-avatar-png-circle-transparent-png.png";
        }
    }
}