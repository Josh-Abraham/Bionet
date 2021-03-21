package com.application.projecttbh;

public class AppProperties {
    private String username = "";
    private final Boolean debugMode = false;
    private final Boolean enableFP = true;
    private int seq_num = 0;
    private Boolean batchMode = false;

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

    public Boolean getBatchMode() {
        return batchMode;
    }

    public void setBatchMode(boolean batchMode) {
        this.batchMode = batchMode;
    }
}