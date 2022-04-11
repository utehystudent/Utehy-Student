package com.example.utehystudent.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.utehystudent.model.Account;
import com.example.utehystudent.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountRepo {
    UserRepo userRepo;
    final String TAG = "AccountRepo";
    FirebaseFirestore db;
    Application application;

    public AccountRepo(Application application) {
        this.db = FirebaseFirestore.getInstance();
        this.application = application;
        userRepo = new UserRepo(application);
    }

    public void SaveAccountToSF(Account account) {
        SharedPreferences preferences = application.getSharedPreferences("Account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username",account.getUsername());
        editor.putString("password",account.getPassword());
        editor.putString("account_type",account.getAccount_type());
        editor.putString("device_token",account.getDevice_token());
        editor.commit();
        Log.d(TAG, "SaveAccountToSF: success");
        String us = preferences.getString("username","");
        Log.d(TAG, "Account: "+us);
        //
        User user = userRepo.GetUserFromFirestore(account.getUsername());
//        userRepo.SaveUserToSF(user);
    }
}
