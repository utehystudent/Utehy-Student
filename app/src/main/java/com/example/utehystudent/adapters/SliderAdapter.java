package com.example.utehystudent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.utehystudent.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {

    private final List<String> mSliderItems;
      
    // Constructor
    public SliderAdapter(Context context, ArrayList<String> sliderDataArrayList) {
        this.mSliderItems = sliderDataArrayList;
    }

    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item_layout, null);
        return new SliderAdapterViewHolder(inflate);
    }
  

    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {
  
        String sliderItem = mSliderItems.get(position);

        try {
            Picasso.get()
                    .load(sliderItem)
                    .into(viewHolder.imageViewBackground);
        }catch (Exception e) {
            Picasso.get()
                    .load("http://konkanbazar.com/assets/vendor/images/no_img.png")
                    .into(viewHolder.imageViewBackground);
        }
    }
  

    @Override
    public int getCount() {
        return mSliderItems.size();
    }
  
    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView imageViewBackground;
  
        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.myimage);
            this.itemView = itemView;
        }
    }
}