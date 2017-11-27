package com.knowledge.mnlin.frame.presenter;


import com.knowledge.mnlin.frame.base.BasePresenter;
import com.knowledge.mnlin.frame.contract.HelpContract;
import com.knowledge.mnlin.frame.activity.HelpActivity;

import javax.inject.Inject;

public class HelpPresenter extends BasePresenter<HelpActivity> implements HelpContract.Presenter{
    @Inject
    public HelpPresenter() {}

}