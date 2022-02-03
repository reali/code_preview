package com.secretpackage.inspector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.secretpackage.inspector.R;
import com.secretpackage.inspector.model.Occupancy;
import com.secretpackage.inspector.util.Constants;

import java.util.List;

/**
 * Created by ali on 7/10/19.
 */

public class MyAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    List<Occupancy> objects;

    MyAdapter(Context context, List<Occupancy> products) {
        ctx = context;
        objects = products;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_my, parent, false);
        }

        Occupancy p = objects.get(position);
        ((TextView) view.findViewById(R.id.tv_my)).setText(Constants.Utility_values.get(p.getCode()));

        return view;
    }
}
