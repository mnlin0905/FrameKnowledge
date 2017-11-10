package com.knowledge.mnlin.frame.base;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jaeger.library.StatusBarUtil;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.dagger.component.ActivityComponent;
import com.knowledge.mnlin.frame.dagger.component.DaggerActivityComponent;
import com.knowledge.mnlin.frame.dagger.module.ActivityModule;
import com.knowledge.mnlin.frame.retrofit.HttpInterface;
import com.knowledge.mnlin.frame.rxbus.RxBus;
import com.knowledge.mnlin.frame.util.ActivityUtil;
import com.knowledge.mnlin.frame.util.Const;

import java.lang.reflect.Field;
import java.util.Stack;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.view.KeyEvent.KEYCODE_MENU;

/**
 * Created by Administrator on 17-1-22.
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();

    public Toolbar toolbar;

    protected ActivityComponent activityComponent;

    //管理RxBus添加的事件队列
    private static CompositeDisposable group;

    @Inject
    protected T mPresenter;

    @Inject
    protected HttpInterface httpInterface;

    @Override
    @SuppressWarnings("all")
    final protected void onCreate(Bundle savedInstanceState) {
        if (BaseApplication.isStrictMode) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
                    .detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        }
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate: ");

        //设置支持动画过渡效果
        ActivityUtil.setActivityContentTransitions(this);
        ActivityUtil.setActivitySupportTransitions(this);
        /*getWindow().setExitTransition(new Fade());*/

        //添加到活动管理中
        manageAddActivity();

        //设置内容全屏
        ActivityUtil.setDecorTransparent(this);

        //设置statubar的颜色
        ActivityUtil.setStatusBarColor(this, getResources().getColor(R.color.colorPrimaryDarkHacker));

        //添加布局
        setContentView(getContentViewId());
        ButterKnife.bind(this);

        //设置toolbar和startBar颜色;当点击navigation时默认退出活动
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        //注入dagger框架
        activityComponent = DaggerActivityComponent.builder().applicationComponent(BaseApplication.getApplicationComponent()).activityModule(new ActivityModule(this)).build();
        injectSelf();

        //注入路由Arouter框架
        ARouter.getInstance().inject(this);

        //绑定presenter和activity
        mPresenter.mView = this;

        //初始化内容
        initData(savedInstanceState);
    }

    /**
     * 使用dagger注入自身
     */
    protected abstract void injectSelf();

    /**
     * 管理activity实例
     */
    private void manageAddActivity() {
        boolean isExistStack = false;
        for (int i = 0; i < BaseApplication.activityManager.size(); i++) {
            Stack<BaseActivity> temp = BaseApplication.activityManager.get(i);
            if (temp.get(0).getTaskId() == getTaskId()) {
                temp.push(this);
                isExistStack = true;
                break;
            }
        }
        if (!isExistStack) {
            Stack<BaseActivity> stack = new Stack<>();
            stack.push(this);
            BaseApplication.activityManager.add(stack);
        }
        logStack();
    }

    /**
     * 移除需要销毁的activity实例
     */
    private void manageRemoveActivity() {
        for (int i = 0; i < BaseApplication.activityManager.size(); i++) {
            Stack<BaseActivity> temp = BaseApplication.activityManager.get(i);
            if (temp.get(0).getTaskId() == getTaskId()) {
                temp.pop();
                if (temp.size() == 0) {
                    BaseApplication.activityManager.remove(temp);
                }
                break;
            }
        }
    }

    /**
     * 打印活动栈
     */
    private void logStack() {
        for (int i = 0; i < BaseApplication.activityManager.size(); i++) {
            Stack<BaseActivity> temp = BaseApplication.activityManager.get(i);
            Log.v(TAG, "\n栈id：" + temp.get(0).getTaskId() + "\n栈底->" + temp.toString() + "栈顶");
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.v(TAG, "onPostCreate: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume: ");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.v(TAG, "onPostResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy: ");
        manageRemoveActivity();
        logStack();

        mPresenter.mView = null;
        removeDisposable();
    }

    /**
     * @return 获取布局文件
     */
    @LayoutRes
    protected abstract int getContentViewId();

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        Log.v(TAG, "onPostResume: public");
    }

    /**
     * 设置状态栏颜色
     */
    @SuppressWarnings("all")
    protected void setStatusBarColor(int color) {
        if (ActivityUtil.getSDKVersion() >= 21) {
            getWindow().setStatusBarColor(color);
        } else {
            StatusBarUtil.setColor(this, color);
        }
    }

    /**
     * 设置导航栏颜色
     */
    protected void setToolbarColor(int color) {
        toolbar.setBackgroundColor(color);
    }

    /**
     * 将一个已有的颜色值加深
     */
    private int getDeepColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        //改变亮度
        hsv[2] = (float) (hsv[2] * 0.8);
        return Color.HSVToColor(hsv);
    }

    /**
     * 初始化数据
     */
    protected abstract void initData(Bundle savedInstanceState);

    /**
     * @param msg 需要显示的toast消息
     */
    protected void showToast(String msg) {
        RxBus.getInstance().post(new BaseEvent(Const.SHOW_TOAST, msg == null ? "null" : msg));
    }

    /**
     * 显示snackbar
     */
    protected void showSnackbar(String msg, String button, View.OnClickListener onClickButton) {
        Snackbar singleSnackbar = Snackbar.make(toolbar == null ? findViewById(android.R.id.content) : toolbar, msg, Snackbar.LENGTH_INDEFINITE);
        ((TextView) singleSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setMaxLines(10);
        singleSnackbar.getView().setAlpha(0.9f);
        singleSnackbar.setActionTextColor(getThemeColorAttribute(R.style.TextInputLayout_HintTextAppearance_Hacker, android.R.attr.textColor));
        singleSnackbar.setAction(button, onClickButton == null ? (View.OnClickListener) view -> singleSnackbar.dismiss() : onClickButton).show();
    }

    /**
     * 获取系统属性中某个值
     */
    protected int getThemeColorAttribute(int styleRes, int colorId) {
        int defaultColor = 0xFF000000;
        int[] attrsArray = {colorId};
        TypedArray typedArray = obtainStyledAttributes(styleRes, attrsArray);
        int color = typedArray.getColor(0, defaultColor);

        typedArray.recycle();
        return color;
    }

    /**
     * 绑定View的id和对象
     */
    protected void bindView(String[] ids, View root) {
        try {
            if (ids == null || ids.length == 0) return;
            Class class_id = Class.forName("com.mnlin.hotchpotch.R$id");
            Class class_view = getClass();
            Field field_id = null;
            Field field_view = null;
            for (String id : ids) {
                field_id = class_id.getDeclaredField(id);
                field_view = class_view.getDeclaredField(id);
                field_id.setAccessible(true);
                field_view.setAccessible(true);
                if (root != null) {
                    field_view.set(this, root.findViewById(field_id.getInt(null)));
                } else {
                    field_view.set(this, findViewById(field_id.getInt(null)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定View的id和对象
     */
    protected void bindView() {
        try {
            Class class_id = Class.forName("com.mnlin.hotchpotch" + ".R$id");
            Class class_view = getClass();
            Field[] fields = class_view.getDeclaredFields();
            Field field_id;
            for (Field field_view : fields) {
                try {
                    field_id = class_id.getDeclaredField(field_view.getName());
                    field_id.setAccessible(true);
                    field_view.setAccessible(true);
                    field_view.set(this, findViewById(field_id.getInt(null)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取颜色值
     */
    final public int getColors(@ColorRes int resId) {
        if (Build.VERSION.SDK_INT < 23) {
            return getResources().getColor(resId);
        } else {
            return getResources().getColor(resId, null);
        }
    }

    protected void removeDisposable() {
        if (group != null) {
            group.dispose();
        }
    }

    protected void addDisposable(Disposable disposable) {
        if (group == null) {
            group = new CompositeDisposable();
        }
        group.add(disposable);
    }

    /**
     * 当点击menu键时屏蔽任何操作
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    /**
     * 获取drawable
     */
    protected Drawable dispatchGetDrawable(@DrawableRes int resId) {
        if (Build.VERSION.SDK_INT >= 21) return getDrawable(resId);
        else return getResources().getDrawable(resId);
    }

    /**
     * 获取color
     */
    protected int dispatchGetColor(@ColorRes int resId) {
        if (Build.VERSION.SDK_INT >= 23) return getColor(resId);
        else return getResources().getColor(resId);
    }

}
