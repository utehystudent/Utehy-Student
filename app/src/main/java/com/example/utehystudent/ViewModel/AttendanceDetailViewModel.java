package com.example.utehystudent.ViewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.utehystudent.model.Attendance;

import java.util.ArrayList;

public class AttendanceDetailViewModel extends AndroidViewModel {
    Application application;
    private MutableLiveData<ArrayList<Attendance>> listAttendanceLiveData;

    public AttendanceDetailViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        listAttendanceLiveData = new MutableLiveData<>();
    }
}
