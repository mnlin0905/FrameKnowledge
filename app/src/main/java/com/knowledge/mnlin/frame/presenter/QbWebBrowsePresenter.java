package com.knowledge.mnlin.frame.presenter;


import com.knowledge.mnlin.frame.base.BasePresenter;
import com.knowledge.mnlin.frame.contract.QbWebBrowseContract;
import com.knowledge.mnlin.frame.activity.QbWebBrowseActivity;

import javax.inject.Inject;

public class QbWebBrowsePresenter extends BasePresenter<QbWebBrowseActivity> implements QbWebBrowseContract.Presenter{
    @Inject
    public QbWebBrowsePresenter() {}

}