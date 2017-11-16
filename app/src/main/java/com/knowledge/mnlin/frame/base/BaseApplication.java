package com.knowledge.mnlin.frame.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.Utils;
import com.knowledge.mnlin.frame.BuildConfig;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.dagger.component.ApplicationComponent;
import com.knowledge.mnlin.frame.dagger.component.DaggerApplicationComponent;
import com.knowledge.mnlin.frame.dagger.module.ApplicationModule;
import com.knowledge.mnlin.frame.rxbus.RxBus;
import com.knowledge.mnlin.frame.util.Const;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
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
        /*Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            try {
                //显示提示
                Toast.makeText(BaseApplication.this, "线程：" + t.getName() + "\n" + "异常：" + e.toString(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "uncaughtException: " + e.toString());

                //关闭应用
                System.exit(0);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });*/

        //简单的toast
        singleSmallToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        ((TextView) singleSmallToast.getView().findViewById(android.R.id.message)).setTextColor(getResources().getColor(R.color.colorAccent));
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
                    Logger.d("x5内核加载成功");
                }else{
                    Logger.d("x5内核加载失败");
                }
            }
        });
        QbSdk.setDownloadWithoutWifi(true);

        //路由跳转框架初始化
        if (BuildConfig.DEBUG) {
            // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.openLog();     // 打印日志
        ARouter.init(this); // 尽可能早，推荐在Application中初始化

        //友盟统计
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //友盟设置场景类型;session统计间隔设置为10分钟
        //MobclickAgent.setSessionContinueMillis(600000);
        //如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用MobclickAgent.onKillProcess(Context context)方法，用来保存统计数据
        //设置调试模式下日志检测
        MobclickAgent.setDebugMode(false);
        //友盟主动上报错误
        //Thread.currentThread().setUncaughtExceptionHandler((t, e) -> MobclickAgent.reportError(App.this,e));
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                PushAgent.getInstance(activity).onAppStart();
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                MobclickAgent.onResume(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                MobclickAgent.onPause(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

        //友盟消息推送
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                //Logger.d(TAG, "onSuccess: \n"+deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                MobclickAgent.reportError(BaseApplication.this,"友盟推送无法开启:\n"+s+"\n&&\n"+s1);
            }
        });

        //Utils框架初始化
        Utils.init(this);
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
