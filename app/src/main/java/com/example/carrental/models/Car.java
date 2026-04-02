package com.example.carrental.models;

import java.util.List;

public class Car {
    private String id;
    private String brand;
    private String model;
    private String category;
    private double priceperday;
    private int seats;
    private String enginepower;
    private String maxspeed;
    private String fueltype;
    private String color;
    private String description;
    private List<String> images;
    private String registration;
    private List<String> features;
    private String showroomid;
    private String location;
    private boolean isavailable;
    private float rating;
    private int reviewcount;
    private Showroom showroom;

    // Getters
    public String getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getCategory() { return category; }
    public double getPriceperday() { return priceperday; }
    public int getSeats() { return seats; }
    public String getEnginepower() { return enginepower; }
    public String getMaxspeed() { return maxspeed; }
    public String getFueltype() { return fueltype; }
    public String getColor() { return color; }
    public String getDescription() { return description; }
    public List<String> getImages() { return images; }
    public String getRegistration() { return registration; }
    public List<String> getFeatures() { return features; }
    public String getShowroomid() { return showroomid; }
    public String getLocation() { return location; }
    public boolean isAvailable() { return isavailable; }
    public float getRating() { return rating; }
    public int getReviewcount() { return reviewcount; }
    public Showroom getShowroom() { return showroom; }
}
