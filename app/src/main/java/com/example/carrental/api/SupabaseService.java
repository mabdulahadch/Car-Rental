package com.example.carrental.api;

import com.example.carrental.models.Car;
import com.example.carrental.models.Review;
import com.example.carrental.models.Showroom;
import com.example.carrental.models.Booking;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseService {

    @GET("rest/v1/cars?select=*")
    Call<List<Car>> getCars(@Query("brand") String brand, @Query("color") String color);

    @GET("rest/v1/showrooms?select=*")
    Call<List<Showroom>> getShowrooms();

    @GET("rest/v1/reviews?select=*")
    Call<List<Review>> getReviews(@Query("car_id") String carId);

    @POST("rest/v1/bookings")
    Call<Void> createBooking(@Body Booking booking);
}