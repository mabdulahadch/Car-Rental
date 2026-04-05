package com.example.carrental.api;

import com.example.carrental.models.Car;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CarApiService {
    @GET("cars/")
    Call<List<Car>> getCars();

    @GET("cars/{car_id}")
    Call<Car> getCarDetail(@Path("car_id") String carId);

    @POST("auth/login")
    Call<com.example.carrental.models.User> login(@Body com.example.carrental.models.User user);

    @POST("auth/register")
    Call<com.example.carrental.models.User> register(@Body java.util.Map<String, String> userDetails);

    @POST("showrooms/")
    Call<com.example.carrental.models.Showroom> createShowroom(@Body com.example.carrental.models.ShowroomCreateRequest request);

    @POST("favorites/")
    Call<java.util.Map<String, String>> addFavorite(@Body java.util.Map<String, String> request);

    @DELETE("favorites/{user_id}/{car_id}")
    Call<Void> removeFavorite(@Path("user_id") String userId, @Path("car_id") String carId);

    @GET("favorites/{user_id}")
    Call<java.util.List<String>> getUserFavorites(@Path("user_id") String userId);
}