package com.aabello.iwalk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aabello.iwalk.model.db.User;
import com.aabello.iwalk.R;
import com.aabello.iwalk.model.db.App;
import com.aabello.iwalk.model.db.DaoSession;
import com.aabello.iwalk.model.db.WalkTest;
import com.aabello.iwalk.util.ReportType;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalkTestChartActivity extends AppCompatActivity {

    private static final String TAG = WalkTestChartActivity.class.getSimpleName();

    @BindView(R.id.walkTestDistanceGraphView) GraphView mDistanceGraphView;
    @BindView(R.id.walkTestPainFreeDistanceGraphView) GraphView mPainFreeDistanceGraphView;
    @BindView(R.id.wtWeeklyButton) Button mWeeklyButton;
    @BindView(R.id.wtMonthlyButton) Button mMonthlyButton;
    private User user;
    private List<WalkTest> mWalkTests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_test_chart);
        ButterKnife.bind(this);

        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        user = daoSession.getUserDao().loadAll().get(0);

        mWalkTests = user.getWalkTests();

        if(!mWalkTests.isEmpty()) {
            plotWeeklyDistanceGraph();
            plotWeeklyPainFreeDistaceGraph();
            toggleButton(ReportType.WEEKLY);
        }
    }

    @OnClick(R.id.wtWeeklyButton)
    public void displayWeeklyGraph(View view){
        toggleButton(ReportType.WEEKLY);
        plotWeeklyDistanceGraph();
        plotWeeklyPainFreeDistaceGraph();
    }

    @OnClick(R.id.wtMonthlyButton)
    public void displayMonthlyGraph(View view){
        toggleButton(ReportType.MONTHLY);
        plotMonthlyDistanceGraph();
        plotMonthlyPainFreeDistaceGraph();
    }

    private void plotWeeklyPainFreeDistaceGraph() {
        mPainFreeDistanceGraphView.removeAllSeries();
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(getPainFreeDistanceDataSet());

        series.setSpacing(50);
        mPainFreeDistanceGraphView.addSeries(series);
    }

    private void plotWeeklyDistanceGraph() {
        mDistanceGraphView.removeAllSeries();
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(getDistanceDataSet());

        series.setSpacing(50);
        mDistanceGraphView.addSeries(series);
    }


    private void plotMonthlyPainFreeDistaceGraph() {
        mPainFreeDistanceGraphView.removeAllSeries();

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(getPainFreeDistanceDataSet());

        series.setDrawDataPoints(true);
        mPainFreeDistanceGraphView.addSeries(series);

        mPainFreeDistanceGraphView.getViewport().setScrollable(true);

        mPainFreeDistanceGraphView.getViewport().setMinX(0);

        if(mWalkTests.size() >= 7) {
            mPainFreeDistanceGraphView.getViewport().setMaxX(6);
        }else{
            mPainFreeDistanceGraphView.getViewport().setMaxX(mWalkTests.size() - 1);
        }

        mPainFreeDistanceGraphView.getViewport().setXAxisBoundsManual(true);


        if(mWalkTests.size() >= 7) {
            mPainFreeDistanceGraphView.getGridLabelRenderer().setNumHorizontalLabels(7);
        }else{
            mPainFreeDistanceGraphView.getGridLabelRenderer().setNumHorizontalLabels(mWalkTests.size());
        }

        mPainFreeDistanceGraphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX) {
                    return new SimpleDateFormat("d").format(mWalkTests.get((int) Math.ceil(value)).getDate());
                }else{
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }

    private void plotMonthlyDistanceGraph() {

        mDistanceGraphView.removeAllSeries();

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(getDistanceDataSet());

        series.setDrawDataPoints(true);
        mDistanceGraphView.addSeries(series);

        mDistanceGraphView.getViewport().setScrollable(true);


        mDistanceGraphView.getViewport().setMinX(0);
        if(mWalkTests.size() >= 7) {
            mDistanceGraphView.getViewport().setMaxX(6);
        }else{
            mDistanceGraphView.getViewport().setMaxX(mWalkTests.size() - 1);
        }
        mDistanceGraphView.getViewport().setXAxisBoundsManual(true);
        if(mWalkTests.size() >= 7) {
            mDistanceGraphView.getGridLabelRenderer().setNumHorizontalLabels(7);
        }else{
            mDistanceGraphView.getGridLabelRenderer().setNumHorizontalLabels(mWalkTests.size());
        }

        mDistanceGraphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {


                if(isValueX) {
                    return new SimpleDateFormat("d").format(mWalkTests.get((int) Math.ceil(value)).getDate());
                }else{
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }

    public DataPoint [] getDistanceDataSet(){
        DataPoint [] dataPoints = new DataPoint[mWalkTests.size()];
        DecimalFormat df = new DecimalFormat("####0.00");
        for(int i = 0; i < mWalkTests.size(); i++){
            dataPoints[i] = new DataPoint(i, Double.parseDouble(df.format(mWalkTests.get(i).getDistance())));//indexOutOfBound
        }
        return dataPoints;
    }

    public DataPoint [] getPainFreeDistanceDataSet(){
        DataPoint [] dataPoints = new DataPoint[mWalkTests.size()];
        DecimalFormat df = new DecimalFormat("####0.00");
        for(int i = 0; i < mWalkTests.size(); i++){
            dataPoints[i] = new DataPoint(i, Double.parseDouble(df.format(mWalkTests.get(i).getPainFreeDistance())));
        }
        return dataPoints;
    }

    private void toggleButton(ReportType reportType){

        switch(reportType) {
            case WEEKLY:
                mWeeklyButton.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                mWeeklyButton.setTextColor(getResources().getColor(R.color.colorWhite));
                mMonthlyButton.setBackground(getResources().getDrawable(R.drawable.button_border));
                mMonthlyButton.setTextColor(getResources().getColor(R.color.colorOrange));
                break;
            case MONTHLY:
                mWeeklyButton.setBackground(getResources().getDrawable(R.drawable.button_border));
                mWeeklyButton.setTextColor(getResources().getColor(R.color.colorOrange));
                mMonthlyButton.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                mMonthlyButton.setTextColor(getResources().getColor(R.color.colorWhite));
                break;
        }

    }
}