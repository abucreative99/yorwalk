package com.aabello.iwalk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.aabello.iwalk.model.db.App;
import com.aabello.iwalk.model.db.DailyActivity;
import com.aabello.iwalk.model.db.DailyActivityDao;
import com.aabello.iwalk.model.db.User;
import com.aabello.iwalk.R;
import com.aabello.iwalk.model.db.DaoSession;
import com.aabello.iwalk.model.db.UserDao;

import org.greenrobot.greendao.query.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TargetActivity extends AppCompatActivity {

    @BindView(R.id.targetText) EditText targetText;
    private UserDao mUserDao;
    private DailyActivityDao mActivityDao;
    private Query<User> mUserQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        ButterKnife.bind(this);


        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        mUserDao = daoSession.getUserDao();
        mActivityDao = daoSession.getDailyActivityDao();
        mUserQuery = mUserDao.queryBuilder().build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        targetText.setText(String.valueOf(mUserQuery.list().get(0).getTarget()));
    }

    @OnClick(R.id.saveTargetButton)
    public void saveTarget(){
        User user = mUserQuery.list().get(0);


        user.setTarget(Integer.parseInt(targetText.getText().toString()));


        mUserDao.update(user);
        updateDailyTarget(user);
        Toast.makeText(this, "Target updated successfully", Toast.LENGTH_LONG).show();
        finish();
    }

    public void updateDailyTarget(User user){
        DailyActivity dailyActivity = user.getDailyActivities().get(user.getDailyActivities().size() - 1);
        dailyActivity.setDailyTarget(user.getTarget());

        mActivityDao.update(dailyActivity);
    }
}
