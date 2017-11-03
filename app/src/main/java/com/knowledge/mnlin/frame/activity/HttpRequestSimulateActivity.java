package com.knowledge.mnlin.frame.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.contract.HttpRequestSimulateContract;
import com.knowledge.mnlin.frame.presenter.HttpRequestSimulatePresenter;

import butterknife.BindView;

@Route(path = "/activity/HttpRequestSimulateActivity")
public class HttpRequestSimulateActivity extends BaseActivity<HttpRequestSimulatePresenter> implements HttpRequestSimulateContract.View {
    @BindView(R.id.tiet_url)
    TextInputEditText mTietUrl;
    @BindView(R.id.tiet_method)
    TextInputEditText mTietMethod;
    @BindView(R.id.tiet_request_header)
    TextInputEditText mTietRequestHeader;
    @BindView(R.id.tiet_request_body)
    TextInputEditText mTietRequestBody;
    @BindView(R.id.tiet_response_header)
    TextInputEditText mTietResponseHeader;
    @BindView(R.id.tiet_response_body)
    TextInputEditText mTietResponseBody;

    @Override
    protected void initData(Bundle savedInstanceState) {
    }

    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_http_request_simulate;
    }
}