package com.knowledge.mnlin.frame.presenter;


import com.knowledge.mnlin.frame.base.BasePresenter;
import com.knowledge.mnlin.frame.contract.QRUtilContract;
import com.knowledge.mnlin.frame.activity.QRUtilActivity;

import javax.inject.Inject;

public class QRUtilPresenter extends BasePresenter<QRUtilActivity> implements QRUtilContract.Presenter{
    @Inject
    public QRUtilPresenter() {}

}