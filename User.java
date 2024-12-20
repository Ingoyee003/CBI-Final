package com.example.finalterm;

public class User {
    public String firstName;
    public String lastName;
    public String username;
    public String email;
    public String phone;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {}

    public User(String firstName, String lastName, String username, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }
}
