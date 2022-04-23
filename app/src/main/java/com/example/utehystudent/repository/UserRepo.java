package com.example.utehystudent.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.utehystudent.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserRepo {
    public static String classID = "";
    final String TAG = "UserRepo";
    FirebaseFirestore db;
    Application application;

    public UserRepo(Application application) {
        this.db = FirebaseFirestore.getInstance();
        this.application = application;
    }

    public User GetUserFromFirestore (String username) {
        final User[] user = {new User()};
        db.collection("User")
                .document(username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                user[0] = document.toObject(User.class);
                                SaveUserToSF(user[0]);
                            }else {
                                Log.d(TAG, "onComplete: USER NOT EXISTED");
                            }
                        }else {
                            Log.d(TAG, "onComplete: FAIL");
                        }
                    }
                });

        return user[0];
    }

    public void SaveUserToSF (User user) {
        SharedPreferences preferences = application.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username",user.getUsername());
        editor.putString("name",user.getName());
        editor.putString("class_ID",user.getClass_ID());
        editor.putString("faculty_ID",user.getFaculty_ID());
        editor.putString("regency",user.getRegency());
        editor.putString("avt_link",user.getAvt_link());
        editor.commit();
        Log.d(TAG, "SaveUserToSF: "+user);
    }

    public User GetUserFromSF() {
        SharedPreferences preferences = application.getSharedPreferences("User", Context.MODE_PRIVATE);

        User user = new User();

        user.setUsername(preferences.getString("username", ""));
        user.setFaculty_ID(preferences.getString("faculty_ID", ""));
        user.setClass_ID(preferences.getString("class_ID", ""));
        user.setName(preferences.getString("name", ""));
        user.setRegency(preferences.getString("regency", ""));
        user.setAvt_link(preferences.getString("avt_link", "data:image/png;base64,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"));

        return user;
    }

    public ArrayList<User> GetUserByClassID (String classID) {
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
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            return;
                        }
                    }
                });
        Log.d(TAG, "GetUserByClassID: "+listUserInClass.size());
        return listUserInClass;
    }
}
