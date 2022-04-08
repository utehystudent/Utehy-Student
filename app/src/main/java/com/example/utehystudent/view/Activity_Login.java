package com.example.utehystudent.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.utehystudent.R;
import com.example.utehystudent.ViewModel.LoginViewModel;

public class Activity_Login extends AppCompatActivity {

    EditText edtTK, edtMK;
    Button btnLogin;
    LoginViewModel loginViewModel;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init();
        EventClick();
    }

    private void EventClick() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
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

                loginViewModel.LoginWithAccount(username, password);
            }
        });
    }

    private void Init() {
        edtTK = findViewById(R.id.Login_edtTK);
        edtMK= findViewById(R.id.Login_edtMK);
        btnLogin = findViewById(R.id.Login_btnDN);

        progressDialog = new ProgressDialog(Activity_Login.this);
        progressDialog.setMessage("Đăng nhập tài khoản ...");
        progressDialog.setCancelable(false);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.isLoginSuccess.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean == true) {
                    Toast.makeText(Activity_Login.this, "Login success", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else {
                    edtTK.setError("Kiểm tra lại tài khoản");
                    edtMK.setError("Kiểm tra lại tài khoản");
                    edtMK.setText("");
                    progressDialog.dismiss();
                    return;
                }
            }
        });
    }
}