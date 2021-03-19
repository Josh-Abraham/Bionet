package com.application.projecttbh;

public class MatchingProperties {
    private boolean enableFace = true;
    private boolean enableFP = false;
    private boolean[] fpOptions = new boolean[] {false, false, false, false}; // LT, LI, RT, RI
    private boolean enableIris = false;
    private boolean[] irisOptions = new boolean[] {false, false}; // IL, IR
    private String passportId = "";
    private String facialScan = "";

    private static MatchingProperties instance;
    private String matchingDirectory = "";

    public static MatchingProperties getInstance() {
        if (instance == null)
            instance = new MatchingProperties();
        return instance;
    }

    private MatchingProperties() { }

    public boolean isEnableFace() {
        return enableFace;
    }

    public void setEnableFace(boolean enableFace) {
        this.enableFace = enableFace;
    }

    public boolean isEnableFP() {
        return enableFP;
    }

    public void setEnableFP(boolean enableFP) {
        this.enableFP = enableFP;
    }

    public boolean isEnableIris() {
        return enableIris;
    }

    public void setEnableIris(boolean enableIris) {
        this.enableIris = enableIris;
    }

    public boolean[] getFpOptions() {
        return fpOptions;
    }

    public boolean[] getIrisOptions() {
        return irisOptions;
    }

    public void updateFpOptions(int pos, boolean checked) {
        this.fpOptions[pos] = checked;
    }

    public void updateIrisOptions(int pos, boolean checked) {
        this.irisOptions[pos] = checked;
    }

    public void setPassportId(String text) {
        this.passportId = text;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setFacialScan(String s) {
        this.facialScan = s;
    }

    public String getFacialScan() {
        return facialScan;
    }

    public void setDirectory(String matching_facial) {
        this.matchingDirectory = matching_facial;
    }

    public String getMatchingDirectoy() {
        return  this.matchingDirectory;
    }
}