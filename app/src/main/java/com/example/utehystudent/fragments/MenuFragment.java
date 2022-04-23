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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.MenuViewModel;
import com.example.utehystudent.activity.ClassManagementActivity;
import com.example.utehystudent.model.User;
import com.squareup.picasso.Picasso;

public class MenuFragment extends Fragment {

    LinearLayout linearQT;
    ImageView imgAvt;
    TextView tvName, tvClass;
    MenuViewModel menuViewModel;
    Button btnClassManagement;

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
        EventClick();
        return view;
    }

    private void EventClick() {
        btnClassManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(requireActivity(), ClassManagementActivity.class);
                startActivity(it);
            }
        });
    }

    private void InitView(View view) {
        tvName = view.findViewById(R.id.Menu_tvTenSV);
        tvClass = view.findViewById(R.id.Menu_tvLop);
        imgAvt = view.findViewById(R.id.Menu_imgAvt);
        linearQT = view.findViewById(R.id.Menu_layoutQuanTri);
        btnClassManagement = view.findViewById(R.id.Menu_btnQLTV);

        menuViewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);
        menuViewModel.getCurrentUser().observe(requireActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user.getRegency().equals("lt")) {
                    linearQT.setVisibility(View.VISIBLE);
                    tvClass.setText("LỚP: "+user.getClass_ID()+" - Lớp trưởng");
                }else {
                    linearQT.setVisibility(View.GONE);
                    tvClass.setText("LỚP: "+user.getClass_ID()+" - Thành viên");
                }
                tvName.setText(user.getName().toUpperCase()+" ("+user.getUsername()+")");
                Picasso.get().load(user.getAvt_link()).into(imgAvt);
            }
        });
    }
}