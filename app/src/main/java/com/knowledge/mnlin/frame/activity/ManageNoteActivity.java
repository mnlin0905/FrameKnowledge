package com.knowledge.mnlin.frame.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.adapter.ManageNoteAdapter;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.bean.NoteConfigBean;
import com.knowledge.mnlin.frame.contract.ManageNoteContract;
import com.knowledge.mnlin.frame.presenter.ManageNotePresenter;
import com.knowledge.mnlin.frame.view.EmptyView;
import com.knowledge.mnlin.frame.view.LineMenuView;
import com.knowledge.mnlin.frame.window.ActivityMenuDialog;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

@Route(path = "/activity/ManageNoteActivity")
public class ManageNoteActivity extends BaseActivity<ManageNotePresenter> implements ManageNoteContract.View, ManageNoteAdapter.OnItemClickListener {

    @BindView(R.id.empty_view)
    EmptyView mEmptyView;
    @BindView(R.id.xrv_noteList)
    XRecyclerView mXrvNoteList;
    @BindView(R.id.lmv_selectAll)
    LineMenuView mLmvSelectAll;

    /**
     * 数据源
     * 适配器
     * 标题
     */
    private LinkedList<NoteConfigBean> data;
    private ManageNoteAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_manage_note;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        data = new LinkedList<>();
        adapter = new ManageNoteAdapter(this, data);
        mXrvNoteList.setAdapter(adapter);
        mXrvNoteList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mXrvNoteList.setPullRefreshEnabled(false);
        mXrvNoteList.setLoadingMoreEnabled(false);
        mXrvNoteList.setEmptyView(mEmptyView);
        adapter.setOnItemClickListener(this);
    }

    @Override
    protected void refreshData() {
        super.refreshData();
        //初始化数据
        DataSupport.findAllAsync(NoteConfigBean.class).listen(new FindMultiCallback() {
            @Override
            public <T> void onFinish(List<T> t) {
                data.clear();
                data.addAll((Collection<? extends NoteConfigBean>) t);
                adapter.notifyDataSetChanged(data);
            }
        });
    }

    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
    }

    @Override
    public void doOnRecyclerViewItemClick(View v, int position) {
        ARouter.getInstance().build("/activity/EditNoteActivity").withObject("bean",data.get(position)).navigation();
    }

    @Override
    public void doOnSelectedAmountChanged(int amount) {
        toolbar.setTitle(activityTitle + "(" + adapter.selectedPosition.countNumber(true) + "/" + data.size() + ")");
    }

    @Override
    public void doOnMultiplyModeChanged(boolean isMultiplyMode) {
        mLmvSelectAll.animate().translationY(isMultiplyMode?1:0);
        //mLmvSelectAll.setVisibility(isMultiplyMode ? View.VISIBLE : View.INVISIBLE);
        if (isMultiplyMode) {
            toolbar.setTitle(activityTitle + "(" + adapter.selectedPosition.countNumber(true) + "/" + data.size() + ")");
        } else {
            toolbar.setTitle(activityTitle);
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_note, menu);
        menu.findItem(R.id.action_add_note).setVisible(!adapter.isMultiplyMode());
        menu.findItem(R.id.action_delete_note).setVisible(adapter.isMultiplyMode());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_note:
                if(adapter.selectedPosition.countNumber(true)==0){
                    showToast("未选中任何选项");
                }else{
                   prepareToDeleteNote();
                }
                break;
            case R.id.action_add_note:
                ARouter.getInstance().build("/activity/EditNoteActivity").navigation();
                break;
        }
        return true;
    }

    /**
     * 准备删除便签
     */
    private void prepareToDeleteNote(){
        new ActivityMenuDialog(this, new String[]{"确认"}, (dialog, position) -> {
            for (int i = 0; i < data.size(); i++) {
                if(adapter.selectedPosition.valueAt(i)){
                    data.get(i).delete();
                }
            }
            return false;
        }).show();
    }
}