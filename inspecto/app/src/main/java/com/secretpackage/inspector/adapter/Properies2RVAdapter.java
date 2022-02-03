package com.secretpackage.inspector.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.secretpackage.inspector.MainActivity;
import com.secretpackage.inspector.R;
import com.secretpackage.inspector.fragment.ProperiesFragment;
import com.secretpackage.inspector.model.Property2;

import java.util.List;

public class Properies2RVAdapter extends RecyclerView.Adapter<Properies2RVAdapter.ViewHolder> {

    private final List<Property2> mValues;
    private final ProperiesFragment.OnListFragmentInteractionListener mListener;
    private boolean multiselect;
    private boolean selectable = false;
    private MainActivity activity;

    public Properies2RVAdapter(MainActivity activity, List<Property2> items, ProperiesFragment.OnListFragmentInteractionListener listener, boolean multiselect) {
        mValues = items;
        mListener = listener;
        this.multiselect = multiselect;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_properies, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Property2 mItem  = mValues.get(position);

        holder.id.setText(mItem.getAccount_Number());
        if (mItem.getSitus_Address() != null && !mItem.getSitus_Address().isEmpty()) {
            holder.address.setText(mItem.getSitus_Address() + ", Broomfield, CO");
        } else {
            holder.address.setText("-");
        }


        holder.mapLot.setText("-");

        holder.type.setText(mValues.get(position).getProperty_type());

        if (multiselect) {

            holder.mView.setBackgroundColor(mItem.isSelected ? Color.parseColor("#AA0d71ad") : Color.TRANSPARENT);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (selectable) {

                        mItem.isSelected = !mItem.isSelected;
                        holder.mView.setBackgroundColor(mItem.isSelected ? Color.parseColor("#AA0d71ad") : Color.TRANSPARENT);

                        calcSelected();

                    } else {
                        if (null != mListener) {
                            // Notify the active callbacks interface (the activity, if the
                            // fragment is attached to one) that an item has been selected.
                            mListener.onListFragmentInteraction(mItem);
                        }
                    }

                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!selectable) {
                        mItem.isSelected = !mItem.isSelected;
                        holder.mView.setBackgroundColor(mItem.isSelected ? Color.parseColor("#AA0d71ad") : Color.TRANSPARENT);

                        selectable = true;
                        activity.showReviewedBtn(true);

                        return true;
                    } else {
                        return false;
                    }
                }
            });
        } else {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(mItem);
                    }
                }
            });
        }

        calcSelected();
    }

    private void calcSelected() {

        int count = 0;

        for (Property2 p : mValues) {
            if (p.isSelected)
                count++;
        }

        if (count == 0) {
            selectable = false;
            activity.showReviewedBtn(false);
        } else {
            activity.showReviewedBtn(true);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mapLot;
        public final TextView id;
        public final TextView address;
        public final TextView type;
        public final TextView distance;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mapLot = (TextView) view.findViewById(R.id.maplot);
            id = (TextView) view.findViewById(R.id.id);
            address = (TextView) view.findViewById(R.id.address);
            type = (TextView) view.findViewById(R.id.type);
            distance = (TextView) view.findViewById(R.id.dist);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + id.getText() + "'";
        }
    }
}
