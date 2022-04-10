package com.example.utehystudent.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.utehystudent.helper.DateHelper;
import com.example.utehystudent.model.Schedule;
import com.example.utehystudent.model.Schedule_detail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ScheduleViewModel extends AndroidViewModel {
    private final String TAG = "ScheduleViewModel";

    private MutableLiveData<Schedule> scheduleMutableLiveData;
    private MutableLiveData<Schedule_detail> schedule_detailMutableLiveData;

    FirebaseFirestore db;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        scheduleMutableLiveData = new MutableLiveData<>();
        schedule_detailMutableLiveData = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();


        //get current class_ID
        SharedPreferences preferences = application.getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        String class_ID = preferences.getString("class_ID", "");
        Log.d("schedule", "class_ID: "+class_ID);

        //get current day string
        DateHelper dateHelper = new DateHelper();
        String day_string = dateHelper.getDayString();

        //get schedule from firebase
        getSchedule(class_ID, day_string);
    }

    //get schedule of current classroom
    private void getSchedule(String class_ID, String day_string) {

        //get schedule information
        db.collection("Schedule")
                .document(class_ID)
                .collection("Schedule_detail")
                .document(day_string)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                String morning = document.get("morning").toString();
                                String afternoon = document.get("afternoon").toString();
                                Schedule_detail detail = new Schedule_detail(morning, afternoon);

                                //set data for schedule_detail live data
                                setSchedule_detailMutableLiveData(detail);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    public void setScheduleMutableLiveData(Schedule schedule) {
        this.scheduleMutableLiveData.setValue(schedule);
    }

    public void setSchedule_detailMutableLiveData(Schedule_detail schedule_detail) {
        this.schedule_detailMutableLiveData.setValue(schedule_detail);
    }

    public MutableLiveData<Schedule> getScheduleMutableLiveData() {
        return scheduleMutableLiveData;
    }

    public MutableLiveData<Schedule_detail> getSchedule_detailMutableLiveData() {
        return schedule_detailMutableLiveData;
    }
}
