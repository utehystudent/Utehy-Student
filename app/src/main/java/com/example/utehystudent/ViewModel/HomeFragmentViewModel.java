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
import com.example.utehystudent.model.SubjectAbsent;
import com.example.utehystudent.model.SubjectsOfSemester_Detail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragmentViewModel extends AndroidViewModel {
    final String TAG = "HomeFragmentViewModel";
    Application application;
    ArrayList<Subject> listSubjectInTerm;
    ArrayList<SubjectAbsent> listSubjectAbsent;
    FirebaseFirestore db;
    private MutableLiveData<ArrayList<SubjectAbsent>> listSubjectAbsentLiveData;
    private MutableLiveData<ArrayList<Subject>> listSubjectInTermLiveData;


    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        listSubjectAbsentLiveData = new MutableLiveData<>();
        listSubjectInTermLiveData = new MutableLiveData<>();
        listSubjectInTerm = new ArrayList<>();
        listSubjectAbsent = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        //get data
        GetListSubjectInTerm();

    }

    public MutableLiveData<ArrayList<SubjectAbsent>> getListSubjectAbsentLiveData() {
        return listSubjectAbsentLiveData;
    }

    public MutableLiveData<ArrayList<Subject>> getListSubjectInTermLiveData() {
        return listSubjectInTermLiveData;
    }

    public void setListSubjectInTermLiveData(ArrayList<Subject> listSubjectInTerm) {
        this.listSubjectInTermLiveData.setValue(listSubjectInTerm);
    }

    private void GetListSubjectInTerm() {
        SharedPreferences pref = application.getSharedPreferences("User", Context.MODE_PRIVATE);
        String classID = pref.getString("class_ID", "");
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

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    listSubjectInTerm = GetSubject(listDetail);

                                    if (listSubjectInTermLiveData.getValue() != null || listSubjectInTermLiveData.getValue().size() != listSubjectInTerm.size()) {
                                        setListSubjectInTermLiveData(listSubjectInTerm);
                                        handler.removeCallbacks(this);
                                    } else {
                                        handler.postDelayed(this, 500);
                                    }
                                }
                            }, 500);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        setListSubjectInTermLiveData(listSubjectInTerm);
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
