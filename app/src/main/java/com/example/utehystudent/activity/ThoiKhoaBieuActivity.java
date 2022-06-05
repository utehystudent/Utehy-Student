package com.example.utehystudent.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utehystudent.R;
import com.example.utehystudent.adapters.ThoiKhoaBieuAdapter;
import com.example.utehystudent.model.Schedule_detail;
import com.example.utehystudent.model.ThoiKhoaBieu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ThoiKhoaBieuActivity extends AppCompatActivity {
    private static final String TAG = "ThoiKhoaBieuActivity";
    Toolbar toolbar;
    RecyclerView rcv;
    ThoiKhoaBieuAdapter tkbAdapter;
    ArrayList<ThoiKhoaBieu> listTKB;
    FirebaseFirestore db;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_thoi_khoa_bieu);

        Init();

    }

    private void Init() {
        toolbar = findViewById(R.id.TKB_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("THỜI KHÓA BIỂU");
        toolbar.setNavigationOnClickListener(v -> finish());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.show();

        db = FirebaseFirestore.getInstance();

        listTKB = new ArrayList<>();

        rcv = findViewById(R.id.TKB_rcv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(linearLayoutManager);
        tkbAdapter = new ThoiKhoaBieuAdapter(this, listTKB);
        rcv.setAdapter(tkbAdapter);

        //get data
        getData();

    }

    private void getData() {
        SharedPreferences pref = getSharedPreferences("User", Context.MODE_PRIVATE);
        String classID = pref.getString("class_ID", "");
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
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Schedule_detail detail = documentSnapshot.toObject(Schedule_detail.class);
                            tkb.setSang(detail.getMorning());
                            tkb.setChieu(detail.getAfternoon());
                            tkb.setToi(detail.getEvening());
                            Log.d("tkb", "onSuccess: " + tkb.toString());
                            tkbAdapter.notifyDataSetChanged();
                        }
                    });
        }
        progressDialog.dismiss();
    }
    
    public void clickItemTKB(ThoiKhoaBieu tkb) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_chitiet_tkb);
        TextView tvThu = dialog.findViewById(R.id.dialogCTTKB_tvThu);
        EditText edtSang = dialog.findViewById(R.id.dialogCTTKB_edtTenMHSang);
        EditText edtChieu = dialog.findViewById(R.id.dialogCTTKB_edtTenMHChieu);
        EditText edtToi = dialog.findViewById(R.id.dialogCTTKB_edtTenMHToi);

        tvThu.setText(getThu(tkb.getThu()));
        edtSang.setText(tkb.getSang());
        edtChieu.setText(tkb.getChieu());
        edtToi.setText(tkb.getToi());


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
            default: rs = ""; break;
        }
        return rs;
    }
}