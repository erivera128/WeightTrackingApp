package com.zybooks.weighttrackingemmanuelrivera.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface WeightDao {
    @Insert
    void insertWeight(WeightRecord record);

    @Query("SELECT * FROM weights WHERE userId = :userId ORDER BY timestamp DESC LIMIT 10")
    List<WeightRecord> getRecentWeights(long userId);

    @Query("SELECT weight FROM weights WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    float getLatestWeight(long userId);
}