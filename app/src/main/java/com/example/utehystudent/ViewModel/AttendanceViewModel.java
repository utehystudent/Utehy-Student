package com.example.utehystudent.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.utehystudent.model.StudentAttendance;
import com.example.utehystudent.model.Subject;
import com.example.utehystudent.model.SubjectsOfSemester_Detail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AttendanceViewModel extends AndroidViewModel {
    final String TAG = "AttendanceViewModel";
    Application application;
    FirebaseFirestore db;
    private MutableLiveData<ArrayList<Subject>> listSubjectInTermLiveData;
    private MutableLiveData<ArrayList<StudentAttendance>> listStudentAttendanceLiveData;
    ArrayList<Subject> listSubjectInTerm;

    public AttendanceViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        db = FirebaseFirestore.getInstance();
        listSubjectInTerm = new ArrayList<>();
        listSubjectInTermLiveData = new MutableLiveData<>();
        listStudentAttendanceLiveData = new MutableLiveData<>();
        //get data
        GetListSubjectInTerm();
    }

    private void GetListSubjectInTerm() {
        SharedPreferences pref = application.getSharedPreferences("User", Context.MODE_PRIVATE);
        String classID = pref.getString("class_ID", "");
        //
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

                            listSubjectInTerm = GetSubject(listDetail);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (listSubjectInTermLiveData.getValue() != null || listSubjectInTermLiveData.getValue().size() != listSubjectInTerm.size()) {
                                        listSubjectInTermLiveData.setValue(listSubjectInTerm);
                                        handler.removeCallbacks(this::run);
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
        listSubjectInTermLiveData.setValue(listSubjectInTerm);
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

    public Subject GetSubjectInfo(int position) {
        Subject sj = listSubjectInTermLiveData.getValue().get(position-1);
        return sj;
    }

    public MutableLiveData<ArrayList<Subject>> getListSubjectInTermLiveData() {
        return listSubjectInTermLiveData;
    }

    public MutableLiveData<ArrayList<StudentAttendance>> getListStudentAttendanceLiveData() {
        return listStudentAttendanceLiveData;
    }
}
