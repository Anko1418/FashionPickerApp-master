package application.com.fashionpicker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import application.com.fashionpicker.R;
import application.com.fashionpicker.database.models.ImageHolderModel;

public class CustomPagerAdapter extends PagerAdapter {

    private ArrayList<ImageHolderModel> imageHolderModels = new ArrayList<>();
    private Context context;

    public CustomPagerAdapter(Context context) {

        this.context = context;
    }

    public void setImageHolderModels(ArrayList<ImageHolderModel> imageHolderModels) {
        this.imageHolderModels = imageHolderModels;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_pager_item_layout, container, false);
        container.addView(view);
        ImageView imageView = view.findViewById(R.id.txt);
        ImageHolderModel imageHolderModel = imageHolderModels.get(position);
        Glide.with(context).load(new File(imageHolderModel.getFilePath())).into(imageView);
        return view;
    }

    @Override
    public int getCount() {
        return imageHolderModels.size();
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return view == object;
    }
}

