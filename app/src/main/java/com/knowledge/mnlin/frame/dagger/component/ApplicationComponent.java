package com.knowledge.mnlin.frame.dagger.component;

import com.knowledge.mnlin.frame.base.BaseApplication;
import com.knowledge.mnlin.frame.dagger.module.ApplicationModule;
import com.knowledge.mnlin.frame.retrofit.HttpInterface;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 功能----应用的组件
 * <p>
 * Created by MNLIN on 2017/9/22.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(BaseApplication application);

    HttpInterface initHttpInterface();
}
