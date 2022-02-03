package com.pa3424.app.stabs1.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pa3424.app.stabs1.R;
import com.pa3424.app.stabs1.data.DayObj;
import com.pa3424.app.stabs1.fragments.WeekGraphFragment;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ali on 5/20/20.
 */

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ItemVH> {

    private Context context;
    private List<DayObj> items;
    private final OnItemClickListener listener;
    private int width_progress_100;

    public WeekAdapter(Context context, List<DayObj> items, int width_progress_100, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.width_progress_100 = width_progress_100;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public ItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_week_day, parent, false);
        return new ItemVH(view);
    }

    @Override
    public void onBindViewHolder(ItemVH holder, final int position) {

        final DayObj day = items.get(position);

        if (day != null) {

            int percent = day.getPercent();

            SimpleDateFormat fmtOut = new SimpleDateFormat("MM/dd");

            holder.tv_date.setText(fmtOut.format(day.getDate()));
            holder.tv_percent.setText(percent + "%");

            if (percent < 25) {
                holder.iv_percent.setImageResource(R.drawable.shape_orange);
            } else if (percent >= 25 && percent < 100) {
                holder.iv_percent.setImageResource(R.drawable.shape_blue);
            } else if (percent >= 100) {
                holder.iv_percent.setImageResource(R.drawable.shape_green);
            }

            if (percent > 100) {
                percent = 100;
            }

            int w = width_progress_100 / 100 * percent;
            int h = WeekGraphFragment.dpToPx(7);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(w, h);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;

            holder.iv_percent.setLayoutParams(layoutParams);

//            holder.root_view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    listener.onItemClick(position);
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public class ItemVH extends RecyclerView.ViewHolder {

//        @BindView(R.id.tv_date) View root_view;

        @BindView(R.id.tv_date) TextView tv_date;
        @BindView(R.id.tv_percent) TextView tv_percent;
        @BindView(R.id.iv_percent) ImageView iv_percent;

        public ItemVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

    }

}
