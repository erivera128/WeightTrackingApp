package com.zybooks.weighttrackingemmanuelrivera.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.zybooks.weighttrackingemmanuelrivera.WeightTrackerDB;
import com.zybooks.weighttrackingemmanuelrivera.model.User;

public class AuthViewModel extends AndroidViewModel {
    private final WeightTrackerDB dbHelper;

    private final MutableLiveData<User> authResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorState = new MutableLiveData<>();

    public AuthViewModel(Application application) {
        super(application);
        dbHelper = new WeightTrackerDB(application);
    }

    public LiveData<User> getAuthResult() { return authResult; }
    public LiveData<String> getErrorState() { return errorState; }

    public void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            errorState.setValue("Please enter a username and password");
            return;
        }

        if (dbHelper.verifyUser(username, password)) {
            long userId = dbHelper.getUserId(username);
            authResult.setValue(new User(userId, username));
        } else {
            errorState.setValue("Invalid username or password");
        }
    }

    public void handleSignUp(String username, String password) {
        if (username.isEmpty() || password.length() < 8) {
            errorState.setValue("Enter a username and password with at least 8 characters");
            return;
        }

        long userId = dbHelper.createUser(username, password);
        if (userId == -1) {
            errorState.setValue("Username already exists");
        } else {
            errorState.setValue("Account created. You can now log in.");
        }
    }

    @Override
    protected void onCleared() {
        dbHelper.close();
        super.onCleared();
    }
}