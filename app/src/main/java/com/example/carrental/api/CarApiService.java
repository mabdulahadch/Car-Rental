package com.example.carrental.api;

import com.example.carrental.models.Car;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CarApiService {
    @GET("cars/")
    Call<List<Car>> getCars();
}