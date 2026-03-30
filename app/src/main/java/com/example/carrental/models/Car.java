package com.example.carrental.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Car {
    public String id;
    public String brand;
    public String model;
    public String category;

    @SerializedName("priceperday")
    public double pricePerDay;

    public int seats;

    @SerializedName("enginepower")
    public String enginePower;

    @SerializedName("maxspeed")
    public String maxSpeed;

    @SerializedName("fueltype")
    public String fuelType;

    public String color;
    public String description;
    public List<String> images;
    public String registration;
    public List<String> features;

    @SerializedName("showroomid")
    public String showroomId;

    public String location;

    @SerializedName("isavailable")
    public boolean isAvailable;

    public double rating;

    @SerializedName("reviewcount")
    public int reviewCount;
}