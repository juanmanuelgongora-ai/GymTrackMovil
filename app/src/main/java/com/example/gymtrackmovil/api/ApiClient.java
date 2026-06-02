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
            String baseUrl = ip;

            if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {

                if (baseUrl.contains("10.0.2.2") || baseUrl.contains("192.168.") ||
                        baseUrl.contains("localhost") || baseUrl.contains(":8000")) {
                    baseUrl = "http://" + baseUrl;
                } else {

                    baseUrl = "https://" + baseUrl;
                }
            }

            if (!baseUrl.endsWith("/")) {
                baseUrl = baseUrl + "/";
            }

            if (!baseUrl.endsWith("api/")) {
                baseUrl = baseUrl + "api/";
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
