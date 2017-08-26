package com.aabello.iwalk.model;


import com.aabello.iwalk.R;

public class WalkTestSteps {

    private WalkTestPage[] mPages;
    private final int LAST_PAGE = 4;


    public WalkTestSteps() {

        mPages = new WalkTestPage[5];

        mPages[0] = new WalkTestPage(R.string.step_title, R.string.page0_title, R.string.page0_text,
                R.drawable.ic_dashboard_dark_green_24dp, false);
        mPages[1] = new WalkTestPage(R.string.step_title, R.string.page1_title, R.string.page1_text,
                R.drawable.ic_dashboard_dark_green_24dp, false);
        mPages[2] = new WalkTestPage(R.string.step_title, R.string.page2_title, R.string.page2_text,
                R.drawable.ic_dashboard_dark_green_24dp, false);
        mPages[3] = new WalkTestPage(R.string.step_title, R.string.page3_title, R.string.page3_text,
                R.drawable.ic_dashboard_dark_green_24dp, false);
        mPages[4] = new WalkTestPage(R.string.step_title, R.string.page4_title, R.string.page4_text,
                R.drawable.ic_dashboard_dark_green_24dp, true);
    }

    public WalkTestPage getPage(int pageNumber){

        if (pageNumber >= mPages.length){
            pageNumber = 0;
        }


        return mPages[pageNumber];
    }

    public int getTotalPages(){
        return mPages.length;
    }

    public int getFinalPage(){
        return LAST_PAGE;
    }
}
