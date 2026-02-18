package com.zybooks.weighttrackingemmanuelrivera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeightTrackerDB extends SQLiteOpenHelper {

    private static final String WEIGHT_DATABASE = "weight_tracker.db";
    private static final int VERSION = 2;

    public WeightTrackerDB(Context context) {
        super(context, WEIGHT_DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "userId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL)");

        db.execSQL("CREATE TABLE weights (" +
                "weightId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "weight FLOAT, " +
                "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "userId INTEGER, " +
                "FOREIGN KEY(userId) REFERENCES users(userId) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE goals (" +
                "goalId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "goal FLOAT, " +
                "userId INTEGER, " +
                "FOREIGN KEY(userId) REFERENCES users(userId) ON DELETE CASCADE)");
    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS weights");
        db.execSQL("DROP TABLE IF EXISTS goals");
        onCreate(db);

    }

    public long createUser(String username, String passwordHash) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", passwordHash);

        return db.insert("users", null, values);
    }

    public boolean verifyUser(String username, String passwordHash) {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT userId FROM users WHERE username = ? AND password = ?",
                new String[]{username, passwordHash})) {
            return cursor.moveToFirst();
        }
    }

    public long getUserId(String username) {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery(
                "SELECT userId FROM users WHERE username = ?",
                new String[]{username})) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
            return -1;
        }
    }

    public long insertWeight(long userId, float weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("weight", weight);
        values.put("userId", userId);
        return db.insert("weights", null, values);
    }

    public long insertGoal(long userId, float goal) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goal", goal);
        values.put("userId", userId);
        return db.insert("goals", null, values);
    }
}
