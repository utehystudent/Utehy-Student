package com.example.utehystudent.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.utehystudent.model.User;

public class UserViewModel extends AndroidViewModel {
    private MutableLiveData<User> userLiveData;
    private User user;
    Application application;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        userLiveData = new MutableLiveData<>();
        user = new User();
        GetUserData();
    }

    public void GetUserData() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user.getUsername() == null) {
                   SharedPreferences preferences = application.getSharedPreferences("User", Context.MODE_PRIVATE);
                   String username = preferences.getString("username", "");
                   String faculty_ID = preferences.getString("faculty_ID", "");
                   String class_ID = preferences.getString("class_ID", "");
                   String name = preferences.getString("name", "");
                   String regency = preferences.getString("regency", "");
                   String avt_link = preferences.getString("avt_link", "");
                   user = new User(username, faculty_ID, class_ID, name, regency, avt_link);
                   userLiveData.setValue(user);
                   handler.removeCallbacks(this::run);
                }else {
                    handler.postDelayed(this, 500);
                }
            }
        }, 500);
    }

    public MutableLiveData<User> getUserLiveData() {
        return userLiveData;
    }


}
