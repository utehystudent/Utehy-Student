package com.example.utehystudent.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.utehystudent.model.Account;
import com.example.utehystudent.model.User;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginViewModel extends AndroidViewModel {
    public static String TAG = "Login";

    Context context;

    private MutableLiveData<Account> accountLiveData;
    private MutableLiveData<User> userLiveData;
    public MutableLiveData<Boolean> isLoginSuccess;

    FirebaseFirestore db;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        accountLiveData = new MutableLiveData<>();
        userLiveData = new MutableLiveData<>();
        isLoginSuccess = new MutableLiveData<>();
        context = application.getApplicationContext();

        db = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<Account> getAccountLiveData() {
        return accountLiveData;
    }

    public MutableLiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void setAccountLiveData(Account account) {
        this.accountLiveData.setValue(account);
    }

    public void setUserLiveData(User user) {
        this.userLiveData.setValue(user);
    }

    public void LoginWithAccount(String username, String password) {
        db.collection("Account").document(username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            //nếu có document có id là username
                            if (document.exists()) {
                                Account account = new Account();
                                account = (Account) document.toObject(Account.class);
                                //check password xem có đúng không?
                                if (account.getPassword().equals(password)) {
                                    isLoginSuccess.setValue(true);
                                    accountLiveData.setValue(account);
                                    //lưu account vào bộ nhớ app
                                    SharedPreferences preferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("username", account.getUsername());
                                    editor.putString("password", account.getPassword());
                                    editor.putString("account_type", account.getAccount_type());
                                    editor.putString("device_token", account.getDevice_token());
                                    editor.commit();
                                    //
                                }else {
                                    Log.d(TAG, "Password not correct");
                                    isLoginSuccess.setValue(false);
                                    return;
                                }
                            }else {
                                Log.d(TAG, "Account not exist");
                                isLoginSuccess.setValue(false);
                                return;
                            }
                        } else {
                            Log.d(TAG, "Not successful");
                            isLoginSuccess.setValue(false);
                            return;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Login failed");
                        isLoginSuccess.setValue(false);
                        return;
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "Cancel connect to Firebase");
                        isLoginSuccess.setValue(false);
                        return;
                    }
                });
    }
}
