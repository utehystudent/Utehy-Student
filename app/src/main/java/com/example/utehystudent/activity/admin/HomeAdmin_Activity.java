package com.example.utehystudent.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utehystudent.R;

public class HomeAdmin_Activity extends AppCompatActivity {
    Button btnSV, btnLH, btnBV, btnMH, btnPB, btnBM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_admin);

        Init();
        Events();
    }

    private void Init() {
        btnSV = findViewById(R.id.admin_btnSV);
        btnLH = findViewById(R.id.admin_btnLH);
        btnBV = findViewById(R.id.admin_btnBV);
        btnMH = findViewById(R.id.admin_btnMH);
        btnPB = findViewById(R.id.admin_btnPB);
        btnBM = findViewById(R.id.admin_btnBM);
    }

    private void Events() {
        btnSV.setOnClickListener(view -> {
            startActivity(new Intent(this, QLSV_Activity.class));
        });
        btnBM.setOnClickListener(view -> {
            startActivity(new Intent(this, BaoMat_Activity.class));
        });
        btnBV.setOnClickListener(view -> {
            startActivity(new Intent(this, PostList_Activity.class));
        });
    }
}