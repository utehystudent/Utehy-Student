package com.example.utehystudent.async;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.utehystudent.model.User;
import com.example.utehystudent.repository.UserRepo;


public class DataAsync extends AsyncTask<Void, Void, Object> {
    Activity mActivity;
    UserRepo userRepo;


    public DataAsync(Activity activity) {
        this.mActivity = activity;
        userRepo = new UserRepo(activity.getApplication());
    }

    @Override
    protected Object doInBackground(Void... voids) {
        try {
            User user = new User();
            return user;
        }
        catch (Exception exc) {
            return exc;
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (mActivity.isFinishing()) {
            return;
        }
    }
}
