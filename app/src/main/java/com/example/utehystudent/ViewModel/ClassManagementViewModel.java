package com.example.utehystudent.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.utehystudent.model.User;
import com.example.utehystudent.repository.UserRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ClassManagementViewModel extends AndroidViewModel {
    final String TAG = "ClassManagementViewModel";
    Application application;
    MutableLiveData<ArrayList<User>> listUserLiveData = new MutableLiveData<>();
    UserRepo userRepo;
    ArrayList<User> usersList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ClassManagementViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        userRepo = new UserRepo(application);
        GetUsersInClass();
    }

    public MutableLiveData<ArrayList<User>> getListUserLiveData() {
        return listUserLiveData;
    }

    public void setListUserLiveData(ArrayList<User> listUser) {
        this.listUserLiveData.setValue(listUser);
        Log.d(TAG, "setListUserLiveData: " + listUser.size());
    }

    private void GetUsersInClass() {
        SharedPreferences preferences = application.getSharedPreferences("User", Context.MODE_PRIVATE);
        String classID = preferences.getString("class_ID", "");
        usersList.clear();
        ArrayList<User> listUserInClass = new ArrayList<>();
        db.collection("User")
                .whereEqualTo("class_ID", classID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                listUserInClass.add(document.toObject(User.class));
                                Log.d(TAG, "onComplete: "+document.toObject(User.class));
                            }
                            usersList = listUserInClass;
                            setListUserLiveData(usersList);
                            Log.d(TAG, "Size: "+listUserLiveData.getValue().size());
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });
    }
}
