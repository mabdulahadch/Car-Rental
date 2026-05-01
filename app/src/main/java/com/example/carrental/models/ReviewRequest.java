package com.example.carrental.models;

public class ReviewRequest {
    private String username;
    private String userimage;
    private float rating;
    private String comment;
    private String carid;

    public ReviewRequest(String username, String userimage, float rating, String comment, String carid) {
        this.username = username;
        this.userimage = userimage;
        this.rating = rating;
        this.comment = comment;
        this.carid = carid;
    }
}
