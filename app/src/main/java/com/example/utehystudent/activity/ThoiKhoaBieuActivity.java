package com.example.utehystudent.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.ThoiKhoaBieuAdapter;
import com.example.utehystudent.model.Schedule_detail;
import com.example.utehystudent.model.ThoiKhoaBieu;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ThoiKhoaBieuActivity extends AppCompatActivity {
    private static final String TAG = "ThoiKhoaBieuActivity";
    Toolbar toolbar;
    RecyclerView rcv;
    ThoiKhoaBieuAdapter tkbAdapter;
    ArrayList<ThoiKhoaBieu> listTKB;
    FirebaseFirestore db;
    String classID = "";
    View parentLayout;
    ProgressBar prgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_thoi_khoa_bieu);

        Init();
        parentLayout = findViewById(android.R.id.content);
    }

    private void Init() {
        toolbar = findViewById(R.id.TKB_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("THỜI KHÓA BIỂU");
        toolbar.setNavigationOnClickListener(v -> finish());

        prgBar = findViewById(R.id.TKB_prgBar);

        db = FirebaseFirestore.getInstance();

        listTKB = new ArrayList<>();

        rcv = findViewById(R.id.TKB_rcv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(linearLayoutManager);
        tkbAdapter = new ThoiKhoaBieuAdapter(this, listTKB);

        //get data
        getData();
    }

    private void getData() {
        rcv.setVisibility(View.GONE);
        prgBar.setVisibility(View.VISIBLE);
        listTKB.clear();
        SharedPreferences pref = getSharedPreferences("User", Context.MODE_PRIVATE);
        classID = pref.getString("class_ID", "");
        listTKB.add(new ThoiKhoaBieu("Monday", "", "", ""));
        listTKB.add(new ThoiKhoaBieu("Tuesday", "", "", ""));
        listTKB.add(new ThoiKhoaBieu("Wednesday", "", "", ""));
        listTKB.add(new ThoiKhoaBieu("Thursday", "", "", ""));
        listTKB.add(new ThoiKhoaBieu("Friday", "", "", ""));
        listTKB.add(new ThoiKhoaBieu("Saturday", "", "", ""));
        listTKB.add(new ThoiKhoaBieu("Sunday", "", "", ""));

        for (ThoiKhoaBieu tkb : listTKB) {
            String weekday = tkb.getThu();
            db.collection("Schedule")
                    .document(classID)
                    .collection("Schedule_detail")
                    .document(weekday)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Schedule_detail detail = documentSnapshot.toObject(Schedule_detail.class);
                        tkb.setSang(detail.getMorning());
                        tkb.setChieu(detail.getAfternoon());
                        tkb.setToi(detail.getEvening());
                        Log.d("tkb", "onSuccess: " + tkb.toString());
                        tkbAdapter.notifyDataSetChanged();
                    });
        }
        rcv.setAdapter(tkbAdapter);
        prgBar.setVisibility(View.GONE);
        rcv.setVisibility(View.VISIBLE);
    }

    public void clickItemTKB(ThoiKhoaBieu tkb) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_chitiet_tkb);
        TextView tvThu = dialog.findViewById(R.id.dialogCTTKB_tvThu);
        EditText edtSang = dialog.findViewById(R.id.dialogCTTKB_edtTenMHSang);
        EditText edtChieu = dialog.findViewById(R.id.dialogCTTKB_edtTenMHChieu);
        EditText edtToi = dialog.findViewById(R.id.dialogCTTKB_edtTenMHToi);
        Button btnLuu = dialog.findViewById(R.id.dialogCTTKB_btnLuu);

        tvThu.setText(getThu(tkb.getThu()));
        edtSang.setText(tkb.getSang());
        edtChieu.setText(tkb.getChieu());
        edtToi.setText(tkb.getToi());

        btnLuu.setOnClickListener(it -> {
            tkb.setSang(edtSang.getText().toString().trim());
            tkb.setChieu(edtChieu.getText().toString().trim());
            tkb.setToi(edtToi.getText().toString().trim());

            if (edtSang.getText().toString().equals("")
                    || edtSang.getText().toString().equalsIgnoreCase("nghỉ")
                    || edtSang.getText().toString().equalsIgnoreCase("nghi")) {
                tkb.setSang("Nghỉ");
            }
            if (edtChieu.getText().toString().equals("")
                    || edtChieu.getText().toString().equalsIgnoreCase("nghỉ")
                    || edtChieu.getText().toString().equalsIgnoreCase("nghi")) {
                tkb.setChieu("Nghỉ");
            }
            if (edtToi.getText().toString().equals("")
                    || edtToi.getText().toString().equalsIgnoreCase("nghỉ")
                    || edtToi.getText().toString().equalsIgnoreCase("nghi")) {
                tkb.setToi("Nghỉ");
            }

            //update dữ liệu lên firestore
            db.collection("Schedule")
                    .document(classID)
                    .collection("Schedule_detail")
                    .document(tkb.getThu())
                    .update("morning", tkb.getSang()
                            , "afternoon", tkb.getChieu()
                            , "evening", tkb.getToi())
                    .addOnSuccessListener(unused -> {
                        Snackbar.make(parentLayout, "Cập nhật thời khóa biểu thành công", Snackbar.LENGTH_SHORT).show();
                        getData();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ThoiKhoaBieuActivity.this, "Đã có lỗi xảy ra. Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "clickItemTKB: " + e.getMessage());
                        dialog.dismiss();
                        return;
                    });
        });

        dialog.create();
        dialog.show();
    }

    private String getThu(String s) {
        String rs = "";
        switch (s) {
            case "Monday":
                rs = "Thứ hai";
                break;
            case "Tuesday":
                rs = "Thứ ba";
                break;
            case "Wednesday":
                rs = "Thứ tư";
                break;
            case "Thursday":
                rs = "Thứ năm";
                break;
            case "Friday":
                rs = "Thứ sáu";
                break;
            case "Saturday":
                rs = "Thứ bảy";
                break;
            case "Sunday":
                rs = "Chủ nhật";
                break;
            default:
                rs = "";
                break;
        }
        return rs;
    }
}