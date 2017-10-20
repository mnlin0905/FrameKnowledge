package com.knowledge.mnlin.frame.base;

/**
 * 功能----基础中间键,mvp模式
 * <p>
 * Created by MNLIN on 2017/9/22.
 */

public abstract class BasePresenter<T extends BaseActivity> {
   protected T mView;
}
