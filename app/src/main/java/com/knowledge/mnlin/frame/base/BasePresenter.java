package com.knowledge.mnlin.frame.base;

import com.knowledge.mnlin.frame.retrofit.HttpInterface;

import javax.inject.Inject;

/**
 * 功能----基础中间键,mvp模式
 * <p>
 * Created by MNLIN on 2017/9/22.
 */

public class BasePresenter<T extends BaseActivity> {
    protected T mView;

    @Inject
    public HttpInterface httpInterface;

    @Inject
    public BasePresenter() {

    }
}
