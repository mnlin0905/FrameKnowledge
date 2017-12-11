package com.knowledge.mnlin.frame.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.arouter.ARouterConst;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.contract.QbWebBrowseContract;
import com.knowledge.mnlin.frame.presenter.QbWebBrowsePresenter;
import com.orhanobut.logger.Logger;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@Route(path = ARouterConst.Activity_QbWebBrowseActivity)
@RuntimePermissions
public class QbWebBrowseActivity extends BaseActivity<QbWebBrowsePresenter> implements QbWebBrowseContract.View {
    @Autowired
    String url;
    @Autowired
    String source;
    @BindView(R.id.pb_schedule)
    ProgressBar mPbSchedule;
    @BindView(R.id.tv_network)
    TextView mTvNetwork;
    @BindView(R.id.wv_web)
    WebView mWvWeb;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_qb_web_browse;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        initWebView();

        //尝试加载网页
        QbWebBrowseActivityPermissionsDispatcher.loadWebWithPermissionCheck(this);
    }

    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
    }

    /**
     * 网页初始化
     */
    private void initWebView() {
        WebSettings settings = mWvWeb.getSettings();
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(true);
        settings.setSavePassword(false);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);
        mWvWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                return super.shouldOverrideUrlLoading(webView, s);
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                Logger.d("页面加载开始:" + s);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
            }
        });
        mWvWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView webView, String s) {
                super.onReceivedTitle(webView, s);
            }
        });

    }

    /**
     * 加载网页
     */
    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    void loadWeb() {
        if (url != null) {
            mWvWeb.loadUrl(url);
        } else if (source != null) {
            mWvWeb.loadData(source, "text/html; charset=UTF-8", "UTF -8");
        } else {
            showToast("无数据源,无法加载网页");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        QbWebBrowseActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.READ_PHONE_STATE)
    void onShowRationale(final PermissionRequest request) {
        showToast("先打开电话权限才能正常加载网页");
        request.proceed();
    }

    @OnPermissionDenied(Manifest.permission.READ_PHONE_STATE)
    void onPermissionDenied() {
        showToast("已拒绝此次权限申请,界面将退出");
        finish();
    }

    @OnNeverAskAgain(Manifest.permission.READ_PHONE_STATE)
    void onNeverAskAgain() {
        showToast("若需要网页加载功能,请自行前往权限界面打开电话权限");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearWebViewResource();
    }

    /**
     * 防止webView内存泄露
     */
    private void clearWebViewResource() {
        if (mWvWeb != null) {
            mWvWeb.stopLoading();
            mWvWeb.clearCache(true);
            mWvWeb.removeAllViews();
            ((ViewGroup) mWvWeb.getParent()).removeView(mWvWeb);
            mWvWeb.setTag(null);
            mWvWeb.clearHistory();
            mWvWeb.destroy();
            mWvWeb = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWvWeb.canGoBack()) {
            mWvWeb.goBack();
            return;
        }
        super.onBackPressed();
    }
}