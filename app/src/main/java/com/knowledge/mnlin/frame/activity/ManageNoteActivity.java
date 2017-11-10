package com.knowledge.mnlin.frame.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.adapter.ManageNoteAdapter;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.bean.NoteConfigBean;
import com.knowledge.mnlin.frame.contract.ManageNoteContract;
import com.knowledge.mnlin.frame.presenter.ManageNotePresenter;
import com.knowledge.mnlin.frame.view.EmptyView;

import java.util.LinkedList;

import butterknife.BindView;

@Route(path = "/activity/ManageNoteActivity")
public class ManageNoteActivity extends BaseActivity<ManageNotePresenter> implements ManageNoteContract.View {

    @BindView(R.id.empty_view)
    EmptyView mEmptyView;
    @BindView(R.id.xrv_noteList)
    XRecyclerView mXrvNoteList;

    /**
     * 数据源
     * 适配器
     */
    private LinkedList<NoteConfigBean> data;
    private ManageNoteAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_manage_note;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
data=new LinkedList<>();
        mXrvNoteList.setAdapter(new ManageNoteAdapter(this,data));
        mXrvNoteList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mXrvNoteList.setPullRefreshEnabled(false);
        mXrvNoteList.setLoadingMoreEnabled(false);
        mXrvNoteList.setEmptyView(mEmptyView);
    }

    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
    }
}