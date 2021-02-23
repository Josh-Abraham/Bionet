package com.application.projecttbh;

public class OnboardData {
    public OnboardData() {
        this.setPassportId("");
        this.setFirstName("");
        this.setMiddleInitial("");
        this.setLastName("");
        this.setAddress("");
        this.setDob("");
    }
    private String passportId = "";
    private String firstName = "";
    private String middleInitial = "";
    private String lastName = "";
    private String dob = "";
    private String address = "";


    // Getter/setter
    public String getPassportId() {
        return this.passportId;
    }

    public void setPassportId(String newPassportId) {
        this.passportId = newPassportId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String newFirstName) {
        this.firstName = newFirstName;
    }

    public String getMiddleInitial() {
        return this.middleInitial;
    }

    public void setMiddleInitial(String newMiddleInitial) {
        this.middleInitial = newMiddleInitial;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String newLastName) {
        this.lastName = newLastName;
    }

    public String getDob() {
        return this.dob;
    }

    public void setDob(String newDob) {
        this.dob = newDob;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String newAddress) {
        this.address = newAddress;
    }

}