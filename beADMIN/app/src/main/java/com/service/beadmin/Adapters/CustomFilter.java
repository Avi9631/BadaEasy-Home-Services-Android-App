package com.service.beadmin.Adapters;

import android.widget.Filter;

import com.service.beadmin.Models.BookingModel;

import java.util.ArrayList;
import java.util.List;

class CustomFilter extends Filter {

    BookingAdapter adapter;
    List<BookingModel> filterList;

    public CustomFilter(List<BookingModel> filterList, BookingAdapter bookingAdapter) {
        this.adapter=bookingAdapter;
        this.filterList= filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults=new FilterResults();
        if(constraint!=null || constraint.length()>0){
            constraint=constraint.toString().toLowerCase();
            List<BookingModel> list= new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getBid().toLowerCase().contains(constraint)){
                    list.add(filterList.get(i));
                }
            }
            filterResults.count= list.size();
            filterResults.values= list;
        }else {
            filterResults.count= filterList.size();
            filterResults.values= filterList;
        }
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.mData= (List<BookingModel>) results.values;
        adapter.notifyDataSetChanged();
    }


}
