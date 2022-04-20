package com.example.utehystudent.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.example.utehystudent.R;
import com.example.utehystudent.model.User;
import com.example.utehystudent.repository.UserRepo;
import com.squareup.picasso.Picasso;

public class MenuFragment extends Fragment {

    LinearLayout linearQT;
    UserRepo userRepo;
    ImageView imgAvt;
    TextView tvName, tvClass;

    public MenuFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        InitView(view);

        return view;
    }

    private void InitView(View view) {
        tvName = view.findViewById(R.id.Menu_tvTenSV);
        tvClass = view.findViewById(R.id.Menu_tvLop);
        imgAvt = view.findViewById(R.id.Menu_imgAvt);
        linearQT = view.findViewById(R.id.Menu_layoutQuanTri);

        userRepo = new UserRepo(requireActivity().getApplication());
        User user = userRepo.GetUserFromSF();
        if (user.getRegency().equals("lt")) {
            linearQT.setVisibility(View.VISIBLE);
        }

        if (user.getRegency().equals("lt")) {
            tvClass.setText("LỚP: "+user.getClass_ID()+" - Lớp trưởng");
        }else {
            tvClass.setText("LỚP: "+user.getClass_ID()+" - Thành viên");
        }
        tvName.setText(user.getName().toUpperCase()+" ("+user.getUsername()+")");
        Picasso.get().load(user.getAvt_link()).into(imgAvt);
    }
}