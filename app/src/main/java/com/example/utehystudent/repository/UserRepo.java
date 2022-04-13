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
}
