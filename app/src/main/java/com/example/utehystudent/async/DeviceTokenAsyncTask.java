package com.example.utehystudent.async;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class DeviceTokenAsyncTask extends AsyncTask<Void, Void, Void> {
    FirebaseFirestore db;
    SharedPreferences pref;
    Activity activity;

    public DeviceTokenAsyncTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        db = FirebaseFirestore.getInstance();
        pref = activity.getSharedPreferences("Account", Context.MODE_PRIVATE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String recent_token = pref.getString("device_token", "");
        Log.d("qqq", "token: "+recent_token);
        String username = pref.getString("username", "");
        if (recent_token.equals("")) {
            return null;
        }else {
            db.collection("Account")
                    .whereNotEqualTo("username", username)
                    .whereEqualTo("device_token", recent_token)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            return;
                        }
                        if (value != null) {
                            value.forEach(it -> {
                                String docID = it.getId();
                                Log.d("qqq", "GET ID HAS SAME TOKEN: "+docID);
                                removeToken(docID);
                            });
                        }
                    });
        }

        return null;
    }

    private void removeToken(String docID) {
        db.collection("Account")
                .document(docID)
                .update("device_token", "");
    }
}
