package com.example.gymtrackmovil.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "GymTrackPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_NAME = "userName";
    private static final String KEY_SERVER_IP = "serverIp";
    private static final String DEFAULT_IP = "10.0.2.2:8000"; // Android Emulator default to host loopback

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String email, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public void updateServerIp(String ip) {
        editor.putString(KEY_SERVER_IP, ip);
        editor.commit();
    }

    public String getServerIp() {
        return pref.getString(KEY_SERVER_IP, DEFAULT_IP);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}
