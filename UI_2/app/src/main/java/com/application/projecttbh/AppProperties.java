package com.application.projecttbh;

public class AppProperties {
    private String username = "";
    private final Boolean debugMode = false;
    private final Boolean enableFP = true;
    private int seq_num = 0;
    private Boolean batchMode = false;
    private String type = "";
    private boolean ran = false;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void resetInstance() {
        this.username = "";
        this.seq_num = 0;
        this.batchMode = false;
        this.type = "";
    }

    public boolean isRan() {
        return ran;
    }

    public void setRan(boolean ran) {
        this.ran = ran;
    }
}