package com.knowledge.mnlin.frame.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.adapter.ManageNoteAdapter;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.bean.NoteConfigBean;
import com.knowledge.mnlin.frame.bean.NoteContentBean;
import com.knowledge.mnlin.frame.contract.ManageNoteContract;
import com.knowledge.mnlin.frame.presenter.ManageNotePresenter;
import com.knowledge.mnlin.frame.view.EmptyView;
import com.knowledge.mnlin.frame.view.LineMenuView;
import com.knowledge.mnlin.frame.window.ActivityMenuDialog;
import com.orhanobut.logger.Logger;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;

import static com.knowledge.mnlin.frame.R.id.lmv_selectAll;

@Route(path = "/activity/ManageNoteActivity")
public class ManageNoteActivity extends BaseActivity<ManageNotePresenter> implements ManageNoteContract.View, ManageNoteAdapter.OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.empty_view)
    EmptyView mEmptyView;
    @BindView(R.id.xrv_noteList)
    XRecyclerView mXrvNoteList;
    @BindView(lmv_selectAll)
    LineMenuView mLmvSelectAll;

    //动画平移的高度
    private int animateheight;

    /**
     * 数据源
     * 适配器
     * 标题
     */
    private ArrayList<NoteConfigBean> data;
    private ManageNoteAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_manage_note;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        data = new ArrayList<>();
        adapter = new ManageNoteAdapter(this, data);
        mXrvNoteList.setAdapter(adapter);
        mXrvNoteList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mXrvNoteList.setPullRefreshEnabled(false);
        mXrvNoteList.setLoadingMoreEnabled(false);
        mXrvNoteList.setEmptyView(mEmptyView);
        mXrvNoteList.addItemDecoration(new ManageNoteAdapter.ItemDecoration(this));
        adapter.setOnItemClickListener(this);

        mLmvSelectAll.setOnCheckedChangeListener(this);
    }

    @Override
    protected void refreshData() {
        super.refreshData();
        //初始化数据
        DataSupport.order("createTime desc").findAsync(NoteConfigBean.class,true).listen(new FindMultiCallback() {
            @Override
            public <T> void onFinish(List<T> t) {
                data.clear();
                data.addAll((Collection<? extends NoteConfigBean>) t);
                adapter.notifyDataSetChanged(data);
                adapter.setMultiplyMode(false);
                Logger.v(data.toString());
                /*Cursor cursor = LitePal.getDatabase().query("NoteContentBean", null, null, null, null, null, null);
                if(cursor.moveToFirst()){
                    while (cursor.moveToNext()){
                        Logger.d("id = "+cursor.getLong(cursor.getColumnIndex("id"))+"\t foreign_id="+cursor.getLong(cursor.getColumnIndex("NoteConfigBean_id")));
                    }
                }*/
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //如果是全选/取消进行了切换，则对应处理逻辑
        if (adapter.isMultiplyMode()) {
            if (isChecked) {
                adapter.selectedPosition.initValue(true);
            } else {
                adapter.selectedPosition.initValue(false);
            }
            adapter.notifyDataSetChanged();
            refreshTitle();
        }
    }

    @Override
    public void doOnRecyclerViewItemClick(View v, int position) {
        ARouter.getInstance().build("/activity/EditNoteActivity").withObject("bean", data.get(position)).navigation();
    }

    @Override
    public void doOnSelectedAmountChanged(int amount) {
        refreshTitle();
    }

    @Override
    public void doOnMultiplyModeChanged(boolean isMultiplyMode) {
        if ((animateheight = mLmvSelectAll.getHeight()) <= 0) {
            mLmvSelectAll.measure(View.MeasureSpec.makeMeasureSpec(((ViewGroup) mLmvSelectAll.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(((ViewGroup) mLmvSelectAll.getParent()).getHeight(), View.MeasureSpec.AT_MOST));
            animateheight = mLmvSelectAll.getMeasuredHeight();
        }
        if (isMultiplyMode) {
            switchToMultiply();
        } else {
            switchToSingle();
        }
        refreshTitle();
        invalidateOptionsMenu();
    }

    /**
     * 刷新标题文字
     */
    private void refreshTitle() {
        if (adapter.isMultiplyMode()) {
            toolbar.setTitle(activityTitle + "(" + adapter.selectedPosition.countNumber(true) + "/" + data.size() + ")");
        } else {
            toolbar.setTitle(activityTitle);
        }
    }

    /**
     * 切换到多选模式
     */
    private void switchToMultiply() {
        mXrvNoteList.animate()
                .translationY(animateheight)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mXrvNoteList.setTranslationY(0);
                        mLmvSelectAll.setTranslationY(-animateheight);
                        mLmvSelectAll.setVisibility(View.VISIBLE);
                        mLmvSelectAll.animate()
                                .translationY(0)
                                .setDuration(200)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mXrvNoteList.setTranslationY(0);
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    /**
     * 切换到单点模式
     */
    private void switchToSingle() {
        mLmvSelectAll.setVisibility(View.VISIBLE);
        mLmvSelectAll.setTranslationY(0);
        mLmvSelectAll.animate()
                .translationY(-animateheight)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mXrvNoteList.setTranslationY(animateheight);
                        mLmvSelectAll.setVisibility(View.GONE);
                        mXrvNoteList.animate()
                                .translationY(0)
                                .setDuration(200)
                                .setListener(null)
                                .start();
                    }
                })
                .start();
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
                if (adapter.selectedPosition.countNumber(true) == 0) {
                    showToast("未选中任何选项");
                } else {
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
    private void prepareToDeleteNote() {
        new ActivityMenuDialog(this, new String[]{"确认"}, (dialog, position) -> {
            for (int i = 0; i < data.size(); i++) {
                if (adapter.selectedPosition.valueAt(i)) {
                    DataSupport.deleteAll(NoteContentBean.class,"NoteConfigBean_id = ?",String.valueOf(data.get(i).getId()));
                    data.get(i).delete();
                }
            }
            refreshData();
            return false;
        }).show();
    }

    @Override
    public void onBackPressed() {
        if (adapter.isMultiplyMode()) {
            adapter.setMultiplyMode(false);
        } else {
            super.onBackPressed();
        }
    }
}