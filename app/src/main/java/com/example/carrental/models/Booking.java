package com.example.carrental.models;

import java.util.List;

public class Booking {
    private String id;
    private String customername;
    private String customeremail;
    private String customerphone;
    private String gender;
    private String pickupdate;
    private String returndate;
    private double totalamount;
    private boolean withdriver;
    private String status;
    private String carid;
    private String userid;
    private String createdat;
    private Car car; // nested car object from API

    // Getters
    public String getId() { return id; }
    public String getCustomerName() { return customername; }
    public String getCustomerEmail() { return customeremail; }
    public String getCustomerPhone() { return customerphone; }
    public String getGender() { return gender; }
    public String getPickupDate() { return pickupdate; }
    public String getReturnDate() { return returndate; }
    public double getTotalAmount() { return totalamount; }
    public boolean isWithDriver() { return withdriver; }
    public String getStatus() { return status; }
    public String getCarId() { return carid; }
    public String getUserId() { return userid; }
    public String getCreatedAt() { return createdat; }
    public Car getCar() { return car; }
}
