package com.service.beadmin.Models;

public class ServiceBuyModel {

    private String buyTitle;
    private String buyRate;
    private String buyMRP;
    private String buyDes;
    private String addToCart;
    private String add1;
    private String add2;
    private String add3;

    public ServiceBuyModel(String buyTitle, String buyRate, String buyMRP, String buyDes, String addToCart, String add1, String add2, String add3) {
        this.buyTitle = buyTitle;
        this.buyRate = buyRate;
        this.buyMRP = buyMRP;
        this.buyDes = buyDes;
        this.addToCart = addToCart;
        this.add1 = add1;
        this.add2 = add2;
        this.add3 = add3;
    }

    public String getBuyTitle() {
        return buyTitle;
    }

    public void setBuyTitle(String buyTitle) {
        this.buyTitle = buyTitle;
    }

    public String getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(String buyRate) {
        this.buyRate = buyRate;
    }

    public String getBuyMRP() {
        return buyMRP;
    }

    public void setBuyMRP(String buyMRP) {
        this.buyMRP = buyMRP;
    }

    public String getBuyDes() {
        return buyDes;
    }

    public void setBuyDes(String buyDes) {
        this.buyDes = buyDes;
    }

    public String getAddToCart() {
        return addToCart;
    }

    public void setAddToCart(String addToCart) {
        this.addToCart = addToCart;
    }

    public String getAdd1() {
        return add1;
    }

    public void setAdd1(String add1) {
        this.add1 = add1;
    }

    public String getAdd2() {
        return add2;
    }

    public void setAdd2(String add2) {
        this.add2 = add2;
    }

    public String getAdd3() {
        return add3;
    }

    public void setAdd3(String add3) {
        this.add3 = add3;
    }
}
