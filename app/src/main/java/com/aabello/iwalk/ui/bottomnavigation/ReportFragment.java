package com.aabello.iwalk.ui.bottomnavigation;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aabello.iwalk.model.db.App;
import com.aabello.iwalk.model.db.DailyActivity;
import com.aabello.iwalk.R;
import com.aabello.iwalk.model.db.DailyActivityDao;
import com.aabello.iwalk.model.db.DaoSession;
import com.aabello.iwalk.ui.WalkTestReportsActivity;
import com.aabello.iwalk.util.ReportType;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.greenrobot.greendao.query.Query;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportFragment extends Fragment {

    @BindView(R.id.weeklyButton) Button mWeeklyButton;
    @BindView(R.id.monthlyButton) Button mMonthlyButton;
    @BindView(R.id.stepsGraphView) GraphView mStepsGraphView;
    private DailyActivityDao mActivityDao;
    private Query<DailyActivity> mActivityQuery;

    public static Fragment newInstance(){
        ReportFragment reportFragment = new ReportFragment();
        return reportFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_fragment, container, false);
        ButterKnife.bind(this, view);

        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        mActivityDao = daoSession.getDailyActivityDao();
        mActivityQuery = mActivityDao.queryBuilder().build();

        if(!mActivityQuery.list().isEmpty()) {
            toggleButton(ReportType.WEEKLY);
            plotWeeklyStepsGraph();
        }

        return view;
    }

    @OnClick(R.id.weeklyButton)
    public void displayDailyGraph(View view){
        if(!mActivityQuery.list().isEmpty()) {
            toggleButton(ReportType.WEEKLY);
            plotWeeklyStepsGraph();
        }
    }

    @OnClick(R.id.monthlyButton)
    public void displayWeeklyGraph(View view){
        if(!mActivityQuery.list().isEmpty()) {
            toggleButton(ReportType.MONTHLY);
            plotMonthlyStepsGraph();
        }
    }


    private void plotWeeklyStepsGraph() {
        mStepsGraphView.removeAllSeries();


        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(getStepsCountDataSet());

        series.setSpacing(50);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(getResources().getColor(R.color.colorOrange));

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/3, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        mStepsGraphView.addSeries(series);

        mStepsGraphView.getViewport().setMinX(0);
        if(mActivityQuery.list().size() >= 5) {
            mStepsGraphView.getViewport().setMaxX(4);
        }else{
            mStepsGraphView.getViewport().setMaxX(mActivityQuery.list().size() - 1);
        }
        mStepsGraphView.getViewport().setXAxisBoundsManual(true);
        if(mActivityQuery.list().size() >= 5) {
            mStepsGraphView.getGridLabelRenderer().setNumHorizontalLabels(4);
        }else{
            mStepsGraphView.getGridLabelRenderer().setNumHorizontalLabels(mActivityQuery.list().size());
        }

        mStepsGraphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {


                if(isValueX) {
                    return new SimpleDateFormat("dMMM").format(mActivityQuery.list().get((int) Math.ceil(value)).getDate());
                }else{
                    return super.formatLabel(value, isValueX);
                }
            }
        });

    }


    private void plotMonthlyStepsGraph() {
        mStepsGraphView.removeAllSeries();

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getStepsCountDataSet());

        series.setDrawDataPoints(true);

        mStepsGraphView.addSeries(series);
        mStepsGraphView.getViewport().setScrollable(true);


        mStepsGraphView.getViewport().setMinX(0);
        if(mActivityQuery.list().size() >= 7) {
            mStepsGraphView.getViewport().setMaxX(6);
        }else{
            mStepsGraphView.getViewport().setMaxX(mActivityQuery.list().size() - 1);
        }
        mStepsGraphView.getViewport().setXAxisBoundsManual(true);
        if(mActivityQuery.list().size() >= 7) {
            mStepsGraphView.getGridLabelRenderer().setNumHorizontalLabels(7);
        }else{
            mStepsGraphView.getGridLabelRenderer().setNumHorizontalLabels(mActivityQuery.list().size());
        }

        mStepsGraphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {


                if(isValueX && ((int) Math.ceil(value) < mActivityQuery.list().size())) {
                    return new SimpleDateFormat("dMMM").format(mActivityQuery.list().get((int) Math.ceil(value)).getDate());
                }else{
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }

    private void toggleButton(ReportType reportType) {

        switch (reportType) {
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

    public DataPoint [] getStepsCountDataSet(){
        DataPoint [] dataPoints = new DataPoint[mActivityQuery.list().size()];
        DecimalFormat df = new DecimalFormat("####0.00");
        for(int i = 0; i < mActivityQuery.list().size(); i++){
            dataPoints[i] = new DataPoint(i, mActivityQuery.list().get(i).getDailyStepCount());
        }
        return dataPoints;
    }

    @OnClick(R.id.sixMinGraphButton)
    public void showSixMinuteTestList(){
        showWalkTestReportActivity();
    }

    private void showWalkTestReportActivity() {
        Intent intent = new Intent(getActivity(), WalkTestReportsActivity.class);
        startActivity(intent);
    }

    private TreeMap<Integer, List<DailyActivity>> getListOfDailyActivityGroupedByWeek(){

        TreeMap<Integer, List<DailyActivity>> dailyActivityListTreeMap = new TreeMap<>();

        for(int i = 0; i < mActivityQuery.list().size(); i++){
            List<DailyActivity> dailyActivities = new ArrayList<>();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mActivityQuery.list().get(i).getDate());

            int weekOfCalendarMonth = calendar.get(Calendar.WEEK_OF_MONTH);

            for(DailyActivity dailyActivity : mActivityQuery.list()){
                Calendar newCalendar = Calendar.getInstance();
                newCalendar.setTime(dailyActivity.getDate());
                if( weekOfCalendarMonth == newCalendar.get(Calendar.WEEK_OF_MONTH)){
                    dailyActivities.add(dailyActivity);
                }
            }

            dailyActivityListTreeMap.put(weekOfCalendarMonth, dailyActivities);
        }

        return dailyActivityListTreeMap;
    }

    private TreeMap<Integer, List<DailyActivity>> getListOfDailyActivityGroupedByMonth(){

        TreeMap<Integer, List<DailyActivity>> dailyActivityListTreeMap = new TreeMap<>();

        for(int i = 0; i < mActivityQuery.list().size(); i++){
            List<DailyActivity> dailyActivities = new ArrayList<>();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mActivityQuery.list().get(i).getDate());

            int calendarMonth = calendar.get(Calendar.MONTH);

            for(DailyActivity dailyActivity : mActivityQuery.list()){
                Calendar newCalendar = Calendar.getInstance();
                newCalendar.setTime(dailyActivity.getDate());
                if( calendarMonth == newCalendar.get(Calendar.MONTH)){
                    dailyActivities.add(dailyActivity);
                }
            }

            dailyActivityListTreeMap.put(calendarMonth, dailyActivities);
        }

        return dailyActivityListTreeMap;
    }

}
