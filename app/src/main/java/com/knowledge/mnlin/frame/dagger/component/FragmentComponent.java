package com.knowledge.mnlin.frame.dagger.component;

import com.knowledge.mnlin.frame.annotation.PerFragment;
import com.knowledge.mnlin.frame.base.BaseFragment;
import com.knowledge.mnlin.frame.dagger.module.FragmentModule;

import dagger.Component;

/**
 * 功能----碎片组件,用于注入dagger
 * <p>
 * Created by MNLIN on 2017/9/23.
 */
@PerFragment
@Component(modules = FragmentModule.class,dependencies = ApplicationComponent.class)
public interface FragmentComponent {
    void inject(BaseFragment baseFragment);
}
