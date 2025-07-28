package com.example.smartroom.service;

import com.example.smartroom.model.User;

public final class UserSession {

    private static UserSession instance;
    private User user;

    private UserSession(User user) {
        this.user = user;
    }

    public static UserSession getInstance(User user) {
        instance = new UserSession(user);
        return instance;
    }

    public static UserSession getInstance() {
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void cleanUserSession() {
        user = null;
        instance = null;
    }
}