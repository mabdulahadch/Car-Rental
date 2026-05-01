package com.example.carrental.models;

public class BookingRequest {
    private String customername;
    private String customeremail;
    private String customerphone;
    private String gender;
    private String pickupdate;
    private String returndate;
    private double totalamount;
    private boolean withdriver;
    private String carid;
    private String userid;

    public BookingRequest(String customername, String customeremail, String customerphone,
                          String gender, String pickupdate, String returndate,
                          double totalamount, boolean withdriver, String carid, String userid) {
        this.customername = customername;
        this.customeremail = customeremail;
        this.customerphone = customerphone;
        this.gender = gender;
        this.pickupdate = pickupdate;
        this.returndate = returndate;
        this.totalamount = totalamount;
        this.withdriver = withdriver;
        this.carid = carid;
        this.userid = userid;
    }
}
