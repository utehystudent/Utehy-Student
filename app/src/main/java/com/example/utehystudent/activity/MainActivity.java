package com.example.utehystudent.activity;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.utehystudent.Pushy.RegisterForPushNotificationsAsync;
import com.example.utehystudent.R;
import com.example.utehystudent.fragments.BangTinFragment;
import com.example.utehystudent.fragments.HomeFragment;
import com.example.utehystudent.fragments.LichHoatDongFragment;
import com.example.utehystudent.fragments.MenuFragment;
import com.example.utehystudent.fragments.ThongBaoFragment;

public class MainActivity extends AppCompatActivity {

    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new RegisterForPushNotificationsAsync(this).execute();
        Init();

    }
    private void Init() {
        bottomNavigation = findViewById(R.id.Main_bottomNavigation);
        //set up bottom navigation
        SetUpBottomNavigation();
    }

    private void SetUpBottomNavigation() {
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_calendar));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_news));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_noti));
        bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_menu));

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                Fragment fragment = new Fragment();
                switch (item.getId()) {
                    case 1 :
                        fragment = new LichHoatDongFragment();
                        break;
                    case 2 :
                        fragment = new BangTinFragment();
                        break;
                    case 3 :
                        fragment = new HomeFragment();
                        break;
                    case 4 :
                        fragment = new ThongBaoFragment();
                        break;
                    case 5 :
                        fragment = new MenuFragment();
                        break;
                }
                loadFragment(fragment);
            }
        });

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
            }
        });

        //set notification count
        bottomNavigation.setCount(4, "2");
        bottomNavigation.setCount(2, "3");
        //set home fragment is default
        bottomNavigation.show(3, true);


        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                if(item.getId() == 3) {
                    loadFragment(new HomeFragment());
                }
            }
        });

    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.Main_frameLayout, fragment)
                .commit();
    }
}