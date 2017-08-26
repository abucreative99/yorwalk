package com.aabello.iwalk.ui.bottomnavigation;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aabello.iwalk.model.WalkTestPage;
import com.aabello.iwalk.model.WalkTestSteps;
import com.aabello.iwalk.ui.WalkTestMonitorActivity;
import com.aabello.iwalk.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalkTestFragment extends Fragment {


    private static final String TAG = WalkTestFragment.class.getSimpleName();
    private static final int LAST_PAGE = 4;
    private WalkTestSteps mWalkTestSteps;
    private int pageNumber = 0;

    @BindView(R.id.wtStepTitleTextView) TextView stepTitleTextView;
    @BindView(R.id.wtPageTitleTextView) TextView pageTitleTextView;
    @BindView(R.id.wtPageTextView) TextView pageTextView;
    @BindView(R.id.wtSkipTextView) TextView skipTextView;
    @BindView(R.id.wtPageButton) Button pageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.walktest_fragment, container, false);
        ButterKnife.bind(this, view);

        mWalkTestSteps = new WalkTestSteps();
        loadWalkTestPage(pageNumber);
        return view;
    }

    public static Fragment newInstance() {
        WalkTestFragment walkTestFragment = new WalkTestFragment();
        return walkTestFragment;
    }

    private void loadWalkTestPage(int pageNumber) {
        final int stepNumber = pageNumber + 1;
        final WalkTestPage page = mWalkTestSteps.getPage(pageNumber);

        if(page.isFinalPage()){

            String stepTitleText = getString(page.getStepTitleId());

            stepTitleText = String.format(stepTitleText, (stepNumber), mWalkTestSteps.getTotalPages());
            stepTitleTextView.setText(stepTitleText);
            pageTitleTextView.setText(page.getPageTitleId());
            pageTextView.setText(page.getPageTextId());
            pageButton.setText(R.string.start_button_title);
            skipTextView.setVisibility(View.INVISIBLE);
            this.pageNumber = LAST_PAGE;
        }
        else{
            String stepTitleText = getString(page.getStepTitleId());
            stepTitleText = String.format(stepTitleText, stepNumber, mWalkTestSteps.getTotalPages());
            stepTitleTextView.setText(stepTitleText);
            pageTitleTextView.setText(page.getPageTitleId());
            pageTextView.setText(page.getPageTextId());
            pageButton.setText(R.string.next_button_title);

        }
    }

    @OnClick (R.id.wtSkipTextView)
    public void hideStepsInstruction(View view){
        loadWalkTestPage(mWalkTestSteps.getFinalPage());
    }

    @OnClick (R.id.wtPageButton)
    public void startWalkTestMonitor() {
        final WalkTestPage page = mWalkTestSteps.getPage(pageNumber);

        if(page.isFinalPage() ) {
            Intent intent = new Intent(getActivity(), WalkTestMonitorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }else{
            if(pageNumber != LAST_PAGE) {
                pageNumber = pageNumber + 1;
            }
            loadWalkTestPage(pageNumber);
        }
    }

}
