package com.example.carrental.api;

import com.example.carrental.models.Car;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CarApiService {
    @GET("cars/")
    Call<List<Car>> getCars();

    @GET("cars/{car_id}")
    Call<Car> getCarDetail(@Path("car_id") String carId);
}