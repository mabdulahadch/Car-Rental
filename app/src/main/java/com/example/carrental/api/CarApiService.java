package com.example.carrental.api;

import com.example.carrental.models.Booking;
import com.example.carrental.models.BookingRequest;
import com.example.carrental.models.Car;
import com.example.carrental.models.Review;
import com.example.carrental.models.ReviewRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CarApiService {

    // ─── Cars ────────────────────────────────────────────────────
    @GET("cars/")
    Call<List<Car>> getCars();

    @GET("cars/{car_id}")
    Call<Car> getCarDetail(@Path("car_id") String carId);

    @GET("cars/showroom/{showroom_id}")
    Call<List<Car>> getShowroomCars(@Path("showroom_id") String showroomId);

    @POST("cars/")
    Call<Car> createCar(@Body com.example.carrental.models.CarCreateRequest request);

    @DELETE("cars/{car_id}")
    Call<Void> deleteCar(@Path("car_id") String carId);

    // ─── Auth ────────────────────────────────────────────────────
    @POST("auth/login")
    Call<com.example.carrental.models.User> login(@Body com.example.carrental.models.User user);

    @POST("auth/register")
    Call<com.example.carrental.models.User> register(@Body Map<String, String> userDetails);

    // ─── Showrooms ───────────────────────────────────────────────
    @POST("showrooms/")
    Call<com.example.carrental.models.Showroom> createShowroom(@Body com.example.carrental.models.ShowroomCreateRequest request);

    @POST("showrooms/login")
    Call<com.example.carrental.models.Showroom> loginShowroom(@Body com.example.carrental.models.ShowroomLoginRequest request);

    // ─── Favorites ───────────────────────────────────────────────
    @POST("favorites/")
    Call<Map<String, String>> addFavorite(@Body Map<String, String> request);

    @DELETE("favorites/{user_id}/{car_id}")
    Call<Void> removeFavorite(@Path("user_id") String userId, @Path("car_id") String carId);

    @GET("favorites/{user_id}")
    Call<List<String>> getUserFavorites(@Path("user_id") String userId);

    // ─── Bookings ────────────────────────────────────────────────
    @POST("bookings/")
    Call<Booking> createBooking(@Body BookingRequest request);

    @GET("bookings/user/{user_id}")
    Call<List<Booking>> getUserBookings(@Path("user_id") String userId);

    @GET("bookings/showroom/{showroom_id}")
    Call<List<Booking>> getShowroomBookings(@Path("showroom_id") String showroomId);

    @GET("bookings/{booking_id}")
    Call<Booking> getBookingDetail(@Path("booking_id") String bookingId);

    @PUT("bookings/{booking_id}/status")
    Call<Booking> updateBookingStatus(@Path("booking_id") String bookingId, @Body Map<String, String> status);

    @DELETE("bookings/{booking_id}")
    Call<Void> cancelBooking(@Path("booking_id") String bookingId);

    // ─── Reviews ─────────────────────────────────────────────────
    @POST("reviews/")
    Call<Review> createReview(@Body ReviewRequest request);

    @GET("reviews/car/{car_id}")
    Call<List<Review>> getCarReviews(@Path("car_id") String carId);

    @DELETE("reviews/{review_id}")
    Call<Void> deleteReview(@Path("review_id") String reviewId);
}