package com.example.utehystudent.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utehystudent.R;
import com.example.utehystudent.activity.admin.HomeAdmin_Activity;

public class SplashActivity extends AppCompatActivity {
    final String TAG = "SplashActivity";
    final Handler handler = new Handler(Looper.getMainLooper());;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //kiểm tra xem đã có tài khoản nào đăng nhập chưa
        SharedPreferences preferences = this.getSharedPreferences("Account", Context.MODE_PRIVATE);
        String username = preferences.getString("username", "");
        String password = preferences.getString("password", "");
        String account_type = preferences.getString("account_type", "");
        String device_token = preferences.getString("device_token", "");

        Toast.makeText(SplashActivity.this, ""+username, Toast.LENGTH_SHORT).show();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent it;
                //nếu đã có tài khoản đăng nhập -> chuyển sang màn hình Home
                if (!username.equals("")) {
                    if (username.equals("admin")) {
                        it = new Intent(SplashActivity.this, HomeAdmin_Activity.class);
                        startActivity(it);
                        return;
                    }
                    it = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(it);
                //nếu chưa -> chuyển sang màn đăng nhập tài khoản
                }else if (username.equals("")){
                    it = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(it);
                    finish();
                }
            }
        }, 1300);
    }

    @Override
    public void onBackPressed() {

    }
}