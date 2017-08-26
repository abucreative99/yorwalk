package com.aabello.iwalk.model.db;

import android.app.Application;

import com.aabello.iwalk.model.db.DaoMaster.DevOpenHelper;

import org.greenrobot.greendao.database.Database;


public class App extends Application {

    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DevOpenHelper helper = new DevOpenHelper(this, "iwalk-db");
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}
