package com.example.utehystudent.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.utehystudent.helper.DateHelper;
import com.example.utehystudent.model.Schedule_detail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ScheduleViewModel extends AndroidViewModel {
    final String TAG = this.getClass().getName();
    MutableLiveData<Schedule_detail> scheduleLiveData = new MutableLiveData<>();
    Schedule_detail scheduleDetail;
    Context context;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
        scheduleLiveData = new MutableLiveData<>();
        scheduleDetail = new Schedule_detail();
        this.context = application.getApplicationContext();
        GetData();
    }

    public MutableLiveData<Schedule_detail> getScheduleLiveData() {
        return scheduleLiveData;
    }

    public void GetData() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scheduleDetail.getMorning() == null) {
                    SharedPreferences preferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
                    String classID = preferences.getString("class_ID", "");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String current_day = DateHelper.getDayString();
                    //get schedule from fire store
                    db.collection("Schedule")
                            .document(classID)
                            .collection("Schedule_detail")
                            .document(current_day)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        String morning = "";
                                        String afternoon = "";
                                        String evening = "";
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            morning = document.getString("morning");
                                            afternoon = document.getString("afternoon");
                                            evening = document.getString("evening");
                                            /*scheduleDetail.setMorning(morning);
                                            scheduleDetail.setAfternoon(afternoon);
                                            scheduleDetail.setSubject_ID(subject_ID);
                                            scheduleLiveData.setValue(scheduleDetail);*/
                                        } else {
                                            morning = "N/A";
                                            afternoon = "N/A";
                                            evening = "N/A";
                                        }
                                        scheduleDetail.setMorning(morning);
                                        scheduleDetail.setAfternoon(afternoon);
                                        scheduleDetail.setEvening(evening);
                                        scheduleLiveData.setValue(scheduleDetail);

                                        SharedPreferences prefSchedule = context.getSharedPreferences("Schedule", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefSchedule.edit();
                                        editor.putString("morning", morning);
                                        editor.putString("afternoon", afternoon);
                                        editor.putString("evening", evening);
                                        editor.commit();
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                    handler.removeCallbacks(this::run);
                } else {
                    handler.postDelayed(this, 500);
                }
            }
        }, 500);
    }
}
