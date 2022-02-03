package com.secretpackage.inspector.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.secretpackage.inspector.R;

import java.util.List;
import java.util.Map;

/**
 * Created by ali on 7/14/19.
 */

public class ChooserAdapter extends BaseAdapter {

    private LayoutInflater layoutinflater;
    private Map<String, String> listStorage1;
    private List<Integer> selectedList;
    private String[] mKeys;
    private Context context;
    private boolean isMultiSelect;

    public ChooserAdapter(Context context, Map<String, String> customizedListView) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage1 = customizedListView;
        mKeys = customizedListView.keySet().toArray(new String[customizedListView.size()]);
        isMultiSelect = false;

    }

    public ChooserAdapter(Context context, Map<String, String> customizedListView, List<Integer> selectedList, boolean isMultiSelect) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage1 = customizedListView;
        mKeys = customizedListView.keySet().toArray(new String[customizedListView.size()]);
        this.isMultiSelect = isMultiSelect;
        this.selectedList = selectedList;
    }

    @Override
    public int getCount() {
        return listStorage1.size()-1;
    }

    @Override
    public Object getItem(int position) {
        return listStorage1.get(mKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {

        int position = pos+1;

        final ViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.item_choose, parent, false);
            listViewHolder.textInListView = (TextView)convertView.findViewById(R.id.textView7);
//            listViewHolder.imageInListView = (ImageView)convertView.findViewById(R.id.imageView);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }

        if (listStorage1.get(mKeys[position]).isEmpty()) {
            listViewHolder.textInListView.setText(mKeys[position]);
        } else {
            listViewHolder.textInListView.setText(mKeys[position] + " - " + listStorage1.get(mKeys[position]));
        }

        if (isMultiSelect) {

            if (selectedList.get(pos+1) == 1) {
                listViewHolder.textInListView.setBackgroundColor(Color.parseColor("#AA343434"));
            } else {
                listViewHolder.textInListView.setBackgroundColor(Color.parseColor("#55343434"));
            }

            listViewHolder.textInListView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedList.get(pos+1) == 0) {
                        selectedList.set(pos+1, 1);
                        listViewHolder.textInListView.setBackgroundColor(Color.parseColor("#AA343434"));
                    } else {
                        selectedList.set(pos+1, 0);
                        listViewHolder.textInListView.setBackgroundColor(Color.parseColor("#55343434"));
                    }
                }
            });
        }

//        int imageResourceId = this.context.getResources().getIdentifier(listStorage.get(position).getImageResource(), "drawable", this.context.getPackageName());
//        listViewHolder.imageInListView.setImageResource(imageResourceId);

        return convertView;
    }

    static class ViewHolder{
        TextView textInListView;
//        ImageView imageInListView;
    }

}
