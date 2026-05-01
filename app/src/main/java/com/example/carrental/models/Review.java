package com.example.carrental.models;

public class Review {
    private String id;
    private String username;
    private String userimage;
    private float rating;
    private String comment;
    private String carid;
    private String createdat;

    // Getters
    public String getId() { return id; }
    public String getUserName() { return username; }
    public String getUserImage() { return userimage; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCarId() { return carid; }
    public String getCreatedAt() { return createdat; }
}
