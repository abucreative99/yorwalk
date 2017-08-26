package com.aabello.iwalk.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aabello.iwalk.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalkTestSummaryActivity extends AppCompatActivity {

    @BindView(R.id.stepsValue) TextView mStepsValue;
    @BindView(R.id.distanceValue) TextView mDistanceValue;
    @BindView(R.id.painFreeDistanceValue) TextView mPainFreeDistanceValue;
    private int noOfSteps;
    private double painFreeCount;
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_test_summary);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        noOfSteps = intent.getIntExtra(getString(R.string.key_no_of_steps), 0);
        distance = intent.getDoubleExtra("distance", 0.0);
        painFreeCount = intent.getDoubleExtra(getString(R.string.key_pain_free_distance), 0.0F);
        displaySummary();
    }

    public void displaySummary() {
        if(noOfSteps != 0 && distance != 0.0){
            mStepsValue.setText((noOfSteps + 1) + " Steps");
            mDistanceValue.setText(String.format("%.2f", distance) + " m");
            mPainFreeDistanceValue.setText(String.format("%.2f", painFreeCount) + " m" + "(PFWD)");
        }else{
            mStepsValue.setText("No steps detected, the walk will not be saved");
        }
    }


    @OnClick (R.id.viewReportsButton)
    public void showReportsActivity(View view){
        Intent intent = new Intent(this, WalkTestReportsActivity.class);
        startActivity(intent);
    }

}
