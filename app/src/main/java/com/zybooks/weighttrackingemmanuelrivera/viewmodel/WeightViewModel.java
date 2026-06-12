package com.zybooks.weighttrackingemmanuelrivera.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.zybooks.weighttrackingemmanuelrivera.WeightTrackerDB;
import java.util.List;

public class WeightViewModel extends AndroidViewModel {
    private final WeightTrackerDB dbHelper;

    private final MutableLiveData<Float> currentWeight = new MutableLiveData<>();
    private final MutableLiveData<Float> currentGoal = new MutableLiveData<>();
    private final MutableLiveData<List<WeightTrackerDB.WeightEntry>> recentWeights = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    public WeightViewModel(Application application) {
        super(application);
        dbHelper = new WeightTrackerDB(application);
    }

    public LiveData<Float> getCurrentWeight() { return currentWeight; }
    public LiveData<Float> getCurrentGoal() { return currentGoal; }
    public LiveData<List<WeightTrackerDB.WeightEntry>> getRecentWeights() { return recentWeights; }
    public LiveData<String> getStatusMessage() { return statusMessage; }

    public void loadDashboardData(long userId) {
        if (userId < 0) return;

        currentWeight.setValue(dbHelper.latestWeight(userId));
        currentGoal.setValue(dbHelper.getLatestGoal(userId));
        recentWeights.setValue(dbHelper.getRecentWeight(userId, 10));
    }

    public void addWeightOrGoal(long userId, boolean isWeight, float value) {
        if (userId < 0) {
            statusMessage.setValue("User not found");
            return;
        }

        long insertId = isWeight
                ? dbHelper.insertWeight(userId, value)
                : dbHelper.insertGoal(userId, value);

        if (insertId == -1) {
            statusMessage.setValue("Save failed");
        } else {
            statusMessage.setValue(isWeight ? "Weight saved" : "Goal saved");
            loadDashboardData(userId);
        }
    }

    @Override
    protected void onCleared() {
        dbHelper.close();
        super.onCleared();
    }
}