package com.example.carrental.models;

import java.util.List;

public class ShowroomCreateRequest {
    private String name;
    private String email;
    private String contact;
    private String location;
    private List<String> image;

    public ShowroomCreateRequest(String name, String email, String contact, String location, List<String> image) {
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.location = location;
        this.image = image;
    }
}
