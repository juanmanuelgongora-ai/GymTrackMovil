package com.example.gymtrackmovil.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static String currentIp = "";

    public static Retrofit getClient(android.content.Context context) {
        com.example.gymtrackmovil.utils.SessionManager session = new com.example.gymtrackmovil.utils.SessionManager(
                context);
        String ip = session.getServerIp();

        if (retrofit == null || !currentIp.equals(ip)) {
            currentIp = ip;
            String baseUrl = "http://" + ip + "/api/";
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
