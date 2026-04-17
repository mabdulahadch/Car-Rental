package com.example.carrental.models;

public class ShowroomLoginRequest {
    private String name;
    private String contact;

    public ShowroomLoginRequest(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }
}
