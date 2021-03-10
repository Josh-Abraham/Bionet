package com.application.projecttbh;

public class AppProperties {
    private String username = "";
    private final Boolean debugMode = true;
    private int fp_seq_num = 0;

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

    public int getFp_seq_num() {
        return fp_seq_num;
    }

    public void setFp_seq_num(int fp_seq_num) {
        this.fp_seq_num = fp_seq_num;
    }

    private static AppProperties instance;

    public static AppProperties getInstance() {
        if (instance == null)
            instance = new AppProperties();
        return instance;
    }

    private AppProperties() { }

    public Boolean getEnableFP() {
        return false;
    }
}