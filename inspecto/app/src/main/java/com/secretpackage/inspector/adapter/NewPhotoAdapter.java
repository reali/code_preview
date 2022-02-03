package com.secretpackage.inspector.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.secretpackage.inspector.PhotoFullScreenActivity;
import com.secretpackage.inspector.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by ali on 7/29/19.
 */

public class NewPhotoAdapter extends PagerAdapter {

    private final List<File> photoInfos;
    private LayoutInflater inflater;
    private Context context;

    public NewPhotoAdapter(Context context, List<File> photoInfos) {
        this.context = context;
        this.photoInfos = photoInfos;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View myImageLayout = inflater.inflate(R.layout.fragment_photo, container, false);

        ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.imageView);
        Picasso.with(context)
                .load(photoInfos.get(position))
                .placeholder(R.mipmap.ic_launcher)
                .resize(0, 300).onlyScaleDown()
                .into(myImage);
        container.addView(myImageLayout, 0);

        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PhotoFullScreenActivity.class);
                intent.putExtra("photo", photoInfos.get(position).getAbsolutePath());

                context.startActivity(intent);
            }
        });

        return myImageLayout;
    }

    @Override
    public int getCount() {
        return photoInfos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }
}
