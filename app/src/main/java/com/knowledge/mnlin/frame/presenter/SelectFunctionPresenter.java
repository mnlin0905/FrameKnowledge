package com.knowledge.mnlin.frame.presenter;

import com.knowledge.mnlin.frame.activity.SelectFunctionActivity;
import com.knowledge.mnlin.frame.base.BasePresenter;
import com.knowledge.mnlin.frame.contract.SelectFunctionContract;

import javax.inject.Inject;

/**
 * 功能----功能选择界面,首页,中间键
 * <p>
 * Created by MNLIN on 2017/9/22.
 */

public class SelectFunctionPresenter extends BasePresenter<SelectFunctionActivity> implements SelectFunctionContract.Presenter  {
    @Inject
    public SelectFunctionPresenter(){
    }
}
