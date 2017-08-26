package com.aabello.iwalk.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aabello.iwalk.model.db.App;
import com.aabello.iwalk.model.db.User;
import com.aabello.iwalk.R;
import com.aabello.iwalk.model.db.DaoSession;
import com.aabello.iwalk.model.db.WalkTest;
import com.aabello.iwalk.model.db.WalkTestDao;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalkTestMonitorActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    public static final String TAG = WalkTestMonitorActivity.class.getSimpleName();

    @BindView(R.id.countDownTimer) TextView mCountDownTimerLabel;
    @BindView(R.id.fiveSecondsTextView) TextView mFiveSecondsTextView;
    @BindView(R.id.painButton) Button mPainButton;
    @BindView(R.id.cancelButton) Button mCancelButton;
    private final String TIME_FORMAT = "%02d:%02d";
    private static final long SIX_MINUTES = 360000;
    private static final long COUNTDOWN_INTERVAL = 1000;
    private boolean stepInitial = true;
    private boolean locationInitial = true;
    private float startCount, finishCount, stepCount;
    private double painFreeDistance;
    private int noOfSteps;
    private boolean firstPainButtonPress = true;
    private SensorManager mSensorManager;
    private LocationManager mLocationManager;
    private Location mLocation;
    private User mUser;
    private WalkTestDao mWalkTestDao;
    public static final int REQUEST_LOCATION = 17;
    private double mLatitudeA, mLatitudeB;
    private double mLongitudeA, mLongitudeB;
    private double mDistance;
    private CountDownTimer mCountDownTimer;
    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_test_monitor);
        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        mWalkTestDao = daoSession.getWalkTestDao();
        mUser = daoSession.getUserDao().loadAll().get(0);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mDistance = 0;

        countDownBeforeStart();
    }

    private void loadSummaryActivity() {
        Intent intent = new Intent(this, WalkTestSummaryActivity.class);
        intent.putExtra(getResources().getString(R.string.key_no_of_steps), noOfSteps);
        intent.putExtra("distance", mDistance);
        intent.putExtra(getResources().getString(R.string.key_pain_free_distance), painFreeDistance);
        startActivity(intent);
    }

    private void registerLocationUpdates(){
        if(checkLocationPermission() && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //Request location updates:
                mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        }
    }

    private void startStepSensor() {

        Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            mSensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not available", Toast.LENGTH_LONG).show();
        }
    }

    private void stopStepSensor(){
        Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mSensorManager.unregisterListener(this);
    }

    public void unRegisterLocationUpdates(){
      mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocalVoiceInteractionStopped() {
        super.onLocalVoiceInteractionStopped();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(stepInitial) {
            startCount = event.values[0];
            stepInitial = false;
        }

        stepCount = event.values[0];
    }


    private void countDownToSixMinutes(){
        mCountDownTimer = new CountDownTimer(SIX_MINUTES, COUNTDOWN_INTERVAL) {

            public void onTick(long millisUntilFinished) {

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                        TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished));
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));


                mCountDownTimerLabel.setText(String.format(TIME_FORMAT, minutes, seconds));
            }

            public void onFinish() {
                stopStepSensor();
                mCountDownTimerLabel.setText("00:00");
                finishCount = stepCount;
                noOfSteps = (int) (finishCount - startCount);
                Toast.makeText(WalkTestMonitorActivity.this, "You have completed the test", Toast.LENGTH_SHORT).show();
                unRegisterLocationUpdates();
                if(noOfSteps != 0) {
                    addWalkTestData();
                }
                loadSummaryActivity();

            }
        };

        mCountDownTimer.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @OnClick (R.id.cancelButton)
    public void cancelWalkTest(){
        showConfirmDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick (R.id.painButton)
    public void registerPain(){
        if(firstPainButtonPress){
            painFreeDistance = mDistance;
            firstPainButtonPress = false;
        }
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.confirm_dialog_title)
                .setMessage("Are you sure?")
                .setNegativeButton(R.string.no_button_label, null)
                .setPositiveButton(R.string.yes_button_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCountDownTimer.cancel();
                        stopStepSensor();
                        unRegisterLocationUpdates();
                        Intent intent = new Intent(WalkTestMonitorActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }).create().show();
    }

    private void addWalkTestData(){
        WalkTest walkTest = new WalkTest();

        walkTest.setUser(mUser);
        walkTest.setDate(new Date());
        walkTest.setStepCount(noOfSteps);
        walkTest.setDistance(mDistance);
        walkTest.setPainFreeDistance(painFreeDistance);

        mWalkTestDao.insert(walkTest);
    }

    @Override
    public void onLocationChanged(Location location) {

        if(locationInitial) {
            //the distance initially is zero.
            locationInitial = false;
            mLocation = location;
        }else {
            mLatitudeA = mLocation.getLatitude();
            mLongitudeA = mLocation.getLongitude();
            mLatitudeB = location.getLatitude();
            mLongitudeB = location.getLongitude();
            float[] results = new float[1];
            Location.distanceBetween(mLatitudeA, mLongitudeA, mLatitudeB, mLongitudeB, results);
            mDistance += results[0];
            mLocation = location;
        }

    }

    public boolean checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

                new AlertDialog.Builder(this)
                        .setMessage("We need to access your location in order to get accurate distance of your walk.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(WalkTestMonitorActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                            }
                        }).create().show();

            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //intentionally left blank
    }

    @Override
    public void onProviderEnabled(String provider) {
        //intentionally left blank
    }

    @Override
    public void onProviderDisabled(String provider) {
        //intentionally left blank
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registerLocationUpdates();
                } else {
                    Toast.makeText(this, "The test was cancelled because location permission was not granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
        
    }

    private void countDownBeforeStart(){
        new CountDownTimer(3000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));

                mFiveSecondsTextView.setText(String.format("%s", (int) seconds));
                mFiveSecondsTextView.setTextColor(getResources().getColor(R.color.colorOrange));
                mFiveSecondsTextView.animate();
                mPainButton.setVisibility(View.INVISIBLE);
                mCancelButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFinish() {
                mVibrator.vibrate(1000);
                ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 90);
                toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 2000);
                mPainButton.setVisibility(View.VISIBLE);
                mCancelButton.setVisibility(View.VISIBLE);
                registerLocationUpdates();
                startStepSensor();
                countDownToSixMinutes();
            }
        }.start();
    }
}
