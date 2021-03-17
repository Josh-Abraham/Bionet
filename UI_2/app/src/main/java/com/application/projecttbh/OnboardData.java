package com.application.projecttbh;

public class OnboardData {

    private String passportId = "";
    private String firstName = "";
    private String middleInitial = "";
    private String lastName = "";
    private String dob = "";
    // Address Data
    private String streetAddress = "";
    private String unitNumber = "";
    private String city = "";
    private String province = "";
    private String country = "Canada";
    private String postalCode = "";

    //Picture Information
    private String directory = "";
    private String file = "";
    private String s3_facial_key = "";


    // Fingerprint Data S3 Locations
    private String[] s3_fp_data = new String[] {"", "", "", ""};

    // Iris Data S3 Locations
    private String[] s3_iris_data = new String[] {"", "", "", ""};

    // Getter/setter
    public String getPassportId() {
        return getInstance().passportId;
    }

    public void setPassportId(String newPassportId) {
        this.passportId = newPassportId;
    }

    public String getFirstName() {
        return getInstance().firstName;
    }

    public void setFirstName(String newFirstName) {
        this.firstName = newFirstName;
    }

    public String getMiddleInitial() {
        return getInstance().middleInitial;
    }

    public void setMiddleInitial(String newMiddleInitial) {
        this.middleInitial = newMiddleInitial;
    }

    public String getLastName() {
        return getInstance().lastName;
    }

    public void setLastName(String newLastName) {
        this.lastName = newLastName;
    }

    public String getDob() {
        return getInstance().dob;
    }

    public void setDob(String newDob) {
        this.dob = newDob;
    }

    public String getFullName() {
        if (this.getMiddleInitial().equals("")) {
            return this.getFirstName() + " " + this.getLastName();
        }

        return this.getFirstName() + " " + this.getMiddleInitial() + " " + this.getLastName();
    }
    // ADDRESS DATA

    public String getStreetAddress() {
        return getInstance().streetAddress;
    }

    public void setStreetAddress(String newAddress) {
        this.streetAddress = newAddress;
    }

    public String getUnitNumber() {
        return getInstance().unitNumber;
    }

    public void setUnitNumber(String newUnitNumber) {
        this.unitNumber = newUnitNumber;
    }

    public String getCity() {
        return getInstance().city;
    }

    public void setCity(String newCity) {
        this.city = newCity;
    }

    public String getProvince() {
        return getInstance().province;
    }

    public void setProvince(String newProvince) {
        this.province = newProvince;
    }

    public String getCountry() {
        return getInstance().country;
    }

    public void setCountry(String newCountry) {
        this.country = country;
    }

    public String getPostalCode() {
        return getInstance().postalCode;
    }

    public void setPostalCode(String newPostalCode) {
        this.postalCode = newPostalCode;
    }


    public String getFormattedAddress() {
        if (this.getUnitNumber().equals("")) {
            return this.getStreetAddress() + ", " + this.getCity() + ", " + this.getProvince() + ", " + this.getCountry() + " - " + this.getPostalCode();
        }
        return this.getStreetAddress() + " " + this.getUnitNumber() + ", " + this.getCity() + ", " + this.getProvince() + ", " + this.getCountry() + " - " + this.getPostalCode();
    }

    // File Data
    public String getDirectory() {
        return getInstance().directory;
    }

    public void setDirectory(String newDirectory) {
        this.directory = newDirectory;
    }

    public String getFile() {
        return getInstance().file;
    }

    public void setFile(String newFile) {
        this.file = newFile;
    }

    public String getS3_facial_key() {
        return getInstance().s3_facial_key;
    }

    public void setS3_facial_key(String new_s3_facial_key) {
        this.s3_facial_key = new_s3_facial_key;
    }

    // FP DATA
    public String[] get_S3_fp_data() {
        return s3_fp_data;
    }

    public void update_S3_fp_data(String newCode, int index) {
        this.s3_fp_data[index] = newCode;
    }

    // IRIS DATA
    public String[] get_S3_iris_data() {
        return s3_iris_data;
    }

    public void update_S3_iris_data(String newCode, int index) {
        this.s3_iris_data[index] = newCode;
    }

    // Singleton Instance
    private static OnboardData instance;

    public static OnboardData getInstance() {
        if (instance == null)
            instance = new OnboardData();
        return instance;
    }

    public void resetInstance() {
        this.passportId = "";
        this.firstName = "";
        this.middleInitial = "";
        this.lastName = "";
        this.dob = "";
        // Address Data
        this.streetAddress = "";
        this.unitNumber = "";
        this.city = "";
        this.province = "";
        this.country = "Canada";
        this.postalCode = "";

        //Picture Information
        this.directory = "";
        this.file = "";
        this.s3_facial_key = "";

        // FP S3 Locations
        this.s3_fp_data = new String[]{"", "", "", ""};

        AppProperties.getInstance().setSeqNum(0);
    }

    private OnboardData() {
        this.resetInstance();
    }
}