package com.secretpackage.inspector.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.secretpackage.inspector.R;
import com.secretpackage.inspector.model.Visit;
import com.secretpackage.inspector.util.Constants;
import com.secretpackage.inspector.util.DateUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VisitsRVAdapter extends RecyclerView.Adapter<VisitsRVAdapter.ViewHolder> {

    private final List<Visit> mValues;

    public VisitsRVAdapter(List<Visit> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_visit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Visit mItem = mValues.get(position);

        holder.tv_date.setText(DateUtils.fromRawStringToString(mItem.getVisit_date()));
        holder.tv_type.setText(Constants.Visit_values.get(mItem.getVisit_code()));
        holder.tv_name.setText(mItem.getInspector());

        Log.e("reali", "vis " + mItem.getVisit_code());

//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.tv_date) TextView tv_date;
        @BindView(R.id.tv_type) TextView tv_type;
        @BindView(R.id.tv_name) TextView tv_name;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
//            mapLot = (TextView) view.findViewById(R.id.maplot);
//            distance = (TextView) view.findViewById(R.id.dist);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tv_name.getText() + "'";
        }
    }
}
