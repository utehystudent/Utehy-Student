package com.example.utehystudent.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.utehystudent.model.Subject;
import com.example.utehystudent.model.SubjectsOfSemester;
import com.example.utehystudent.model.SubjectsOfSemester_Detail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SubjectManagementViewModel extends AndroidViewModel {
    final String TAG = "SubjectManagementViewModel";
    String classID = "";
    Application application;
    public MutableLiveData<ArrayList<Subject>> listSubjectLiveData = new MutableLiveData<>();
    MutableLiveData<SubjectsOfSemester> subjectOfSemesterLiveData = new MutableLiveData<>();
    ArrayList<Subject> listSubject;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SubjectManagementViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        listSubject = new ArrayList<>();
        GetSemesterInfo();
        GetSubjectDetailList();
    }

    public void GetSemesterInfo() {
        SharedPreferences preferences = application.getSharedPreferences("User", Context.MODE_PRIVATE);
        classID = preferences.getString("class_ID", "");
        db.collection("SubjectsOfSemester")
                .whereEqualTo("class_ID", classID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                subjectOfSemesterLiveData.setValue(document.toObject(SubjectsOfSemester.class));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void GetSubjectDetailList() {
        ArrayList<SubjectsOfSemester_Detail> listDetail = new ArrayList<>();
        db.collection("SubjectsOfSemester_Detail")
                .whereEqualTo("class_ID", classID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listDetail.add(document.toObject(SubjectsOfSemester_Detail.class));
                            }

                            listSubject = GetSubject(listDetail);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (listSubjectLiveData.getValue() != null) {
                                        listSubjectLiveData.setValue(listSubject);
                                        handler.removeCallbacks(this::run);
                                    }else {
                                        handler.postDelayed(this, 500);
                                    }
                                }
                            }, 500);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        listSubjectLiveData.setValue(listSubject);
    }

    public MutableLiveData<ArrayList<Subject>> getListSubjectLiveData() {
        return listSubjectLiveData;
    }

    public MutableLiveData<SubjectsOfSemester> getSubjectOfSemesterLiveData() {
        return subjectOfSemesterLiveData;
    }

    private ArrayList<Subject> GetSubject(ArrayList<SubjectsOfSemester_Detail> listDetail) {
        ArrayList<Subject> subjects = new ArrayList<>();
        for (SubjectsOfSemester_Detail detail : listDetail) {
            db.collection("Subject")
                    .whereEqualTo("subject_ID", detail.getSubject_ID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, "onComplete: " + task.getResult().toObjects(Subject.class).toString());
                                    subjects.add(document.toObject(Subject.class));
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        return subjects;
    }
}
