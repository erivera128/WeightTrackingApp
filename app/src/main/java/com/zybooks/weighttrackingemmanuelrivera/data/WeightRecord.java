package com.zybooks.weighttrackingemmanuelrivera.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weights")
public class WeightRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public float weight;
    public long timestamp;
    public long userId;

    public WeightRecord(float weight, long userId) {
        this.weight = weight;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
    }
}