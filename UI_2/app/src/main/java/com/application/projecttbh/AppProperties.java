package com.application.projecttbh;

public class AppProperties {
    private String username = "";
    private Boolean debugMode = false;

    // Getter/setter
    public String getUsername() {
        return getInstance().username;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public Boolean getDebugMode() {
        return getInstance().debugMode;
    }

    private static AppProperties instance;

    public static AppProperties getInstance() {
        if (instance == null)
            instance = new AppProperties();
        return instance;
    }

    private AppProperties() { }
}