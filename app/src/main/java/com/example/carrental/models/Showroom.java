package com.example.carrental.models;

import java.util.List;

public class Showroom {
    private String id;
    private String name;
    private String location;
    private String contact;
    private List<String> image; // This matches the JSON array ["url"]
    private float rating;

    public String getId() { return id; }

    public String getName() { return name; }
    
    public String getImageUrl() { 
        if (image != null && !image.isEmpty()) {
            return image.get(0);
        }
        return null;
    }

    public List<String> getImage() { return image; }

    public String getContact() { return contact; }
}
