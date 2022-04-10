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
    public MutableLiveData<Schedule> scheduleMutableLiveData;
    public MutableLiveData<Schedule_detail> schedule_detailMutableLiveData;

    FirebaseFirestore db;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        scheduleMutableLiveData = new MutableLiveData<>();
        schedule_detailMutableLiveData = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();

        //get schedule from firebase
        getSchedule();
    }

    //get schedule of current classroom
    public void getSchedule() {
        //get current class_ID
        SharedPreferences preferences = getApplication().getSharedPreferences("User", Context.MODE_PRIVATE);
        String class_ID = preferences.getString("class_ID", "");
        Log.d("schedule", "class_ID: "+class_ID);
        //get current day string
        DateHelper dateHelper = new DateHelper();
        String day_string = dateHelper.getDayString();

        //get schedule information
        db.collection("Schedule")
                .document("101185")
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
