package com.service.badaeasy.Models;

public class ProModel {

    private String proTitle;
    private int proIcon;

    public ProModel(String proTitle, int proIcon) {
        this.proTitle = proTitle;
        this.proIcon = proIcon;
    }

    public String getProTitle() {
        return proTitle;
    }

    public void setProTitle(String proTitle) {
        this.proTitle = proTitle;
    }

    public int getProIcon() {
        return proIcon;
    }

    public void setProIcon(int proIcon) {
        this.proIcon = proIcon;
    }
}
