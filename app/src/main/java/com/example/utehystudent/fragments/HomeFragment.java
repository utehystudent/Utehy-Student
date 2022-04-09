package com.example.utehystudent.fragments;

import android.media.Image;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.LoginViewModel;
import com.example.utehystudent.model.User;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {
    ImageView imgAvt;
    TextView tvName, tvClass, tvXinChao;
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
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        loginViewModel.getUserLiveData().observe(requireActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                imgAvt.setPadding(0,0,0,0);
                Picasso.get().load(user.getAvt_link()).into(imgAvt);
                String name = user.getName().toUpperCase() +" ("+user.getUsername()+")";
                tvName.setText(name);
                tvClass.setText("LỚP: "+user.getClass_ID());
                tvXinChao.setText("XIN CHÀO");
            }
        });
        return view;
    }
}