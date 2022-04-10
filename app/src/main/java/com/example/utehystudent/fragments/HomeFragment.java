package com.example.utehystudent.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.LoginViewModel;
import com.example.utehystudent.ViewModel.ScheduleViewModel;
import com.example.utehystudent.model.Schedule_detail;
import com.example.utehystudent.model.User;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {
    final String TAG = "Home";
    ImageView imgAvt;
    TextView tvName, tvClass, tvXinChao, tvMorning, tvAfternoon;
    ScheduleViewModel scheduleViewModel;
    LoginViewModel loginViewModel;

    public HomeFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imgAvt = view.findViewById(R.id.Home_imgAvt);
        tvName = view.findViewById(R.id.Home_tvTenSV);
        tvClass = view.findViewById(R.id.Home_tvLop);
        tvXinChao = view.findViewById(R.id.Home_tvXinChao);
        tvMorning = view.findViewById(R.id.Home_tvMorning);
        tvAfternoon = view.findViewById(R.id.Home_tvAfternoon);

        //view model
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        loginViewModel.getUserFromFirestore();
        loginViewModel.getUserLiveData().observe(requireActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                imgAvt.setPadding(0,0,0,0);
                Picasso.get().load(user.getAvt_link()).into(imgAvt);
                String name = user.getName() +" ("+user.getUsername()+")";
                tvName.setText(name);
                tvClass.setText("LỚP: "+user.getClass_ID());
                tvXinChao.setText("XIN CHÀO");
            }
        });


        scheduleViewModel = new ViewModelProvider(requireActivity()).get(ScheduleViewModel.class);
        scheduleViewModel.getSchedule_detailMutableLiveData().observe(requireActivity(), new Observer<Schedule_detail>() {
            @Override
            public void onChanged(Schedule_detail schedule_detail) {
                tvMorning.setText(schedule_detail.getMorning());
                tvAfternoon.setText(schedule_detail.getAfternoon());
            }
        });

//        SharedPreferences preferences = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
//        imgAvt.setPadding(0,0,0,0);
//        Picasso.get().load(preferences.getString("avt_link", "https://daotaotuyensinh.com/images/hung-yn-400x400.jpg")).into(imgAvt);
//        String name = preferences.getString("name","") +" ("+preferences.getString("username", "")+")";
//        tvName.setText(name);
//        tvClass.setText("LỚP: "+preferences.getString("class_ID", ""));
//        tvXinChao.setText("XIN CHÀO");


        return view;
    }
}