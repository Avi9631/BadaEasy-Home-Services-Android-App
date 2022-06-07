package com.service.badaeasy.Models;

public class AddressModel {

    private String city;
    private String locality;
    private String flatNo;
    private String pincode;
    private String landmark;
    private String name;
    private String mobile;
    private String altMobile;
    private String state;
    private boolean check;

    public AddressModel(String city, String locality, String flatNo, String pincode, String landmark, String name, String mobile, String altMobile, String state, boolean check) {
        this.city = city;
        this.locality = locality;
        this.flatNo = flatNo;
        this.pincode = pincode;
        this.landmark = landmark;
        this.name = name;
        this.mobile = mobile;
        this.altMobile = altMobile;
        this.state = state;
        this.check = check;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getFlatNo() {
        return flatNo;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAltMobile() {
        return altMobile;
    }

    public void setAltMobile(String altMobile) {
        this.altMobile = altMobile;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
