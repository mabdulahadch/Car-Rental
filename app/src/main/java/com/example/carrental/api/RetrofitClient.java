package com.example.carrental.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Replace 192.168.x.x with your computer's local IP address (e.g., 192.168.1.10)
    private static final String BASE_URL = "http://192.168.0.108:8000/"; // 1st floor error
//    private static final String BASE_URL = "http://192.168.10.6:8000/"; // ground floor
    private static Retrofit retrofit = null;

    public static CarApiService getCarApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(CarApiService.class);
    }
}