package com.example.utehystudent;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.utehystudent.ViewModel.LoginViewModel;
import com.example.utehystudent.model.User;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import me.pushy.sdk.Pushy;

public class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, Object> {
    String username;
    Activity mActivity;
    FirebaseFirestore db;
    LoginViewModel loginViewModel;

    public RegisterForPushNotificationsAsync(Activity activity) {
        this.mActivity = activity;
        username = "";
        loginViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(LoginViewModel.class);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        db  = FirebaseFirestore.getInstance();


        // Start Pushy notification service if not already running
        Pushy.listen(activity);


        // Enable FCM Fallback Delivery
        Pushy.toggleFCM(true, activity);

    }

    protected Object doInBackground(Void... params) {
        try {
            // Register the device for notifications
            String deviceToken = Pushy.register(mActivity);

            // Registration succeeded, log token to logcat
            Log.d("Pushy", "Pushy device token: " + deviceToken);
            // Provide token to onPostExecute()
            return deviceToken;
        }
        catch (Exception exc) {
            // Registration failed, provide exception to onPostExecute()
            return exc;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        if (mActivity.isFinishing()) {
            return;
        }
        String message;
        saveDeviceToken(result.toString());
        getUserFromFirestore(username);
        // Registration failed?
        if (result instanceof Exception) {
            // Log to console
            Log.e("Pushy", result.toString());
            // Display error in alert
            message = ((Exception) result).getMessage();
        }
        else {
            message = "Pushy device token: " + result.toString();
        }
        Log.d("get token", ""+message);
    }

    private void getUserFromFirestore(String username) {
        db.collection("User").document(username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User user = new User();
                                user = (User) document.toObject(User.class);
                                //set value to LoginViewModel
                                loginViewModel.setUserLiveData(user);

                                //save user to shared preferences
                                SharedPreferences preferences = mActivity.getSharedPreferences("User", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("username", user.getUsername());
                                editor.putString("class_ID", user.getClass_ID());
                                editor.putString("name", user.getName());
                                editor.putString("faculty_ID", user.getFaculty_ID());
                                editor.putString("regency", user.getRegency());
                                editor.putString("avt_link", user.getAvt_link());
                                editor.commit();

                                Log.d("save user live data", "success: "+user.getClass_ID());
                            }else {
                                Log.d("save user live data", "User doesn't exist");
                                return;
                            }
                        }else {
                            Log.d("save user live data", "fail");
                            return;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("save user live data", "failure");
                        return;
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d("save user live data", "cancel");
                        return;
                    }
                });
    }

    private void saveDeviceToken(String deviceToken) {
        //save device token to Shared Preferences
        SharedPreferences preferences = mActivity.getApplicationContext().getSharedPreferences("Account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("device_token", deviceToken);
        editor.commit();

        username = preferences.getString("username", "");

        //save device_token to Firestore user
        db.collection("Account").document(username)
                .update("device_token", deviceToken)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("update token", "update token success");
                        }else {
                            Log.d("update token", "update token failed");
                        }
                    }
                });
    }
}