package com.aabello.iwalk.model;


public class WalkTestPage {

    private int stepTitleId;
    private int pageTitleId;
    private int pageTextId;
    private int imageId;
    private boolean isFinalPage;

    public WalkTestPage(int stepTitleId, int pageTitleId, int textId, int imageId, boolean isFinalPage) {
        this.stepTitleId = stepTitleId;
        this.pageTitleId = pageTitleId;
        this.pageTextId = textId;
        this.imageId = imageId;
        this.isFinalPage = isFinalPage;
    }



    public int getStepTitleId() {
        return stepTitleId;
    }

    public void setStepTitleId(int stepTitleId) {
        this.stepTitleId = stepTitleId;
    }

    public int getPageTitleId() {
        return pageTitleId;
    }

    public void setPageTitleId(int pageTitleId) {
        this.pageTitleId = pageTitleId;
    }

    public int getPageTextId() {
        return pageTextId;
    }

    public void setPageTextId(int pageTextId) {
        this.pageTextId = pageTextId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public boolean isFinalPage() {
        return isFinalPage;
    }

}
