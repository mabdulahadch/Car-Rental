package com.example.carrental.models;

public class Booking {
    public String id;
    public String customerName;
    public String customerEmail;
    public String customerPhone;
    public String gender;
    public String pickupDate;
    public String returnDate;
    public double totalAmount;
    public boolean withDriver;
    public String status; // PENDING, APPROVED, REJECTED
    public String carId;
    public String createdAt;
}