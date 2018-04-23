package com.knowledge.mnlin.frame.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.knowledge.mnlin.RollTextView;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.arouter.ARouterConst;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.contract.SplashContract;
import com.knowledge.mnlin.frame.presenter.SplashPresenter;
import com.orhanobut.logger.Logger;

import java.util.Arrays;

/**
 * 功能----启动界面
 * <p>
 * Created(Gradle default create) by MNLIN on 2017/12/08 07:09:59 (+0000).
 */
//@ActivityInject
@Route(path = ARouterConst.Activity_SplashActivity)
public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashContract.View {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        RollTextView rollTextView = findViewById(R.id.rtv_temp);
        rollTextView.setAppearCount(3)//设置每次显示的数量
                .setInterval(2000)//设置滚动的间隔时间，以毫秒为单位
                .setOrderVisible(true)//设置显示序号的view是否可见
                .setLayoutResource(R.layout.item_roll_text_view)//为item自定义layout,但必须遵循规定
                .setEndText("查看", true)//设置尾部文字是否可见
                .setRollDirection(2)//设置滚动的方向，0为向上滚动，1为向下滚动，2为向右滚动，3为向左滚动
                //设置itemClick监听器
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Logger.e("点击位置：" + position);
                    }
                })
                //设置数据源，接收一个list，显示内容时调用其toString方法，因此数据内容不可为null
                .refreshData(Arrays.asList("0000000"
                        , "1111111111111111111111111111111111111111111111111111"
                        , "22222"
                        , "3333333"
                        , "444444"
                        , "55555"
                        , "6666666"
                        , "777777"
                        , "888888"
                        , "99999999"));

  /*  rollTextView.refreshData(Arrays.asList("0000000"
                , "1111111111111111111111111111111111111111111111111111"
                , "22222"
                , "3333333"
                , "444444"
                , "55555"
                , "6666666"
                , "777777"
                , "888888"
                , "99999999"));*/
    }

    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
    }

//    @MethodInject(method = LifeCycleMethod.onResume,priority = 2)
//    void sp1(){
//        Logger.e("sp1");
//    }
}