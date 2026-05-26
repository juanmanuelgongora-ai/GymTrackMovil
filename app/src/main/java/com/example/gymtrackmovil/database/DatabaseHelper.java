package com.example.gymtrackmovil.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GymTrack.db";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    public static final String TABLE_ROUTINES = "routines";
    public static final String TABLE_LOGS = "app_logs";
    public static final String TABLE_USERS = "users";

    // Common columns
    public static final String KEY_ID = "id";

    // Routines Table columns
    public static final String KEY_ROUTINE_NAME = "name";
    public static final String KEY_ROUTINE_DESC = "description";

    // Logs Table columns
    public static final String KEY_LOG_TYPE = "type";
    public static final String KEY_LOG_MSG = "message";
    public static final String KEY_LOG_TIMESTAMP = "timestamp";

    // Create Table Statements
    private static final String CREATE_TABLE_ROUTINES = "CREATE TABLE " + TABLE_ROUTINES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_ROUTINE_NAME + " TEXT,"
            + KEY_ROUTINE_DESC + " TEXT" + ")";

    private static final String CREATE_TABLE_LOGS = "CREATE TABLE " + TABLE_LOGS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_LOG_TYPE + " TEXT,"
            + KEY_LOG_MSG + " TEXT,"
            + KEY_LOG_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";

    // Users Table columns
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_GOAL = "goal";
    public static final String KEY_USER_ADDRESS = "address";
    public static final String KEY_USER_AGE = "age";
    public static final String KEY_USER_EPS = "eps";
    public static final String KEY_USER_PHONE = "phone";
    public static final String KEY_USER_FAMILY_PHONE = "family_phone";
    public static final String KEY_USER_WEIGHT = "weight";
    public static final String KEY_USER_HEIGHT = "height";
    public static final String KEY_USER_SEX = "sex";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_NAME + " TEXT,"
            + KEY_USER_EMAIL + " TEXT,"
            + KEY_USER_GOAL + " TEXT,"
            + KEY_USER_ADDRESS + " TEXT,"
            + KEY_USER_AGE + " INTEGER,"
            + KEY_USER_EPS + " TEXT,"
            + KEY_USER_PHONE + " TEXT,"
            + KEY_USER_FAMILY_PHONE + " TEXT,"
            + KEY_USER_WEIGHT + " REAL,"
            + KEY_USER_HEIGHT + " REAL,"
            + KEY_USER_SEX + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ROUTINES);
        db.execSQL(CREATE_TABLE_LOGS);
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTINES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Helper method to add a user to DB
    public long saveUser(String name, String email, String goal, String address, int age,
            String eps, String phone, String familyPhone,
            double weight, double height, String sex) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, name);
        values.put(KEY_USER_EMAIL, email);
        values.put(KEY_USER_GOAL, goal);
        values.put(KEY_USER_ADDRESS, address);
        values.put(KEY_USER_AGE, age);
        values.put(KEY_USER_EPS, eps);
        values.put(KEY_USER_PHONE, phone);
        values.put(KEY_USER_FAMILY_PHONE, familyPhone);
        values.put(KEY_USER_WEIGHT, weight);
        values.put(KEY_USER_HEIGHT, height);
        values.put(KEY_USER_SEX, sex);
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    // Helper method to add a log entry to DB
    public void addLog(String type, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOG_TYPE, type);
        values.put(KEY_LOG_MSG, message);
        db.insert(TABLE_LOGS, null, values);
        db.close();
    }
}
