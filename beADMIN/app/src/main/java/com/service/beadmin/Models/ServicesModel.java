package com.service.beadmin.Models;

public class ServicesModel {
    private String serviceIcon;
    private String serviceTitle;
    private String servicekey;
    private String servicetype;
    private String servicekeylast;
    private String serviceadd1;
    private String serviceadd2;
    private String serviceadd3;

    public ServicesModel(String serviceIcon, String serviceTitle, String servicekey, String servicetype, String servicekeylast, String serviceadd1, String serviceadd2, String serviceadd3) {
        this.serviceIcon = serviceIcon;
        this.serviceTitle = serviceTitle;
        this.servicekey = servicekey;
        this.servicetype = servicetype;
        this.servicekeylast = servicekeylast;
        this.serviceadd1 = serviceadd1;
        this.serviceadd2 = serviceadd2;
        this.serviceadd3 = serviceadd3;
    }

    public String getServiceIcon() {
        return serviceIcon;
    }

    public void setServiceIcon(String serviceIcon) {
        this.serviceIcon = serviceIcon;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getServicekey() {
        return servicekey;
    }

    public void setServicekey(String servicekey) {
        this.servicekey = servicekey;
    }

    public String getServicetype() {
        return servicetype;
    }

    public void setServicetype(String servicetype) {
        this.servicetype = servicetype;
    }

    public String getServicekeylast() {
        return servicekeylast;
    }

    public void setServicekeylast(String servicekeylast) {
        this.servicekeylast = servicekeylast;
    }

    public String getServiceadd1() {
        return serviceadd1;
    }

    public void setServiceadd1(String serviceadd1) {
        this.serviceadd1 = serviceadd1;
    }

    public String getServiceadd2() {
        return serviceadd2;
    }

    public void setServiceadd2(String serviceadd2) {
        this.serviceadd2 = serviceadd2;
    }

    public String getServiceadd3() {
        return serviceadd3;
    }

    public void setServiceadd3(String serviceadd3) {
        this.serviceadd3 = serviceadd3;
    }
}
