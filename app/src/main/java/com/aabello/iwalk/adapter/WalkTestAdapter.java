package com.aabello.iwalk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aabello.iwalk.R;
import com.aabello.iwalk.model.db.WalkTest;

import java.text.SimpleDateFormat;
import java.util.List;


public class WalkTestAdapter extends RecyclerView.Adapter<WalkTestAdapter.WalkTestViewHolder> {

    private Context context;
    private List<WalkTest> mWalkTests;

    public WalkTestAdapter(Context context, List<WalkTest> walkTests) {
        this.context = context;
        mWalkTests = walkTests;
    }

    public class WalkTestViewHolder extends RecyclerView.ViewHolder{

        public TextView mDateTimeLabel;
        public TextView mStepsCountLabel;
        public TextView mDistanceLabel;
        public TextView mPainFreeDistanceLabel;

        public WalkTestViewHolder(View itemView) {
            super(itemView);

            mDateTimeLabel = (TextView) itemView.findViewById(R.id.dateTimeLabel);
            mDistanceLabel = (TextView) itemView.findViewById(R.id.distanceLabel);
            mPainFreeDistanceLabel = (TextView) itemView.findViewById(R.id.painFreeDistanceLabel);
        }

        public void bindWalkTest(WalkTest walktest){
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d");
            mDateTimeLabel.setText(formatter.format(walktest.getDate()));
            mDistanceLabel.setText(String.format("%.2f", walktest.getDistance()));
            mPainFreeDistanceLabel.setText(String.format("%.2f", walktest.getPainFreeDistance()));
        }
    }

    @Override
    public WalkTestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.walktest_report_list_item, parent, false);
        WalkTestViewHolder viewHolder = new WalkTestViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WalkTestViewHolder holder, int position) {
        holder.bindWalkTest(mWalkTests.get(position));
    }

    @Override
    public int getItemCount() {
        return mWalkTests.size();
    }


}

