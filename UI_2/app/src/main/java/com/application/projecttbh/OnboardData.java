package com.application.projecttbh;

public class OnboardData {
    private String username = "";

    // Getter/setter
    public String getUsername() {
        return getInstance().username;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    private static OnboardData instance;

    public static OnboardData getInstance() {
        if (instance == null)
            instance = new OnboardData();
        return instance;
    }

    private OnboardData() { }
}