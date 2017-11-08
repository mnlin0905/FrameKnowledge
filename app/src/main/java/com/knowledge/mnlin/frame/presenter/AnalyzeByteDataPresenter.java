package com.knowledge.mnlin.frame.presenter;


import com.knowledge.mnlin.frame.base.BasePresenter;
import com.knowledge.mnlin.frame.contract.AnalyzeByteDataContract;
import com.knowledge.mnlin.frame.activity.AnalyzeByteDataActivity;

import javax.inject.Inject;

public class AnalyzeByteDataPresenter extends BasePresenter<AnalyzeByteDataActivity> implements AnalyzeByteDataContract.Presenter{
    @Inject
    public AnalyzeByteDataPresenter() {}

}