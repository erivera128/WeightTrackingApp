package com.zybooks.weighttrackingemmanuelrivera.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.zybooks.weighttrackingemmanuelrivera.data.AppDatabase;
import com.zybooks.weighttrackingemmanuelrivera.data.WeightDao;
import com.zybooks.weighttrackingemmanuelrivera.data.WeightRecord;
import com.zybooks.weighttrackingemmanuelrivera.WeightTrackerDB;
import java.util.ArrayList;
import java.util.List;

public class WeightViewModel extends AndroidViewModel {
    private final WeightDao weightDao;
    private final WeightTrackerDB legacyDbHelper;
    private final MutableLiveData<Float> currentWeight = new MutableLiveData<>();
    private final MutableLiveData<Float> currentGoal = new MutableLiveData<>();
    private final MutableLiveData<List<WeightTrackerDB.WeightEntry>> recentWeights = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    public WeightViewModel(Application application) {
        super(application);
        weightDao = AppDatabase.getDatabase(application).weightDao();
        legacyDbHelper = new WeightTrackerDB(application);
    }

    public LiveData<Float> getCurrentWeight() { return currentWeight; }
    public LiveData<Float> getCurrentGoal() { return currentGoal; }
    public LiveData<List<WeightTrackerDB.WeightEntry>> getRecentWeights() { return recentWeights; }
    public LiveData<String> getStatusMessage() { return statusMessage; }

    public void loadDashboardData(long userId) {
        if (userId < 0) return;

        try {
            float latest = weightDao.getLatestWeight(userId);
            currentWeight.setValue(latest);
        } catch (Exception e) {
            currentWeight.setValue(null);
        }


        currentGoal.setValue(legacyDbHelper.getLatestGoal(userId));

        List<WeightRecord> roomRecords = weightDao.getRecentWeights(userId);
        List<WeightTrackerDB.WeightEntry> mappedEntries = new ArrayList<>();

        for (WeightRecord record : roomRecords) {
            java.util.Date date = new java.util.Date(record.timestamp);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.US);

            mappedEntries.add(new WeightTrackerDB.WeightEntry(
                    record.weight,
                    sdf.format(date)
            ));
        }
        recentWeights.setValue(mappedEntries);
    }

    public void addWeightOrGoal(long userId, boolean isWeight, float value) {
        if (userId < 0) {
            statusMessage.setValue("User not found");
            return;
        }

        if (isWeight) {
            weightDao.insertWeight(new WeightRecord(value, userId));
            statusMessage.setValue("Weight saved to Room DB");
        } else {
            long insertId = legacyDbHelper.insertGoal(userId, value);
            if (insertId == -1) {
                statusMessage.setValue("Save failed");
            } else {
                statusMessage.setValue("Goal saved");
            }
        }
        loadDashboardData(userId);
    }

    @Override
    protected void onCleared() {
        legacyDbHelper.close();
        super.onCleared();
    }
}