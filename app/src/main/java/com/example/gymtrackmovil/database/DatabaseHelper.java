package com.example.gymtrackmovil.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GymTrack.db";
    private static final int DATABASE_VERSION = 4;
    public static final String TABLE_ROUTINES = "routines";
    public static final String TABLE_LOGS = "app_logs";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_METRICS = "metrics";
    public static final String TABLE_GOALS = "goals";
    public static final String KEY_ID = "id";
    public static final String KEY_ROUTINE_NAME = "name";
    public static final String KEY_ROUTINE_DESC = "description";
    public static final String KEY_LOG_TYPE = "type";
    public static final String KEY_LOG_MSG = "message";
    public static final String KEY_LOG_TIMESTAMP = "timestamp";
    private static final String CREATE_TABLE_ROUTINES = "CREATE TABLE " + TABLE_ROUTINES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_ROUTINE_NAME + " TEXT,"
            + KEY_ROUTINE_DESC + " TEXT" + ")";
    private static final String CREATE_TABLE_LOGS = "CREATE TABLE " + TABLE_LOGS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_LOG_TYPE + " TEXT,"
            + KEY_LOG_MSG + " TEXT,"
            + KEY_LOG_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_PASSWORD = "password";
    public static final String KEY_USER_GOAL = "goal";
    public static final String KEY_USER_ADDRESS = "address";
    public static final String KEY_USER_AGE = "age";
    public static final String KEY_USER_EPS = "eps";
    public static final String KEY_USER_PHONE = "phone";
    public static final String KEY_USER_FAMILY_PHONE = "family_phone";
    public static final String KEY_USER_WEIGHT = "weight";
    public static final String KEY_USER_HEIGHT = "height";
    public static final String KEY_USER_SEX = "sex";
    public static final String KEY_USER_ROLE = "role";
    public static final String KEY_USER_TRAINER_EMAIL = "trainer_email";
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_NAME + " TEXT,"
            + KEY_USER_EMAIL + " TEXT UNIQUE,"
            + KEY_USER_PASSWORD + " TEXT,"
            + KEY_USER_GOAL + " TEXT,"
            + KEY_USER_ADDRESS + " TEXT,"
            + KEY_USER_AGE + " INTEGER,"
            + KEY_USER_EPS + " TEXT,"
            + KEY_USER_PHONE + " TEXT,"
            + KEY_USER_FAMILY_PHONE + " TEXT,"
            + KEY_USER_WEIGHT + " REAL,"
            + KEY_USER_HEIGHT + " REAL,"
            + KEY_USER_SEX + " TEXT,"
            + KEY_USER_ROLE + " TEXT DEFAULT 'cliente',"
            + KEY_USER_TRAINER_EMAIL + " TEXT" + ")";
    public static final String KEY_METRIC_EMAIL = "user_email";
    public static final String KEY_METRIC_WEIGHT = "weight";
    public static final String KEY_METRIC_BODY_FAT = "body_fat";
    public static final String KEY_METRIC_MUSCLE_MASS = "muscle_mass";
    public static final String KEY_METRIC_DATE = "date";
    private static final String CREATE_TABLE_METRICS = "CREATE TABLE " + TABLE_METRICS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_METRIC_EMAIL + " TEXT,"
            + KEY_METRIC_WEIGHT + " REAL,"
            + KEY_METRIC_BODY_FAT + " REAL,"
            + KEY_METRIC_MUSCLE_MASS + " REAL,"
            + KEY_METRIC_DATE + " TEXT" + ")";
    public static final String KEY_GOAL_EMAIL = "user_email";
    public static final String KEY_GOAL_TITLE = "title";
    public static final String KEY_GOAL_TARGET = "target";
    public static final String KEY_GOAL_PROGRESS = "progress";
    public static final String KEY_GOAL_DEADLINE = "deadline";
    private static final String CREATE_TABLE_GOALS = "CREATE TABLE " + TABLE_GOALS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_GOAL_EMAIL + " TEXT,"
            + KEY_GOAL_TITLE + " TEXT,"
            + KEY_GOAL_TARGET + " TEXT,"
            + KEY_GOAL_PROGRESS + " INTEGER,"
            + KEY_GOAL_DEADLINE + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ROUTINES);
        db.execSQL(CREATE_TABLE_LOGS);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_METRICS);
        db.execSQL(CREATE_TABLE_GOALS);

        // Administrador
        db.execSQL("INSERT INTO " + TABLE_USERS + " (" +
                KEY_USER_NAME + ", " + KEY_USER_EMAIL + ", " + KEY_USER_PASSWORD + ", " + KEY_USER_ROLE +
                ") VALUES ('Administrador', 'admin@gymtrack.com', 'admin123', 'admin')");

        // Entrenador
        db.execSQL("INSERT INTO " + TABLE_USERS + " (" +
                KEY_USER_NAME + ", " + KEY_USER_EMAIL + ", " + KEY_USER_PASSWORD + ", " + KEY_USER_ROLE +
                ") VALUES ('Entrenador', 'entrenador@gymtrack.com', 'entrenador123', 'entrenador')");

        // Cliente de prueba
        db.execSQL("INSERT INTO " + TABLE_USERS + " (" +
                KEY_USER_NAME + ", " + KEY_USER_EMAIL + ", " + KEY_USER_PASSWORD + ", " + KEY_USER_ROLE +
                ") VALUES ('Cliente', 'cliente@gymtrack.com', 'cliente123', 'cliente')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + KEY_USER_ROLE + " TEXT DEFAULT 'cliente'");
            } catch (Exception e) {
            }
            try {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + KEY_USER_TRAINER_EMAIL + " TEXT");
            } catch (Exception e) {
            }
        }
    }

    public long saveUser(String name, String email, String password, String goal, String address, int age,
            String eps, String phone, String familyPhone,
            double weight, double height, String sex) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, name);
        values.put(KEY_USER_EMAIL, email);
        values.put(KEY_USER_PASSWORD, password);
        values.put(KEY_USER_GOAL, goal);
        values.put(KEY_USER_ADDRESS, address);
        values.put(KEY_USER_AGE, age);
        values.put(KEY_USER_EPS, eps);
        values.put(KEY_USER_PHONE, phone);
        values.put(KEY_USER_FAMILY_PHONE, familyPhone);
        values.put(KEY_USER_WEIGHT, weight);
        values.put(KEY_USER_HEIGHT, height);
        values.put(KEY_USER_SEX, sex);
        long id = db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return id;
    }

    public boolean checkUserCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.query(TABLE_USERS,
                new String[] { KEY_ID },
                KEY_USER_EMAIL + "=? AND " + KEY_USER_PASSWORD + "=?",
                new String[] { email, password },
                null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null)
            cursor.close();
        db.close();
        return exists;
    }

    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.query(TABLE_USERS,
                new String[] { KEY_ID },
                KEY_USER_EMAIL + "=?",
                new String[] { email },
                null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null)
            cursor.close();
        db.close();
        return exists;
    }

    public android.database.Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS,
                null,
                KEY_USER_EMAIL + "=?",
                new String[] { email },
                null, null, null);
    }

    public int updateUserProfile(String email, String name, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, name);
        values.put(KEY_USER_ADDRESS, address);
        values.put(KEY_USER_PHONE, phone);
        int rows = db.update(TABLE_USERS, values, KEY_USER_EMAIL + "=?", new String[] { email });
        db.close();
        return rows;
    }

    public void addLog(String type, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOG_TYPE, type);
        values.put(KEY_LOG_MSG, message);
        db.insert(TABLE_LOGS, null, values);
        db.close();
    }

    public long saveMetric(String userEmail, double weight, double bodyFat, double muscleMass, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_METRIC_EMAIL, userEmail);
        values.put(KEY_METRIC_WEIGHT, weight);
        values.put(KEY_METRIC_BODY_FAT, bodyFat);
        values.put(KEY_METRIC_MUSCLE_MASS, muscleMass);
        values.put(KEY_METRIC_DATE, date);
        long id = db.insert(TABLE_METRICS, null, values);
        db.close();
        return id;
    }

    public android.database.Cursor getUserMetrics(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_METRICS,
                null,
                KEY_METRIC_EMAIL + "=?",
                new String[] { userEmail },
                null, null, KEY_ID + " DESC");
    }

    public long saveGoal(String userEmail, String title, String target, int progress, String deadline) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_GOAL_EMAIL, userEmail);
        values.put(KEY_GOAL_TITLE, title);
        values.put(KEY_GOAL_TARGET, target);
        values.put(KEY_GOAL_PROGRESS, progress);
        values.put(KEY_GOAL_DEADLINE, deadline);
        long id = db.insert(TABLE_GOALS, null, values);
        db.close();
        return id;
    }

    public android.database.Cursor getUserGoals(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_GOALS,
                null,
                KEY_GOAL_EMAIL + "=?",
                new String[] { userEmail },
                null, null, KEY_ID + " ASC");
    }

    public long saveUserWithRole(String name, String email, String password, String goal,
            String address, int age, String eps, String phone, String familyPhone,
            double weight, double height, String sex, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, name);
        values.put(KEY_USER_EMAIL, email);
        values.put(KEY_USER_PASSWORD, password);
        values.put(KEY_USER_GOAL, goal != null ? goal : "");
        values.put(KEY_USER_ADDRESS, address != null ? address : "");
        values.put(KEY_USER_AGE, age);
        values.put(KEY_USER_EPS, eps != null ? eps : "");
        values.put(KEY_USER_PHONE, phone != null ? phone : "");
        values.put(KEY_USER_FAMILY_PHONE, familyPhone != null ? familyPhone : "");
        values.put(KEY_USER_WEIGHT, weight);
        values.put(KEY_USER_HEIGHT, height);
        values.put(KEY_USER_SEX, sex != null ? sex : "");
        values.put(KEY_USER_ROLE, role != null ? role : "cliente");
        long id = db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return id;
    }

    public android.database.Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, null, null, null, null, KEY_ID + " ASC");
    }

    public android.database.Cursor getUsersByRole(String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null,
                KEY_USER_ROLE + "=?", new String[] { role },
                null, null, KEY_USER_NAME + " ASC");
    }

    public android.database.Cursor getClientsByTrainer(String trainerEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null,
                KEY_USER_TRAINER_EMAIL + "=?", new String[] { trainerEmail },
                null, null, KEY_USER_NAME + " ASC");
    }

    public int updateUserRole(String email, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ROLE, role);
        int rows = db.update(TABLE_USERS, values, KEY_USER_EMAIL + "=?", new String[] { email });
        db.close();
        return rows;
    }

    public int assignTrainer(String clientEmail, String trainerEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_TRAINER_EMAIL, trainerEmail);
        int rows = db.update(TABLE_USERS, values, KEY_USER_EMAIL + "=?", new String[] { clientEmail });
        db.close();
        return rows;
    }

    public int countUsersByRole(String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_USERS + " WHERE " + KEY_USER_ROLE + "=?",
                new String[] { role });
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst())
                count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    public int countAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst())
                count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }
}
