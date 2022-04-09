package com.example.utehystudent.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.LoginViewModel;
import com.example.utehystudent.model.Account;

public class SplashActivity extends AppCompatActivity {
    final Handler handler = new Handler(Looper.getMainLooper());;
    LoginViewModel loginViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

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
                    loginViewModel.setAccountLiveData(new Account(username, password, account_type, device_token));
                    it = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(it);
                    finish();
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