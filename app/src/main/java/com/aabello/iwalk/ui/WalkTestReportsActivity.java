package com.aabello.iwalk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.aabello.iwalk.R;
import com.aabello.iwalk.adapter.WalkTestAdapter;
import com.aabello.iwalk.model.db.App;
import com.aabello.iwalk.model.db.DaoSession;
import com.aabello.iwalk.model.db.WalkTest;

import org.greenrobot.greendao.query.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aabello.iwalk.model.db.WalkTestDao.Properties;

public class WalkTestReportsActivity extends AppCompatActivity {

    private Query<WalkTest> mWalkTestQuery;
    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.showGraph) LinearLayout mShowGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_test_reports);
        ButterKnife.bind(this);

        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        mWalkTestQuery = daoSession.getWalkTestDao().queryBuilder().orderDesc(Properties.Date).build();

        WalkTestAdapter adapter = new WalkTestAdapter(this, mWalkTestQuery.list());
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

    }

    @OnClick(R.id.showGraph)
    public void showCharts(View view){
        Intent intent = new Intent(this, WalkTestChartActivity.class);
        startActivity(intent);
    }




}
