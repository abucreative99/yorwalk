package com.aabello.iwalk.ui;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.aabello.iwalk.model.db.App;
import com.aabello.iwalk.ui.bottomnavigation.HomeFragment;
import com.aabello.iwalk.ui.bottomnavigation.ReportFragment;
import com.aabello.iwalk.ui.bottomnavigation.WalkTestFragment;
import com.aabello.iwalk.ui.dialog.AboutDialogFragment;
import com.aabello.iwalk.ui.dialog.DisclaimerDialogFragment;
import com.aabello.iwalk.R;
import com.aabello.iwalk.model.db.DaoSession;


public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final boolean isRegistered = false;

        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        if(daoSession.getUserDao().loadAll().isEmpty()){
            new AlertDialog.Builder(this).setCancelable(false)
                    .setTitle(R.string.disclaimer_dialog_title)
                    .setMessage(R.string.disclaimer_dialog_message)
                    .setNegativeButton(R.string.reject_button_label, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Process.killProcess(Process.myPid());
                            System.exit(1);
                        }
                    }).setPositiveButton(R.string.accept_button_label, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showUserDataActivity();
                }
            }).show().setCanceledOnTouchOutside(false);
        }else {

            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            loadHomePageFragment();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment clickedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    clickedFragment = HomeFragment.newInstance();
                    break;
                case R.id.navigation_walktest:
                    clickedFragment = WalkTestFragment.newInstance();
                    break;
                case R.id.navigation_reports:
                    clickedFragment = ReportFragment.newInstance();
                    break;
            }

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content, clickedFragment);
            transaction.addToBackStack("ClickedFragment");
            transaction.commit();
            return true;
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Handle item selection
        switch (item.getItemId()) {
            case R.id.targetOption:
                startTargetActivity();
                return true;
            case R.id.disclaimerOption:
                showDisclaimerDialog();
                return true;
            case R.id.aboutOption:
                showAboutDialog();
                return true;
            case R.id.userData:
                showUserDataActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDisclaimerDialog() {
        DisclaimerDialogFragment disclaimerFragment = new DisclaimerDialogFragment();
        disclaimerFragment.show(getFragmentManager(), "disclaimer_dialog");
    }

    private void showAboutDialog() {
        AboutDialogFragment aboutFragment = new AboutDialogFragment();
        aboutFragment.show(getFragmentManager(), "about_dialog");
    }

    private void showUserDataActivity() {
        Intent intent = new Intent(this, UserDataActivity.class);
        startActivity(intent);
    }

    private void startTargetActivity() {
        Intent intent = new Intent(this, TargetActivity.class);
        startActivity(intent);
    }

    private void loadHomePageFragment(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content, HomeFragment.newInstance());
        transaction.commit();
    }
}
