package com.secretpackage.inspector.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.secretpackage.inspector.PropertyDetailsActivity;
import com.secretpackage.inspector.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by ali on 7/29/19.
 */

public class NewPhotoPreviewAdapter extends PagerAdapter {

    private final int sidePreviewCount = 3;
    int vp;

    private final List<File> photoInfos;
    private LayoutInflater inflater;
    private Context context;

    public NewPhotoPreviewAdapter(Context context, List<File> photoInfos, int vp) {
        this.context = context;
        this.photoInfos = photoInfos;
        this.vp = vp;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public int getSidePreviewCount() {
        return sidePreviewCount;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View myImageLayout = inflater.inflate(R.layout.fragment_photo_preview, container, false);

        if (isDummy(position)) {

            ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.imageView);
            Picasso.with(context)
                    .load(android.R.color.transparent)
                    .resize(100, 100).onlyScaleDown()
                    .into(myImage);
            container.addView(myImageLayout, 0);

        } else {

            final int realPos = getRealPosition(position);

            ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.imageView);
            Picasso.with(context)
                    .load(photoInfos.get(realPos))
                    .placeholder(R.mipmap.ic_launcher)
                    .resize(100, 100).onlyScaleDown()
                    .into(myImage);
            container.addView(myImageLayout, 0);

            myImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (vp == 1) {
                        ((PropertyDetailsActivity)(context)).photoPager.setCurrentItem(realPos);
                    } else {
                        ((PropertyDetailsActivity)(context)).photoPager2.setCurrentItem(realPos);
                    }
                }
            });

        }



        return myImageLayout;
    }

    private boolean isDummy(int position) {
        return position < sidePreviewCount || position > photoInfos.size() - 1 + sidePreviewCount;
    }

    private int getRealPosition(int position) {
        return position - sidePreviewCount;
    }

    @Override
    public int getCount() {
        return photoInfos.size() + (sidePreviewCount * 2);
    }

    @Override
    public float getPageWidth(int position) {
        return 1.0f / getElementsPerPage();
    }

    private int getElementsPerPage() {
        return (sidePreviewCount * 2) + 1;
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
