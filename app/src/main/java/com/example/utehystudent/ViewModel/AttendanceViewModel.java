package com.example.utehystudent.ViewModel;

import android.annotation.SuppressLint;
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
import com.example.utehystudent.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AttendanceViewModel extends AndroidViewModel {
    final String TAG = "AttendanceViewModel";
    Application application;
    FirebaseFirestore db;
    ArrayList<Subject> listSubjectInTerm;
    ArrayList<StudentAttendance> listStudentAttendance;
    private MutableLiveData<ArrayList<Subject>> listSubjectInTermLiveData;
    private MutableLiveData<ArrayList<StudentAttendance>> listStudentAttendanceLiveData;

    public AttendanceViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        db = FirebaseFirestore.getInstance();
        listSubjectInTerm = new ArrayList<>();
        listSubjectInTermLiveData = new MutableLiveData<>();
        listStudentAttendanceLiveData = new MutableLiveData<>();
        //get data
        GetListSubjectInTerm();
        GetListStudentAttendance();
    }

    public MutableLiveData<ArrayList<Subject>> getListSubjectInTermLiveData() {
        return listSubjectInTermLiveData;
    }

    public MutableLiveData<ArrayList<StudentAttendance>> getListStudentAttendanceLiveData() {
        return listStudentAttendanceLiveData;
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

                            listSubjectInTerm = GetSubject(listDetail);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (listSubjectInTermLiveData.getValue() != null || listSubjectInTermLiveData.getValue().size() != listSubjectInTerm.size()) {
                                        listSubjectInTermLiveData.setValue(listSubjectInTerm);
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

    private void GetListStudentAttendance() {
        listStudentAttendance = new ArrayList<>();
        SharedPreferences preferences = application.getSharedPreferences("User", Context.MODE_PRIVATE);
        String classID = preferences.getString("class_ID", "");
        db.collection("User")
                .whereEqualTo("class_ID", classID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            User user;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user = document.toObject(User.class);
                                listStudentAttendance.add(new StudentAttendance(user.getUsername(), user.getFaculty_ID(), user.getClass_ID(), user.getName(), user.getRegency(), user.getAvt_link(), true));
                                Log.d(TAG, "onComplete: "+document.toObject(User.class));
                            }
                            listStudentAttendanceLiveData.setValue(listStudentAttendance);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (listStudentAttendanceLiveData.getValue() != null || listStudentAttendanceLiveData.getValue().size() != listStudentAttendance.size()) {
                                        listStudentAttendanceLiveData.setValue(listStudentAttendance);
                                        handler.removeCallbacks(this);
                                    } else {
                                        handler.postDelayed(this, 500);
                                    }
                                }
                            }, 500);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });
    }

    public Subject GetSubjectInfo(int position) {
        Subject subject = Objects.requireNonNull(listSubjectInTermLiveData.getValue()).get(position - 1);
        return subject;
    }

    public String GetCurrentDate() {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public int GetCurrentTimeInDay() {
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        if (hour <= 12) {
            //Sáng
            return 1;
        } else if (hour <= 17) {
            //Chiều
            return 2;
        } else {
            //Tối
            return 3;
        }
    }
}
