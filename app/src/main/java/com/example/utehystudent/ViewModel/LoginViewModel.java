package com.example.utehystudent.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.utehystudent.model.Account;
import com.example.utehystudent.repository.AccountRepo;
import com.example.utehystudent.repository.UserRepo;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginViewModel extends AndroidViewModel {
    final String TAG = "LoginViewModel";
    AccountRepo accountRepo;
    UserRepo userRepo;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<Account> accountLiveData;
    private MutableLiveData<Boolean> isSuccess;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        accountRepo = new AccountRepo(application);
        userRepo = new UserRepo(application);
        accountLiveData = new MutableLiveData<>();
        isSuccess = new MutableLiveData<>();
    }

    public MutableLiveData<Account> getAccountLiveData() {
        if (accountLiveData == null) {
            accountLiveData = new MutableLiveData<>();
        }
        return accountLiveData;
    }

    public MutableLiveData<Boolean> getIsSuccess() {
        if (isSuccess == null) {
            isSuccess = new MutableLiveData<>();
        }
        return isSuccess;
    }

    //LOGIN METHOD WITH username, password
    public void SignInAccount (String username, String password) {
        db.collection("Account")
                .document(username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Account account = document.toObject(Account.class);
                                Log.d(TAG, "" + account.toString());
                                //check password
                                if (password.equals(account.getPassword())) {
                                    Log.d(TAG, "Login success: " + account.getUsername() + "- " + account.getPassword());
                                    isSuccess.setValue(true);
                                    accountLiveData.setValue(account);
                                    //SAVE DATA TO SHARED PREFERENCES
                                    accountRepo.SaveAccountToSF(account);
                                } else {
                                    Log.d(TAG, "Login failed");
                                    isSuccess.setValue(false);
                                }
                            } else {
                                Log.d(TAG, "account not exist");
                            }
                        } else {
                            Log.d(TAG, "Cached get failed: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "cancel");
                    }
                });
    }
}
