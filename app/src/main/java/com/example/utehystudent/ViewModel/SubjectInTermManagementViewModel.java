package com.example.utehystudent.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.utehystudent.model.Subject;
import com.example.utehystudent.model.SubjectsOfSemester;
import com.example.utehystudent.model.SubjectsOfSemester_Detail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SubjectInTermManagementViewModel extends AndroidViewModel {
    final String TAG = "SubjectManagementViewModel";
    String classID = "";
    Application application;
    public MutableLiveData<ArrayList<Subject>> listSubjectInTermLiveData = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Subject>> listAllSubjectLiveData = new MutableLiveData<>();
    MutableLiveData<SubjectsOfSemester> subjectOfSemesterLiveData = new MutableLiveData<>();
    ArrayList<Subject> listSubjectInTerm, listAllSubject;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public SubjectInTermManagementViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        listSubjectInTerm = new ArrayList<>();
        listAllSubject = new ArrayList<>();
        GetSemesterInfo();
        GetSubjectDetailList();
        GetAllSubjects();
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


    public MutableLiveData<SubjectsOfSemester> getSubjectOfSemesterLiveData() {
        return subjectOfSemesterLiveData;
    }

    public MutableLiveData<ArrayList<Subject>> getListAllSubjectLiveData() {
        return listAllSubjectLiveData;
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

    private void GetAllSubjects() {
        db.collection("Subject")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listAllSubject.add(document.toObject(Subject.class));
                            }
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (listAllSubjectLiveData.getValue() != null) {
                                        listAllSubjectLiveData.setValue(listAllSubject);
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
        listAllSubjectLiveData.setValue(listAllSubject);
    }

    public Boolean CheckSubjectExistedInTerm(String subjectName, ArrayList<Subject> list) {
        for (Subject sb : list) {
            if (sb.getSubject_name().equals(subjectName)) {
                return true;
            }
        }
        return false;
    }

    public Boolean CheckSubjectExistInListAll(String subjectName) {
        ArrayList<Subject> list = listAllSubjectLiveData.getValue();
        for (Subject sb : list) {
            if (sb.getSubject_name().equals(subjectName)) {
                return true;
            }
        }
        return false;
    }

    public Subject GetSubjectInfo(String subjectName) {
        Subject subject = new Subject();
        ArrayList<Subject> list = listAllSubjectLiveData.getValue();
        for (Subject sb : list) {
            if (sb.getSubject_name().equalsIgnoreCase(subjectName)) {
                subject = sb;
            }
        }
        return subject;
    }

    public void AddSubjectToTerm(Subject subject) {
        SubjectsOfSemester_Detail detail = new SubjectsOfSemester_Detail(classID, subject.getSubject_ID());
        db.collection("SubjectsOfSemester_Detail")
                .add(detail)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        GetSubjectDetailList();
                    }
                });
    }

    public void DeleteSubjectFromTerm(String subject_ID) {
        CollectionReference itemsRef = db.collection("SubjectsOfSemester_Detail");
        Query query = itemsRef.whereEqualTo("class_ID", classID).whereEqualTo("subject_ID", subject_ID);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        itemsRef.document(document.getId()).delete();
                    }
                    GetSubjectDetailList();
                    Toast.makeText(application.getApplicationContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

}
