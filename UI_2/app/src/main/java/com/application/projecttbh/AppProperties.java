package com.application.projecttbh;

public class AppProperties {
    private String username = "";
    private final Boolean debugMode = true;
    private final Boolean enableFP = false;
    private int seq_num = 0;

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

    public int getSeqNum() {
        return seq_num;
    }

    public void setSeqNum(int seq_num) {
        this.seq_num = seq_num;
    }

    private static AppProperties instance;

    public static AppProperties getInstance() {
        if (instance == null)
            instance = new AppProperties();
        return instance;
    }

    private AppProperties() { }

    public Boolean getEnableFP() {
        return enableFP;
    }
}