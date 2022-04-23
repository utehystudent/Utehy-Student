package com.example.utehystudent.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

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
}
