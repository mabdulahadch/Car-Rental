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

    // Getters
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public double getPriceperday() { return priceperday; }
    public List<String> getImages() { return images; }
    public String getLocation() { return location; }
    public float getRating() { return rating; }
    public boolean isAvailable() { return isavailable; }
    
    // Setters can be added if needed
}