package com.example.carrental.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://axmxnphikzowphndtfoz.supabase.co/";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImF4bXhucGhpa3pvd3BobmR0Zm96Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQ4MTA3NDIsImV4cCI6MjA5MDM4Njc0Mn0.gq9bmHM1eB5SWJiycYdj72CPrhBXEtM6nRx3uI3eqn0";
    private static Retrofit retrofit = null;

    public static SupabaseService getService() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                        .header("apikey", API_KEY)
                        .header("Authorization", "Bearer " + API_KEY)
                        .method(original.method(), original.body())
                        .build();
                    return chain.proceed(request);
                })
                .build();

            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        }
        return retrofit.create(SupabaseService.class);
    }
}