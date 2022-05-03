package com.example.utehystudent.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.utehystudent.model.Subject;
import com.example.utehystudent.model.SubjectsOfSemester_Detail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SubjectRepo {
    final String TAG = "SubjectRepo";
    FirebaseFirestore db;

    public SubjectRepo() {
        db = FirebaseFirestore.getInstance();
    }

    public ArrayList<Subject> getSubjectList (ArrayList<SubjectsOfSemester_Detail> listDetail) {
        ArrayList<Subject> listSubject = new ArrayList<>();
        for (SubjectsOfSemester_Detail detail : listDetail) {
            db.collection("Subject")
                    .whereEqualTo("subject_ID", detail.getSubject_ID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Subject subject = document.toObject(Subject.class);
                                    listSubject.add(subject);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        return listSubject;
    }
}
