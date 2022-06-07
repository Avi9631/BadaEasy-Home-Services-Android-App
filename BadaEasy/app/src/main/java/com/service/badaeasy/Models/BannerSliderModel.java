package com.service.badaeasy.Models;

import androidx.recyclerview.widget.RecyclerView;

public class BannerSliderModel {

    private String banner;
    private String backgroundColor;

    public BannerSliderModel(String banner, String backgroundColor) {
        this.banner = banner;
        this.backgroundColor = backgroundColor;
    }

    public BannerSliderModel() {
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

}
