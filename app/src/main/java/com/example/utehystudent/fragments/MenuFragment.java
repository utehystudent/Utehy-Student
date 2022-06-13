package com.example.utehystudent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.MenuViewModel;
import com.example.utehystudent.activity.AccountSetting_Activity;
import com.example.utehystudent.activity.AttendanceActivity;
import com.example.utehystudent.activity.ClassManagementActivity;
import com.example.utehystudent.activity.TinTucKhoaActivity;
import com.example.utehystudent.activity.LoginActivity;
import com.example.utehystudent.activity.SubjectInTermManagementActivity;
import com.example.utehystudent.activity.ThoiKhoaBieuActivity;
import com.example.utehystudent.activity.ThuVienSoActivity;
import com.example.utehystudent.activity.TraCuuDiemActivity;
import com.squareup.picasso.Picasso;

public class MenuFragment extends Fragment {
    LinearLayout linearQT;
    ImageView imgAvt;
    TextView tvName, tvClass;
    MenuViewModel menuViewModel;
    Button btnClassManagement, btnSubjectManagement, btnDangXuat, btnDiemDanh, btnTinTuc, btnTKB, btnThuVienSo, btnTaiKhoan, btnTraCuuDiem;

    public MenuFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        InitView(view);
        EventClick();
        return view;
    }

    private void EventClick() {
        btnClassManagement.setOnClickListener(view -> {
            Intent it = new Intent(requireActivity(), ClassManagementActivity.class);
            startActivity(it);
        });
        btnSubjectManagement.setOnClickListener(view -> {
            Intent it = new Intent(requireActivity(), SubjectInTermManagementActivity.class);
            startActivity(it);
        });
        btnDangXuat.setOnClickListener(view -> DangXuatTaiKhoan());
        btnDiemDanh.setOnClickListener(view -> {
            Intent it = new Intent(requireActivity(), AttendanceActivity.class);
            startActivity(it);
        });
        btnTinTuc.setOnClickListener(view -> {
            Intent it = new Intent(requireActivity(), TinTucKhoaActivity.class);
            startActivity(it);
        });
        btnTKB.setOnClickListener(view -> {
            Intent it = new Intent(requireActivity(), ThoiKhoaBieuActivity.class);
            startActivity(it);
        });
        btnThuVienSo.setOnClickListener(view -> {
            Intent it = new Intent(requireActivity(), ThuVienSoActivity.class);
            startActivity(it);
        });
        btnTaiKhoan.setOnClickListener(view -> {
            Intent it = new Intent(requireActivity(), AccountSetting_Activity.class);
            startActivity(it);
        });
        btnTraCuuDiem.setOnClickListener(view -> {
            Intent it = new Intent(requireActivity(), TraCuuDiemActivity.class);
            startActivity(it);
        });
    }

    private void DangXuatTaiKhoan() {
        menuViewModel.SignOut();
        requireActivity().finish();
        Intent it = new Intent(requireActivity(), LoginActivity.class);
        startActivity(it);
    }

    private void InitView(View view) {
        tvName = view.findViewById(R.id.AccountSetting_tvName);
        tvClass = view.findViewById(R.id.AccountSetting_tvLop);
        imgAvt = view.findViewById(R.id.AccountSetting_imgAvt);
        linearQT = view.findViewById(R.id.Menu_layoutQuanTri);
        btnClassManagement = view.findViewById(R.id.Menu_btnQLTV);
        btnSubjectManagement = view.findViewById(R.id.Menu_btnQLMH);
        btnDangXuat = view.findViewById(R.id.Menu_btnDangXuat);
        btnDiemDanh = view.findViewById(R.id.Menu_btnDiemDanh);
        btnTinTuc = view.findViewById(R.id.Menu_btnCongThongTin);
        btnTKB = view.findViewById(R.id.Menu_btnTKB);
        btnThuVienSo = view.findViewById(R.id.Menu_btnThuVienSo);
        btnTaiKhoan = view.findViewById(R.id.Menu_btnTaiKhoan);
        btnTraCuuDiem = view.findViewById(R.id.Menu_btnTraCuuDiem);

        menuViewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);
        menuViewModel.getCurrentUser().observe(requireActivity(), user -> {
            String textInfo = "";
            if (user.getRegency().equals("lt")) {
                linearQT.setVisibility(View.VISIBLE);
                textInfo = "Mã sinh viên: " + user.getUsername() + "\nLớp: " + user.getClass_ID() + " - LỚP TRƯỞNG";
            } else {
                linearQT.setVisibility(View.GONE);
                textInfo = "Mã sinh viên: " + user.getUsername() + "\nLớp: " + user.getClass_ID() + " - THÀNH VIÊN";
            }
            tvClass.setText(textInfo);
            tvName.setText(user.getName().toUpperCase());
            Picasso.get().load(user.getAvt_link()).resize(300, 300).centerCrop().into(imgAvt);
        });
    }
}
