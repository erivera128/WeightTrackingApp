package com.zybooks.weighttrackingemmanuelrivera.model;

public class User {
    private final long userId;
    private final String username;

    public User(long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public long getUserId() { return userId; }
    public String getUsername() { return username; }
}