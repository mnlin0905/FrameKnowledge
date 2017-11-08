package com.knowledge.mnlin.frame.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.contract.HttpRequestSimulateContract;
import com.knowledge.mnlin.frame.presenter.HttpRequestSimulatePresenter;
import com.knowledge.mnlin.frame.util.ActivityUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Route(path = "/activity/HttpRequestSimulateActivity")
public class HttpRequestSimulateActivity extends BaseActivity<HttpRequestSimulatePresenter> implements HttpRequestSimulateContract.View {
    @BindView(R.id.tiet_url)
    TextInputEditText mTietUrl;
    @BindView(R.id.tiet_method)
    TextInputEditText mTietMethod;
    @BindView(R.id.tiet_request_body)
    TextInputEditText mTietRequestBody;
    @BindView(R.id.tiet_response_header)
    TextInputEditText mTietResponseHeader;
    @BindView(R.id.tiet_response_body)
    TextInputEditText mTietResponseBody;
    @BindView(R.id.til_request_body)
    TextInputLayout mTilRequestBody;
    @BindView(R.id.tiet_request_header)
    TextInputEditText mTietRequestHeader;
    @BindView(R.id.til_request_header)
    TextInputLayout mTilRequestHeader;
    @BindView(R.id.til_response_header)
    TextInputLayout mTilResponseHeader;
    @BindView(R.id.til_response_body)
    TextInputLayout mTilResponseBody;

    //添加header请求头
    private Map<String, String> reqHeader;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_http_request_simulate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!mTietUrl.getText().toString().matches("^(http://)(\\S+)$")) {
            showToast("请输入有效的http地址");
            return true;
        }
        if (!mTietMethod.getText().toString().matches("^(GET)|(POST)$")) {
            showToast("现只支持POST或GET方式请求网络");
            return true;
        }

        //获取请求头部
        reqHeader = new HashMap<>();
        String reqHeaderStr = mTietRequestHeader.getText().toString();
        if (reqHeaderStr.endsWith("\n"))
            reqHeaderStr = reqHeaderStr.substring(reqHeaderStr.length() - 1, reqHeaderStr.length());
        if (reqHeaderStr.matches("；|："))
            reqHeaderStr = reqHeaderStr.replaceAll("；", ";").replaceAll("：", "");
        try {
            if (reqHeaderStr.length() != 0) {
                String[] results = reqHeaderStr.split("\n");
                for (String result : results) {
                    String[] key_value = result.split(":");
                    reqHeader.put(key_value[0], key_value[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("请求头信息格式不符");
            return true;
        }

        try {
            switch (mTietMethod.getText().toString()) {
                case "GET":
                    //httpGet(mTietUrl.getText().toString());
                    httpGet("http://192.168.1.165:8000/index");
                    break;
                case "POST":
                    //httpPost(mTietUrl.getText().toString(), mTietRequestHeader.getText().toString());
                    httpPost("http://192.168.1.165:8000/index", mTietRequestHeader.getText().toString());
                    break;
                default:
                    showToast("暂不支持该请求方式");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("网络请求失败");
        }
        clearScreen();
        return true;
    }

    /**
     * 清除屏幕显示的信息
     */
    private void clearScreen() {
        mTietResponseHeader.setText("");
        mTietResponseBody.setText("");
    }

    /**
     * 发起网络请求GET
     */
    private void httpGet(final String urlRequest) throws Exception {
        Observable.just(urlRequest)
                .subscribeOn(Schedulers.io())
                .map(s -> {
                    final HttpURLConnection connection;
                    URL url = new URL(urlRequest);

                    //从指定url建立链接。
                    connection = (HttpURLConnection) url.openConnection();
                    //设置连接方式。
                    connection.setRequestMethod("GET");
                    //设置连接超时。
                    connection.setConnectTimeout(5000);
                    //设置读取超时。
                    connection.setReadTimeout(5000);
                    connection.setDoOutput(true);
                    //添加请求头
                    for (Map.Entry<String, String> item : reqHeader.entrySet()) {
                        connection.addRequestProperty(item.getKey(), item.getValue());
                    }

                    //获取链接的输入流。
                    InputStream inputStream = connection.getInputStream();
                    //新建BufferedReader对象，读取数据方便
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    //输入流读取完毕。
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    StringBuilder resHeader = new StringBuilder();
                    for (Map.Entry<String, List<String>> stringListEntry : connection.getHeaderFields().entrySet()) {
                        resHeader.append(stringListEntry.getKey() == null ? "" : stringListEntry.getKey() + ":");
                        for (int i = 0; i < stringListEntry.getValue().size(); i++) {
                            resHeader.append(stringListEntry.getValue().get(i));
                            resHeader.append(i == stringListEntry.getValue().size() - 1 ? "" : ";");
                        }
                        resHeader.append("\n");
                    }
                    runOnUiThread(() -> mTietResponseHeader.setText(resHeader.toString()));
                    if (connection != null) connection.disconnect();
                    return builder.toString();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> mTietResponseBody.setText(s), throwable -> {
                    throwable.printStackTrace();
                    showToast("请求失败:" + throwable.getMessage());
                });
    }

    /**
     * 发起网络请求POST
     */
    private void httpPost(final String urlRequest, final String formData) {
        Observable.just(urlRequest)
                .subscribeOn(Schedulers.io())
                .map(s -> {
                    final HttpURLConnection connection;
                    URL url = new URL(urlRequest);

                    //从指定url建立链接。
                    connection = (HttpURLConnection) url.openConnection();
                    //设置连接方式。
                    connection.setRequestMethod("POST");
                    //获取连接的输出流（输出流输出流是相对本机而言的）
                    //设置连接超时。
                    connection.setConnectTimeout(5000);
                    //设置读取超时。
                    connection.setReadTimeout(5000);
                    connection.setDoOutput(true);
                    //添加请求头
                    for (Map.Entry<String, String> item : reqHeader.entrySet()) {
                        connection.addRequestProperty(item.getKey(), item.getValue());
                    }
                    //将需要发送的数据送入输出流
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(formData);

                    //获取链接的输入流。
                    InputStream inputStream = connection.getInputStream();
                    //新建BufferedReader对象，读取数据方便
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    //输入流读取完毕。
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    StringBuilder resHeader = new StringBuilder();
                    for (Map.Entry<String, List<String>> stringListEntry : connection.getHeaderFields().entrySet()) {
                        resHeader.append(stringListEntry.getKey() == null ? "" : stringListEntry.getKey() + ":");
                        for (int i = 0; i < stringListEntry.getValue().size(); i++) {
                            resHeader.append(stringListEntry.getValue().get(i));
                            resHeader.append(i == stringListEntry.getValue().size() - 1 ? "" : ";");
                        }
                        resHeader.append("\n");
                    }
                    runOnUiThread(() -> mTietResponseHeader.setText(resHeader.toString()));
                    if (connection != null) connection.disconnect();
                    return builder.toString();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> mTietResponseBody.setText(s), throwable -> {
                    throwable.printStackTrace();
                    showToast("请求失败:" + throwable.getMessage());
                });
    }

    @OnClick({R.id.til_response_body, R.id.til_response_header})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.til_response_body:
                ActivityUtil.saveMsgToClipboard(getApplication(), mTietResponseBody.getText().toString());
                break;
            case R.id.til_response_header:
                ActivityUtil.saveMsgToClipboard(getApplication(), mTietResponseHeader.getText().toString());
                break;
        }
        showToast("内容已复制");
    }

    /**
     * 清除请求头信息
     */
    @OnLongClick({R.id.til_request_body, R.id.til_response_body, R.id.til_response_header})
    boolean onViewLongClicked(View view) {
        switch (view.getId()) {
            case R.id.til_request_body:
                mTietRequestBody.setText("");
                break;
            case R.id.til_response_header:
                ActivityOptionsCompat compat = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this,mTietResponseHeader,"share");
                ARouter.getInstance()
                        .build("/activity/AnalyzeByteDataActivity")
                        .withOptionsCompat(compat)
                        .withString("stream", mTietResponseHeader.getText().toString())
                        .navigation(this);
                break;
            case R.id.til_response_body:
                // TODO: 2017/11/8 编写网页
                break;
        }
        return true;
    }
}