package com.knowledge.mnlin.frame.dagger.module;

import com.knowledge.mnlin.frame.annotation.PerActivity;
import com.knowledge.mnlin.frame.base.BaseActivity;

import dagger.Module;

/**
 * 功能----为activity提供生命周期的对象
 * <p>
 * Created by MNLIN on 2017/9/22.
 */
@PerActivity
@Module
public class ActivityModule {
    private BaseActivity activity;

    public ActivityModule(BaseActivity activity) {
        this.activity = activity;
    }
}
