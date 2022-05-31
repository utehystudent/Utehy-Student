package com.example.utehystudent.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.utehystudent.model.Activity;

import java.util.ArrayList;

public class LichHoatDongViewModel extends AndroidViewModel {
    Application application;
    public MutableLiveData<ArrayList<Activity>> listActivityMutableLiveData;

    public LichHoatDongViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        listActivityMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<Activity>> getListActivityMutableLiveData() {
        return listActivityMutableLiveData;
    }
}


