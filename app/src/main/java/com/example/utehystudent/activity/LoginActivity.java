package com.example.utehystudent.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.LoginViewModel;
import com.example.utehystudent.repository.UserRepo;

public class LoginActivity extends AppCompatActivity {
    EditText edtTK, edtMK;
    Button btnLogin;
    LoginViewModel loginViewModel;
    ProgressDialog progressDialog;
    UserRepo userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init();
        EventClick();
    }

    private void EventClick() {
        btnLogin.setOnClickListener(view -> {
            String username = edtTK.getText().toString().trim().toLowerCase();
            String password = edtMK.getText().toString().trim();

            if (username.equalsIgnoreCase("") && password.equalsIgnoreCase("")) {
                edtTK.setError("Thông tin trống");
                edtMK.setError("Thông tin trống");
                return;
            }

            if (username.equalsIgnoreCase("")) {
                edtTK.setError("Tên tài khoản trống");
                return;
            }

            if (password.equalsIgnoreCase("")) {
                edtMK.setError("Mật khẩu trống");
                return;
            }

            progressDialog.show();
            loginViewModel.SignInAccount(username, password);

        });
    }

    private void Init() {
        edtTK = findViewById(R.id.Login_edtTK);
        edtMK= findViewById(R.id.Login_edtMK);
        btnLogin = findViewById(R.id.Login_btnDN);

        userRepo = new UserRepo(getApplication());

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.setCancelable(false);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getIsSuccess().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean == true) {
                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(it);
                }else {
                    Toast.makeText(LoginActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}