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
import com.example.utehystudent.ViewModel.UserViewModel;
import com.example.utehystudent.model.User;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {
    final String TAG = "Home";
    ImageView imgAvt;
    TextView tvName, tvClass, tvXinChao, tvMorning, tvAfternoon;
    UserViewModel userViewModel;
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

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.GetUserData();
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
        return view;
    }
}