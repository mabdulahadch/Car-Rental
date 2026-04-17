package com.example.carrental.models;

import java.util.List;

public class CarCreateRequest {
    private String brand;
    private String model;
    private String category;
    private double priceperday; // Matches the FastAPI model casing
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
    private double rating;
    private int reviewcount;

    public CarCreateRequest(String brand, String model, String category, double priceperday, int seats,
                            String enginepower, String maxspeed, String fueltype, String color,
                            String description, List<String> images, String registration,
                            List<String> features, String showroomid, String location,
                            boolean isavailable, double rating, int reviewcount) {
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.priceperday = priceperday;
        this.seats = seats;
        this.enginepower = enginepower;
        this.maxspeed = maxspeed;
        this.fueltype = fueltype;
        this.color = color;
        this.description = description;
        this.images = images;
        this.registration = registration;
        this.features = features;
        this.showroomid = showroomid;
        this.location = location;
        this.isavailable = isavailable;
        this.rating = rating;
        this.reviewcount = reviewcount;
    }

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
    public boolean isIsavailable() { return isavailable; }
    public double getRating() { return rating; }
    public int getReviewcount() { return reviewcount; }
}
