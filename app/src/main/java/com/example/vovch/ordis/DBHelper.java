package com.example.vovch.ordis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public final static String DB_NAME = "order.db";
    final static String TABLE_NAME = "Orders";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +  "(" +
                "`_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`name` TEXT NOT NULL," +
                "`pronouncing` TEXT NOT NULL," +
                "`description` TEXT, " +
                "`location` TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}