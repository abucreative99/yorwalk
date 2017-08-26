package com.aabello.iwalk.ui.bottomnavigation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aabello.iwalk.model.db.App;
import com.aabello.iwalk.model.db.DailyActivity;
import com.aabello.iwalk.model.db.User;
import com.aabello.iwalk.R;
import com.aabello.iwalk.model.db.DailyActivityDao;
import com.aabello.iwalk.model.db.DaoSession;
import com.aabello.iwalk.model.db.UserDao;
import com.aabello.iwalk.util.Notifier;

import org.greenrobot.greendao.query.Query;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeFragment extends Fragment implements SensorEventListener{

    @BindView(R.id.stepCount) TextView mStepCount;
    @BindView(R.id.dailyProgressBar) ProgressBar dailyProgressBar;
    @BindView(R.id.ratingBar) RatingBar mRatingBar;

    private static final String TAG = HomeFragment.class.getSimpleName();
    private SensorManager mSensorManager;
    private static double initialStepCount = 0;
    private static boolean initial = true;
    private static double stepCountValue = 0;
    private User mUser;
    private DailyActivityDao mActivityDao;
    private UserDao mUserDao;
    private Query<DailyActivity> mDailyActivityQuery;
    private boolean isTargetMet = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);

        DaoSession daoSession = ((App) getActivity().getApplication()).getDaoSession();
        UserDao userDao = daoSession.getUserDao();
        mActivityDao = daoSession.getDailyActivityDao();
        mUserDao = daoSession.getUserDao();
        mUser = userDao.loadAll().get(0);

        mDailyActivityQuery = mActivityDao.queryBuilder().build();

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        startSensor();
        mRatingBar.setClickable(false);
        mRatingBar.setPressed(false);
        setRating((int) Math.round(getValueAsPercentage((int) stepCountValue,
                mDailyActivityQuery.list().get(
                        mDailyActivityQuery.list().size() - 1).getDailyTarget())));

        return view;
    }

    public static Fragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    private void startSensor() {

        Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null){
            mSensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        }else{
            Toast.makeText(getActivity(), "Sensor not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(isNewDay() || mDailyActivityQuery.list().isEmpty()){
            if(isLastDayOfTheWeek()){
                calculateWeeklyAverageAndSetTarget();
                Notifier.notify(2, getActivity(), R.drawable.ic_iwalk_notification,
                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round),
                        "Kindly perform a six-minutes working test today",
                        "Happy to know how much you improved?");
            }
            initialStepCount = event.values[0];
            addDailyActivityData();
        }

        if (initialStepCount == 0) {
            initialStepCount = event.values[0] - mDailyActivityQuery.list().get(
                    mDailyActivityQuery.list().size() - 1).getDailyStepCount();
        }

        stepCountValue = (event.values[0] - initialStepCount);

        if (!mDailyActivityQuery.list().isEmpty()) {
            updateDailyActivityData();
        }

        mStepCount.setText(String.valueOf((int) (stepCountValue)));
        int percentage = (int) Math.round(getValueAsPercentage((int) stepCountValue,
                mDailyActivityQuery.list().get(
                        mDailyActivityQuery.list().size() - 1).getDailyTarget()));

        dailyProgressBar.setProgress(percentage);
        setRating(percentage);
        if(isTargetMet){
            Notifier.notify(1, getActivity(), R.drawable.ic_iwalk_notification, BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher_round), "You reached your Target Today", "Nice Work");
            isTargetMet=false;
        }
    }

    private void calculateWeeklyAverageAndSetTarget() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDailyActivityQuery.list().get(mDailyActivityQuery.list().size() - 1).getDate());

        int weekOfCalendarMonth = calendar.get(Calendar.WEEK_OF_MONTH);

        List<DailyActivity> dailyActivities = getListOfDailyActivityGroupedByWeeks().get(weekOfCalendarMonth);
        int weekAverage = (int) Math.round(getAverage(dailyActivities));

        int existingTarget = mUser.getTarget();

        if(weekAverage >= existingTarget){
            int newTarget = existingTarget + 500;
            mUser.setTarget(newTarget);
            mUserDao.update(mUser);
            updateDailyActivityData();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


    private void addDailyActivityData(){
        DailyActivity dailyActivity = new DailyActivity();

        dailyActivity.setUser(mUser);
        dailyActivity.setDailyStepCount(0);
        dailyActivity.setDate(new Date());
        dailyActivity.setDailyTarget(mUser.getTarget());
        dailyActivity.setDailyAward(0);
        dailyActivity.setDistance(0);

        mActivityDao.insert(dailyActivity);
    }

    private void updateDailyActivityData(){

        DailyActivity dailyActivity = mDailyActivityQuery.list().get(mDailyActivityQuery.list().size() - 1);

        dailyActivity.setDailyTarget(mUser.getTarget());
        dailyActivity.setDailyStepCount(stepCountValue);

        mActivityDao.update(dailyActivity);
    }

    private boolean isNewDay(){

        Calendar nowTime = Calendar.getInstance();
        nowTime.setTimeInMillis(System.currentTimeMillis());
        int nowDayOfMonth = nowTime.get(Calendar.DAY_OF_MONTH);

        if(!mDailyActivityQuery.list().isEmpty()) {
            Calendar savedTime = Calendar.getInstance();
            savedTime.setTime(mDailyActivityQuery.list().get(mDailyActivityQuery.list().size() - 1).getDate());
            int savedDayOfMonth = savedTime.get(Calendar.DAY_OF_MONTH);

            if (nowDayOfMonth == savedDayOfMonth) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private TreeMap<Integer, List<DailyActivity>> getListOfDailyActivityGroupedByWeeks(){

        TreeMap<Integer, List<DailyActivity>> dailyActivityListTreeMap = new TreeMap<>();

        for(int i = 0; i < mDailyActivityQuery.list().size(); i++){
            List<DailyActivity> dailyActivities = new ArrayList<>();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mDailyActivityQuery.list().get(i).getDate());

            int weekOfCalendarMonth = calendar.get(Calendar.WEEK_OF_MONTH);

            for(DailyActivity dailyActivity : mDailyActivityQuery.list()){
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

    private double getAverage(List<DailyActivity> dailyActivities){

        double total = 0;

        for(DailyActivity dailyActivity : dailyActivities){
            total += dailyActivity.getDailyStepCount();
        }

        return total/dailyActivities.size();
    }

    private boolean isLastDayOfTheWeek(){

        if(!mDailyActivityQuery.list().isEmpty()) {
            DateTime dateTime = new DateTime(mDailyActivityQuery.list().get(mDailyActivityQuery.list().size() - 1).getDate());

            if (dateTime.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                return true;
            } else {
                return false;
            }
        }
        return false;

    }

    private double getValueAsPercentage(int value, int total){
        if(total == 0) {
            return 0;
        }else if(value == total){
            isTargetMet = true;
            return 100;
        }else if(value >= total){
            return 100;
        }else{
            return (value * 100) / total;
        }
    }

    private void setRating(int percentage){

        if(percentage >= 20 && percentage <= 39){
            mRatingBar.setVisibility(View.VISIBLE);
            mRatingBar.setRating(1.0f);
        }else if(percentage >= 40 && percentage <= 59) {
            mRatingBar.setRating(2.0f);
        }else if(percentage >= 60 && percentage <= 79){
            mRatingBar.setRating(3.0f);
        }else if(percentage >= 80 && percentage <= 99){
            mRatingBar.setRating(4.0f);
        }else if(percentage >= 100){
            mRatingBar.setRating(5.0f);
        }else{
            mRatingBar.setVisibility(View.INVISIBLE);
        }
    }

}
