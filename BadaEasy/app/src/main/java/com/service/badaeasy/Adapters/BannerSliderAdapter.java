package com.service.badaeasy.Adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.service.badaeasy.Models.BannerSliderModel;
import com.service.badaeasy.R;

import java.util.List;

public class BannerSliderAdapter extends PagerAdapter {

    private List<BannerSliderModel> sliderModelList;

    public BannerSliderAdapter(List<BannerSliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view= LayoutInflater.from(container.getContext()).inflate(R.layout.item_banner_slider, container, false);
        ConstraintLayout bannerContainer= view.findViewById(R.id.banner_container);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bannerContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(sliderModelList.get(position).getBackgroundColor())));
        }
        ImageView banner= view.findViewById(R.id.banner_slide);
        Glide.with(container.getContext()).load(sliderModelList.get(position).getBanner()).apply(new RequestOptions().placeholder(R.drawable.key)).into(banner);
        container.addView(view, 0);

        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return sliderModelList.size();
    }
}
