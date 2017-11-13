package com.knowledge.mnlin.frame.dagger.component;

import com.knowledge.mnlin.frame.activity.AnalyzeByteDataActivity;
import com.knowledge.mnlin.frame.activity.AnalyzeCityInfoActivity;
import com.knowledge.mnlin.frame.activity.EditNoteActivity;
import com.knowledge.mnlin.frame.activity.HttpRequestSimulateActivity;
import com.knowledge.mnlin.frame.activity.ManageNoteActivity;
import com.knowledge.mnlin.frame.activity.QRUtilActivity;
import com.knowledge.mnlin.frame.activity.QbWebBrowseActivity;
import com.knowledge.mnlin.frame.activity.SelectFunctionActivity;
import com.knowledge.mnlin.frame.annotation.PerActivity;
import com.knowledge.mnlin.frame.dagger.module.ActivityModule;

import dagger.Component;

/**
 * 功能----activity组件,提供清单文件
 * <p>
 * Created by MNLIN on 2017/9/22.
 */
@PerActivity
@Component(modules = ActivityModule.class,dependencies = ApplicationComponent.class)
public interface ActivityComponent {
    void inject(SelectFunctionActivity activity);

    void inject(QRUtilActivity qrUtilActivity);

    void inject(AnalyzeCityInfoActivity analyzeCityInfoActivity);

    void inject(HttpRequestSimulateActivity httpRequestSimulateActivity);

    void inject(AnalyzeByteDataActivity analyzeByteDataActivity);

    void inject(QbWebBrowseActivity qbWebBrowseActivity);

    void inject(ManageNoteActivity manageNoteActivity);

    void inject(EditNoteActivity editNoteActivity);
}
