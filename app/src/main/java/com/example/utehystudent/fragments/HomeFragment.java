package com.example.utehystudent.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.ScheduleViewModel;
import com.example.utehystudent.ViewModel.UserViewModel;
import com.example.utehystudent.model.Schedule_detail;
import com.example.utehystudent.model.User;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {
    final String TAG = "Home";
    ImageView imgAvt;
    TextView tvName, tvClass, tvXinChao, tvMorning, tvAfternoon, tvEvening;
    UserViewModel userViewModel;
    ScheduleViewModel scheduleViewModel;
    Dialog dialog;
    LinearLayout linearEvening;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imgAvt = view.findViewById(R.id.Home_imgAvt);
        tvName = view.findViewById(R.id.Home_tvTenSV);
        tvClass = view.findViewById(R.id.Home_tvLop);
        tvXinChao = view.findViewById(R.id.Home_tvXinChao);
        tvMorning = view.findViewById(R.id.Home_tvMorning);
        tvAfternoon = view.findViewById(R.id.Home_tvAfternoon);
        tvEvening = view.findViewById(R.id.Home_tvEvening);
        linearEvening = view.findViewById(R.id.Home_linearEvening);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        scheduleViewModel = new ViewModelProvider(requireActivity()).get(ScheduleViewModel.class);
        
        BindData();
        return view;
    }

    private void BindData() {
        ShowLoadingDialog();
        userViewModel.getUserLiveData().observe(requireActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                try{
                    Picasso.get().load(user.getAvt_link()).resize(300, 300).centerCrop().into(imgAvt);
                }catch (Exception e) {
                    imgAvt.setImageResource(R.drawable.utehy_logo);
                }
                tvName.setText(user.getName()+"");
                tvClass.setText("Mã SV: "+user.getUsername()+"  |  "+"Lớp: "+user.getClass_ID());
                tvXinChao.setText("XIN CHÀO");
            }
        });

        scheduleViewModel.GetData();
        scheduleViewModel.getScheduleLiveData().observe(requireActivity(), new Observer<Schedule_detail>() {
            @Override
            public void onChanged(Schedule_detail schedule_detail) {
                if (schedule_detail != null) {
                    tvMorning.setText(schedule_detail.getMorning());
                    tvAfternoon.setText(schedule_detail.getAfternoon());
                    if (schedule_detail.getEvening().equals("N/A") || schedule_detail.getEvening().equals("Nghỉ")) {
                        linearEvening.setVisibility(View.GONE);
                    }else {
                        linearEvening.setVisibility(View.VISIBLE);
                        tvEvening.setText(schedule_detail.getEvening());
                    }
                    //dismiss loading dialog
                    dialog.dismiss();
                }
            }
        });
    }

    private void ShowLoadingDialog() {
        dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.loading_layout1);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.create();
        dialog.show();
    }
}