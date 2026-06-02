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
            // Cliente OkHttp personalizado con interceptor de cabeceras de autorización y JSON (De Master)
            okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();
                        okhttp3.Request.Builder builder = original.newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Content-Type", "application/json");
                        com.example.gymtrackmovil.utils.SessionManager liveSession = new com.example.gymtrackmovil.utils.SessionManager(
                                context.getApplicationContext());
                        String token = liveSession.getUserToken();
                        if (token != null && !token.isEmpty()) {
                            String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
                            builder.header("Authorization", authHeader);
                        }
                        return chain.proceed(builder.build());
                    })
                    .build();
            // Retrofit configurado para usar el OkHttpClient con interceptores
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient) // Vinculado correctamente para inyectar los headers de autorización
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}