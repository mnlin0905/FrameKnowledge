package com.knowledge.mnlin.frame.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.knowledge.mnlin.collapselayout.CollapseLayout;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.adapter.HelpAdapter;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.contract.HelpContract;
import com.knowledge.mnlin.frame.presenter.HelpPresenter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;

@Route(path = "/activity/HelpActivity")
public class HelpActivity extends BaseActivity<HelpPresenter> implements HelpContract.View {

    @BindView(R.id.lv_help)
    ListView mLvHelp;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_help;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        ArrayList<String> temp = new ArrayList<>();
        mLvHelp.setAdapter(new HelpAdapter(this, Arrays.asList(getResources().getStringArray(R.array.activity_help_keys)), Arrays.asList(getResources().getStringArray(R.array.activity_help_values))));
    }

    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
    }


}