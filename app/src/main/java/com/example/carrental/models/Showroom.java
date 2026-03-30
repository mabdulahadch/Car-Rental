package com.example.carrental.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Showroom {
    public String id;
    public String name;
    public String email;
    public String contact;
    public String location;
    
    // In your SQL this is TEXT[]
    public List<String> image;
    
    public List<Car> cars;

    @SerializedName("createdat")
    public String createdAt;
}