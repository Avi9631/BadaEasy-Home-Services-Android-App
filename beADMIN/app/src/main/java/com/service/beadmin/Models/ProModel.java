package com.service.beadmin.Models;

public class ProModel {

    private String proID;
    private String proName;
    private String proMobile;
    private String proStatus;
    private String proDuty;
    private String proCategory;
    private String proLocation;
    private String bookID;

    public ProModel(String proID, String proName, String proMobile, String proStatus, String proDuty, String proCategory, String proLocation, String bookID) {
        this.proID = proID;
        this.proName = proName;
        this.proMobile = proMobile;
        this.proStatus = proStatus;
        this.proDuty = proDuty;
        this.proCategory = proCategory;
        this.proLocation = proLocation;
        this.bookID = bookID;
    }

    public String getProID() {
        return proID;
    }

    public void setProID(String proID) {
        this.proID = proID;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProMobile() {
        return proMobile;
    }

    public void setProMobile(String proMobile) {
        this.proMobile = proMobile;
    }

    public String getProStatus() {
        return proStatus;
    }

    public void setProStatus(String proStatus) {
        this.proStatus = proStatus;
    }

    public String getProDuty() {
        return proDuty;
    }

    public void setProDuty(String proDuty) {
        this.proDuty = proDuty;
    }

    public String getProCategory() {
        return proCategory;
    }

    public void setProCategory(String proCategory) {
        this.proCategory = proCategory;
    }

    public String getProLocation() {
        return proLocation;
    }

    public void setProLocation(String proLocation) {
        this.proLocation = proLocation;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }
}
