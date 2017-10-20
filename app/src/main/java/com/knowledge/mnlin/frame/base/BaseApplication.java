package com.knowledge.mnlin.frame.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.knowledge.mnlin.frame.dagger.component.ApplicationComponent;
import com.knowledge.mnlin.frame.dagger.component.DaggerApplicationComponent;
import com.knowledge.mnlin.frame.dagger.module.ApplicationModule;
import com.knowledge.mnlin.frame.rxbus.RxBus;
import com.knowledge.mnlin.frame.util.ActivityUtil;
import com.knowledge.mnlin.frame.util.Const;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.smtt.sdk.QbSdk;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Stack;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * Created by Administrator on 17-1-22.
 * <p>
 * 将蓝牙4.0服务与application进行绑定，保证在内存足够的情况下，对象不会被回收
 */
public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";

    //活动管理
    static ArrayList<Stack<BaseActivity>> activityManager;

    //当前是否处于严格模式
    static boolean isStrictMode = false;

    //BigToast
    @Inject
    public  Toast singleBigToast;

    protected Toast singleSmallToast;

    //维持全局的对象
    private static ApplicationComponent applicationComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //当方法数量超过65535后，需要变成两个dex包
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化其他框架
        init();
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    /**
     * 初始化全局变量等信息
     */
    private void init() {
        //注入dagger框架
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
        applicationComponent.inject(this);

        //活动管理对象初始化
        activityManager = new ArrayList<>();

        //初始化litePal框架,创建数据库
        LitePal.initialize(this);
        Connector.getDatabase();

        //设置全局未被捕获的异常：监听当前进程的所有线程
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            try {
                //先让应用回到桌面
                ActivityUtil.returnHome(BaseApplication.this);

                //显示提示
                Toast.makeText(BaseApplication.this, "线程：" + t.getName() + "\n" + "异常：" + e.toString(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "uncaughtException: " + e.toString());

                //关闭应用
                System.exit(0);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        //简单的toast
        singleSmallToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        singleSmallToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 200);

        //捕获RxBus发出的消息,用于显示toast等
        RxBus.getInstance().toObservable(BaseEvent.class).observeOn(AndroidSchedulers.mainThread()).subscribe(baseEvent -> {
            switch (baseEvent.operateCode) {
                case Const.SHOW_TOAST: {
                    singleSmallToast.setText(baseEvent.data.toString());
                    singleSmallToast.show();
                    break;
                }
            }
        });

        //Logger
        LogStrategy strategy= new LogcatLogStrategy();
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                //.showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                //.methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .logStrategy(strategy) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("My custom tag")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

        //初始化内存泄漏工具
        //LeakCanary.install(this);

        //二维码扫描工具
        ZXingLibrary.initDisplayOpinion(this);

        //初始化第三方webview
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {
                if(b){
                    //加载x5内核成功,会使用x5内核
                }else{
                    //加载内核失败,会使用系统自带webkit内核
                }
            }
        });
        QbSdk.setDownloadWithoutWifi(true);

    }

    /**
     * 当系统内存严重不足时，系统会调用该方法
     * <p>
     * Activity对象和Application对象都实现了ComponentCallbacks接口，该接口内有抽象的onLowMemory方法，因此在activity和application中都可以通过实现该方法来处理内存不足的事件
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
