package com.knowledge.mnlin.frame.presenter;


import com.knowledge.mnlin.frame.base.BasePresenter;
import com.knowledge.mnlin.frame.contract.HttpRequestSimulateContract;
import com.knowledge.mnlin.frame.activity.HttpRequestSimulateActivity;

import javax.inject.Inject;

public class HttpRequestSimulatePresenter extends BasePresenter<HttpRequestSimulateActivity> implements HttpRequestSimulateContract.Presenter{
    @Inject
    public HttpRequestSimulatePresenter() {}

}