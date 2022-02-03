package com.secretpackage.inspector.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.secretpackage.inspector.Chooser;
import com.secretpackage.inspector.R;
import com.secretpackage.inspector.model.NewValue;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UtilitiesRVAdapter extends RecyclerView.Adapter<UtilitiesRVAdapter.ViewHolder> {

    private List<String> items;
    private List<NewValue> newItems;
    private Activity ctx;
    private int resCode;
    private int type;

    public UtilitiesRVAdapter(Activity ctx, List<String> items, List<NewValue> newItems, int type, int resCode) {
        this.items = items;
        this.newItems = newItems;
        this.ctx = ctx;
        this.resCode = resCode;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_utility, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final NewValue newValue = newItems.get(position);

        if (items.get(position).contains("null")) {
            holder.tv_value.setText(items.get(position).replace("null", "NONE"));
        } else {
            holder.tv_value.setText(items.get(position));
        }

        if (newValue != null) {
            if (newValue.selected) {
                holder.bt_change.setText(newValue.value + " - " + newValue.showValue);
                holder.iv_check.setImageResource(R.drawable.check_gr);
                newValue.showValue = holder.tv_value.getText().toString();
            } else {
                holder.bt_change.setText("Change");
                holder.iv_check.setImageResource(R.drawable.check);
                newValue.showValue = "";
            }
        }

        holder.bt_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, Chooser.class);
                intent.putExtra("type", type);
                intent.putExtra("pos", position);
                ctx.startActivityForResult(intent, resCode);
            }
        });

        holder.iv_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newValue.selected) {
                    holder.bt_change.setText("Change");
                    holder.iv_check.setImageResource(R.drawable.check);
                    newValue.showValue = "";
                    newValue.selected = false;
                } else {
                    holder.bt_change.setText(holder.tv_value.getText());
                    holder.iv_check.setImageResource(R.drawable.check_gr);
                    newValue.showValue = holder.tv_value.getText().toString();
                    newValue.selected = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_value) TextView tv_value;
        @BindView(R.id.bt_change) Button bt_change;
        @BindView(R.id.iv_check) ImageView iv_check;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tv_value.getText() + "'";
        }
    }
}
