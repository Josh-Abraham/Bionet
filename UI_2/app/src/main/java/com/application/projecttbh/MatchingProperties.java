package com.application.projecttbh;

public class MatchingProperties {
    private boolean enableFace = true;
    private boolean enableFP = false;
    private Boolean[] fpOptions = new Boolean[] {false, false}; // LT, RT
    private String[] fpS3 = new String[] {"", ""}; // LT, RT
    private boolean enableIris = false;
    private Boolean[] irisOptions = new Boolean[] {false, false}; // IL, IR
    private String[] irisS3 = new String[] {"", ""}; // LI, RI
    private Integer[] fullSeq = new Integer[]{};
    private String passportId = "";
    private String facialScan = "";
    private final String directory = "Matching";
    private Boolean[] FpMatches = new Boolean[] {false, false};

    private static MatchingProperties instance;
    private String irisImage;

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

    public Boolean[] getFpOptions() {
        return fpOptions;
    }

    public Boolean[] getIrisOptions() {
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

    public String[] getFpS3() {
        return fpS3;
    }

    public void setFpS3(String[] fpS3) {
        this.fpS3 = fpS3;
    }

    public void updateFpS3(int pos, String file) {
        this.fpS3[pos] = file;
    }

    public String[] getIrisS3() {
        return irisS3;
    }

    public void setIrisS3(String[] irisS3) {
        this.irisS3 = irisS3;
    }

    public void updateIrisS3(int pos, String file) {
        this.irisS3[pos] = file;
    }

    public String getDirectory() {
        return directory;
    }

    public void resetInstance() {
        this.enableFace = true;
        this.enableFP = false;
        this.fpOptions = new Boolean[] {false, false}; // LT, RT
        this.fpS3 = new String[] {"", ""}; // LT, RT
        this.enableIris = false;
        this.irisOptions = new Boolean[] {false, false}; // IL, IR
        this.irisS3 = new String[] {"", ""}; // LI, RI
        this.passportId = "";
        this.facialScan = "";
        this.fullSeq = new Integer[]{};
    }

    public Integer[] getFullSeq() {
        return fullSeq;
    }

    public void setFullSeq(Integer[] fullSeq) {
        this.fullSeq = fullSeq;
    }

    public Boolean[] getFpMatches() {
        return FpMatches;
    }

    public void setFpMatches(Boolean[] fpMatches) {
        this.FpMatches = fpMatches;
    }

    public void setFPMatchIndex(int index, Boolean newVal) {
        this.FpMatches[index] = newVal;
    }

    public void setIris_image(String data) {
        this.irisImage = data;
    }

    public String getIris_image() {
        return this.irisImage;
    }
}