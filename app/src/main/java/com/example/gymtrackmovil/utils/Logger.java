package com.example.gymtrackmovil.utils;
import android.content.Context;
import android.util.Log;
public class Logger {
    private static final String TAG = "GymTrackLog";
    private static final String FILE_NAME = "gymtrack_logs.txt";
    private static Context context;
    public static void init(Context ctx) {
        context = ctx.getApplicationContext();
    }
    public static void i(String message) {
        Log.i(TAG, message);
    }
    public static void e(String message, Throwable throwable) {
        Log.e(TAG, message, throwable);
        saveToFile("ERROR: " + message + (throwable != null ? " - " + throwable.getMessage() : ""));
    }
    public static void d(String message) {
        Log.d(TAG, message);
    }
    private static void saveToFile(String text) {
        if (context == null)
            return;
        try {
            java.io.FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
            String entry = new java.util.Date().toString() + ": " + text + "\n";
            fos.write(entry.getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "Error saving log to file", e);
        }
    }
}


