package com.knowledge.mnlin.frame.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jaeger.library.StatusBarUtil;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.base.BaseBean;
import com.knowledge.mnlin.frame.contract.SelectFunctionContract;
import com.knowledge.mnlin.frame.presenter.SelectFunctionPresenter;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SelectFunctionActivity extends BaseActivity<SelectFunctionPresenter> implements SelectFunctionContract.View {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.ll_content)
    LinearLayout mLlContent;
    @BindView(R.id.nv_slideBar)
    NavigationView mNvSlideBar;
    @BindView(R.id.dl_drawerLayout)
    DrawerLayout mDlDrawerLayout;

    //上次点击back时间
    private long lastPressBackTime;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_select_function;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_function, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_qr:
                ARouter.getInstance().build("/activity/QRUtilActivity").navigation();
                break;
            case R.id.action_city_info:
                ARouter.getInstance().build("/activity/AnalyzeCityInfoActivity").navigation();
                break;
            case R.id.action_http_request_simulate:
                ARouter.getInstance().build("/activity/HttpRequestSimulateActivity").navigation();
        }
        return true;
    }

    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        StatusBarUtil.setTranslucentForDrawerLayout(this, mDlDrawerLayout);

        toolbar.setNavigationOnClickListener(v -> mDlDrawerLayout.openDrawer(GravityCompat.START));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDlDrawerLayout, R.string
                .drawer_layout_open, R.string.drawer_layout_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (drawerView == mNvSlideBar) {
                    mLlContent.setLeft((int) (slideOffset * drawerView.getWidth()));
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        //设置左上角的图标形状随着滑动发生变化；设置滑动之后应当出现遮罩窗口的地方现设为透明
        toggle.syncState();
        mDlDrawerLayout.addDrawerListener(toggle);
        mDlDrawerLayout.setScrimColor(Color.TRANSPARENT);

        //设置navigation默认选中状态,位置等
        mNvSlideBar.setItemTextColor(getResources().getColorStateList(R.color.selector_slide_bar_text_icon_color));
        mNvSlideBar.setItemIconTintList(getResources().getColorStateList(R.color.selector_slide_bar_text_icon_color));
        //mNvSlideBar.setCheckedItem(0);
    }

    @OnClick(R.id.fab)
    protected void onClickFab() {
        // TODO: 2017/9/25 测试数据
        httpInterface.getJson("18337138008", "e5a00e37eb72222f8703098f1d07fa8e")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onNext(BaseBean baseBean) {
                        Log.d(TAG, "onNext: " + baseBean);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onCompleted: ");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (mDlDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDlDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (System.currentTimeMillis() - lastPressBackTime > 1000) {
            showToast("再点一次退出程序");
            lastPressBackTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
