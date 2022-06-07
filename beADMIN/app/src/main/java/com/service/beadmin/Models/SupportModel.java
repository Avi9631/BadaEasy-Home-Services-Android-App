package com.service.beadmin.Models;

public class SupportModel {

    private String reqID;
    private String exeID;
    private String userID;
    private String phone;
    private String status;
    private String timeReq;
    private String timeRes;

    public SupportModel(String reqID, String exeID, String userID, String phone, String status, String timeReq, String timeRes) {
        this.reqID = reqID;
        this.exeID = exeID;
        this.userID = userID;
        this.phone = phone;
        this.status = status;
        this.timeReq = timeReq;
        this.timeRes = timeRes;
    }

    public String getReqID() {
        return reqID;
    }

    public void setReqID(String reqID) {
        this.reqID = reqID;
    }

    public String getExeID() {
        return exeID;
    }

    public void setExeID(String exeID) {
        this.exeID = exeID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeReq() {
        return timeReq;
    }

    public void setTimeReq(String timeReq) {
        this.timeReq = timeReq;
    }

    public String getTimeRes() {
        return timeRes;
    }

    public void setTimeRes(String timeRes) {
        this.timeRes = timeRes;
    }
}
