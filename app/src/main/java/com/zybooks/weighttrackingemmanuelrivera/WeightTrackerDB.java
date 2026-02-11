package com.zybooks.weighttrackingemmanuelrivera;

import android.content.Context;
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
}
