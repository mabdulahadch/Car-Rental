package com.example.carrental.models;

public class User {
    private String id;
    private String name;
    private String email;
    private String password; // used for request

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
