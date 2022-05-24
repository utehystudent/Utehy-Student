package com.example.utehystudent.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.utehystudent.model.SubjectAbsent;

import java.util.ArrayList;

public class HomeFragmentViewModel extends AndroidViewModel {
    final String TAG = "HomeFragmentViewModel";
    Application application;
    private MutableLiveData<ArrayList<SubjectAbsent>> listSubjectAbsentLiveData;

    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        listSubjectAbsentLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<SubjectAbsent>> getListSubjectAbsentLiveData() {
        return listSubjectAbsentLiveData;
    }

    public void setListSubjectAbsentLiveData(ArrayList<SubjectAbsent> listSubjectAbsentLiveData) {
        this.listSubjectAbsentLiveData.setValue(listSubjectAbsentLiveData);
    }
}
