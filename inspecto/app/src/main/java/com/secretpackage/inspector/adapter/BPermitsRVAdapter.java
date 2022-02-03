package com.secretpackage.inspector.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.secretpackage.inspector.Chooser;
import com.secretpackage.inspector.EditBuildPermitActivity;
import com.secretpackage.inspector.R;
import com.secretpackage.inspector.model.BuildingPermit;
import com.secretpackage.inspector.model.PermitChanges;
import com.secretpackage.inspector.util.DateUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPermitsRVAdapter extends RecyclerView.Adapter<BPermitsRVAdapter.ViewHolder> {

    private final List<PermitChanges> mValues;
    Activity ctx;
    int propId = -1;
    String acc_num;

    public BPermitsRVAdapter(Activity ctx, int propId, List<PermitChanges> items) {
        mValues = items;
        this.ctx = ctx;
        this.propId = propId;

    }

    public BPermitsRVAdapter(Activity ctx, String acc_num, List<PermitChanges> items) {
        mValues = items;
        this.ctx = ctx;
        this.acc_num = acc_num;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_b_permit, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PermitChanges mItem = mValues.get(position);

        if (mItem.isUpdated) {
            holder.mView.setBackgroundColor(Color.argb(30, 0, 255, 0));
            holder.iv_check.setImageResource(R.drawable.check_gr);
        } else {
            holder.mView.setBackgroundResource(android.R.color.transparent);
            holder.iv_check.setImageResource(R.drawable.check);
        }

        if (mItem.selected) {
            holder.iv_check.setImageResource(R.drawable.check_gr);
        } else {
            holder.iv_check.setImageResource(R.drawable.check);
        }

        BuildingPermit permit = mItem.permit;

        holder.tv_issue_date.setText(DateUtils.fromRawStringToString(permit.getIssue_date()));
        holder.tv_compl_date.setText(DateUtils.fromRawStringToString(permit.getCompl_date()));
        holder.tv_insp_date.setText(DateUtils.fromRawStringToString(permit.getInsp_date()));
        holder.tv_percent.setText(permit.getPercentage() + "%");

        DecimalFormat formatter = new DecimalFormat("#,###");

        holder.tv_type.setText(permit.getPermit_type() + " - " + permit.getDescription());
        holder.tv_price.setText("$" + formatter.format(permit.getAmount()));
        holder.tv_note.setText(permit.getComments());

        if (propId != -1) {

            holder.bt_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(ctx, EditBuildPermitActivity.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("propid", propId);
                    ctx.startActivityForResult(intent, 156);

                }
            });

        } else {

            holder.bt_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(ctx, EditBuildPermitActivity.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("acc_num", acc_num);
                    ctx.startActivityForResult(intent, 156);

                }
            });

        }

        holder.iv_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItem.selected) {
                    holder.iv_check.setImageResource(R.drawable.check);
                    mItem.selected = false;
                } else {
                    holder.iv_check.setImageResource(R.drawable.check_gr);
                    mItem.selected = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.tv_issue_date) TextView tv_issue_date;
        @BindView(R.id.tv_compl_date) TextView tv_compl_date;
        @BindView(R.id.tv_insp_date) TextView tv_insp_date;
        @BindView(R.id.tv_percent) TextView tv_percent;
        @BindView(R.id.tv_type) TextView tv_type;
        @BindView(R.id.tv_price) TextView tv_price;
        @BindView(R.id.tv_note) TextView tv_note;
        @BindView(R.id.bt_change) Button bt_change;
        @BindView(R.id.iv_check) ImageView iv_check;

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
            mView = view;
//            mapLot = (TextView) view.findViewById(R.id.maplot);
//            id = (TextView) view.findViewById(R.id.id);
//            address = (TextView) view.findViewById(R.id.address);
//            type = (TextView) view.findViewById(R.id.type);
//            distance = (TextView) view.findViewById(R.id.dist);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tv_issue_date.getText() + "'";
        }
    }
}
