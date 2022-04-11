package com.example.utehystudent.repository;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.utehystudent.helper.DateHelper;
import com.example.utehystudent.model.Schedule_detail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ScheduleRepo {
    private final String TAG = "ScheduleRepo";
    FirebaseFirestore db;
    Activity activity;

    public ScheduleRepo(Activity activity) {
        this.db = FirebaseFirestore.getInstance();
        this.activity = activity;
    }

    //get schedule of current classroom
    public Schedule_detail getSchedule(String class_ID) {
        final Schedule_detail[] scheduleDetail = {new Schedule_detail()};
        //get current day string
        DateHelper dateHelper = new DateHelper();
        String day_string = dateHelper.getDayString();
        //get schedule information
        db.collection("Schedule")
                .document(""+class_ID)
                .collection("Schedule_detail")
                .document(day_string)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String morning = document.get("morning").toString();
                                String afternoon = document.get("afternoon").toString();
                                Schedule_detail detail = new Schedule_detail(morning, afternoon);
                                scheduleDetail[0] = detail;
                            } else {
                                Log.d(TAG, "No such document");
                                return;
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            return;
                        }
                    }
                });
        return scheduleDetail[0];
    }

}
