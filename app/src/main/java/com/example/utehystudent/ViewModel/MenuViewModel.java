package com.example.utehystudent.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.utehystudent.activity.MainActivity;
import com.example.utehystudent.model.User;
import com.example.utehystudent.repository.UserRepo;

public class MenuViewModel extends AndroidViewModel {
    Application context;
    MutableLiveData<User> currentUser;
    UserRepo userRepo;

    public MenuViewModel(@NonNull Application application) {
        super(application);
        this.context = application;
        currentUser = new MutableLiveData<>();
        userRepo = new UserRepo(application);
        GetCurrentUser();
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser.setValue(currentUser);
    }

    private void GetCurrentUser () {
        userRepo = new UserRepo(context);
        User user = userRepo.GetUserFromSF();
        this.setCurrentUser(user);
    }

    public void SignOut () {
        //Delete user data from SharedPreferences
        SharedPreferences preferencesAccount = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
        SharedPreferences preferencesUser = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        preferencesAccount.edit().clear().commit();
        preferencesUser.edit().clear().commit();
        MainActivity.DeleteDataAttendance();
    }
}
