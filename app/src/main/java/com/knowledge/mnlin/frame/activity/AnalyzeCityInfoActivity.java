package com.knowledge.mnlin.frame.activity;

import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.bean.CityInfoBean;
import com.knowledge.mnlin.frame.contract.AnalyzeCityInfoContract;
import com.knowledge.mnlin.frame.presenter.AnalyzeCityInfoPresenter;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.knowledge.mnlin.frame.R.id.tv_city;

/**
 * 解析城市信息
 */
public class AnalyzeCityInfoActivity extends BaseActivity<AnalyzeCityInfoPresenter> implements AnalyzeCityInfoContract.View {


    @BindView(R.id.tv_city_switch)
    TextView mTvCitySwitch;
    @BindView(tv_city)
    TextView mTvCity;
    private WebView webView;

    //数据结构
    private ArrayList<CityInfoBean> datas;
    //记录城市区域是否加载完成
    private boolean isLoadOver;

    //选择城市
    private OptionsPickerView pvCustomOptions;
    private int[] currencyIndex;
    private ArrayList<CityInfoBean> provinces;
    private ArrayList<ArrayList<CityInfoBean>> cities;
    private ArrayList<ArrayList<ArrayList<CityInfoBean>>> counties;

    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_analyze_city_info;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        currencyIndex = new int[3];
        provinces = new ArrayList<>();
        cities = new ArrayList<>();
        counties = new ArrayList<>();

        //初始化一个webview,来加载城市信息
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }
        });
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setSupportZoom(false);
        settings.setAppCacheEnabled(false);
        settings.setAllowFileAccess(true);
        settings.setUseWideViewPort(true);
        settings.supportMultipleWindows();
        settings.setNeedInitialFocus(true);
        if (Build.VERSION.SDK_INT >= 17) {
            webView.addJavascriptInterface(this, "android");
        }
        webView.loadUrl("file:///android_asset/www/get_address_list.html");
    }

    /**
     * 解析网页中返回的信息
     */
    @JavascriptInterface
    public void analyzeHtmlInfo(String json) {
        Type type = new TypeToken<ArrayList<CityInfoBean>>() {
        }.getType();
        datas = new Gson().fromJson(json, type);
        if (datas == null) showToast("无法显示城市信息");
        CityInfoBean province_item;
        ArrayList<CityInfoBean> city_list;
        CityInfoBean city_item;
        ArrayList<ArrayList<CityInfoBean>> country_list_list;
        ArrayList<CityInfoBean> country_list;
        CityInfoBean country_item;

        for (int i = 0; i < datas.size(); i++) {
            province_item = datas.get(i);

            //如果province为'请选择',则不再处理市和乡镇,同时从省份中移除该item(不添加即为移除)
            if (province_item.requireDelete()) {
                continue;
            }

            //如果province为其他,但没有sub,则对sub进行填充
            city_list = province_item.getSub();
            if (city_list == null) city_list = new ArrayList<>();
            if (!province_item.hasSub()) {
                city_list.add(province_item);
            }

            //同时初始化一个country
            country_list_list = new ArrayList<>();

            for (int j = 0; j < city_list.size(); j++) {
                city_item = city_list.get(j);

                country_list = city_item.getSub();
                if (country_list == null) country_list = new ArrayList<>();
                if (!city_item.hasSub()) {
                    country_list.add(city_item);
                }

                for (int k = 0; k < country_list.size(); k++) {
                    country_item = country_list.get(k);

                    // TODO: 2017/10/17 处理下一层信息
                }

                country_list_list.add(country_list);
            }

            counties.add(country_list_list);
            cities.add(city_list);
            provinces.add(province_item);
        }
        isLoadOver = true;

        //创建视图的代码需要放在主线程
        runOnUiThread(() -> {
            //初始化银行卡选择窗口
            pvCustomOptions = new OptionsPickerView
                    .Builder(this, (options1, options2, options3, v) -> {
                currencyIndex[0] = options1;
                currencyIndex[1] = options2;
                currencyIndex[2] = options3;

                // TODO: 2017/10/16 处理选中item的逻辑
                mTvCity.setText("中国/" + provinces.get(options1) + "/" + cities.get(options1).get(options2) + "/" + counties.get(options1).get(options2).get(options3));
            })
                    .setLayoutRes(R.layout.pickerview_custom_options, v -> {
                        v.findViewById(R.id.tv_finish).setOnClickListener(v1 -> {
                            pvCustomOptions.returnData();
                            pvCustomOptions.dismiss();
                        });
                        v.findViewById(R.id.tv_cancel).setOnClickListener(v1 -> {
                            pvCustomOptions.returnData();
                            pvCustomOptions.dismiss();
                        });
                    })
                    .setSelectOptions(0, 0, 0)
                    .setOutSideCancelable(false)
                    .isDialog(false)
                    .build();
            pvCustomOptions.setPicker(provinces, cities, counties);
        });
    }

    /**
     * 切换城市
     */
    @OnClick(R.id.tv_city_switch)
    public void switchCity() {
        if(!isLoadOver){
            showToast("正在加载数据,请稍后操作");
            return;
        }

        if (pvCustomOptions != null) {
            pvCustomOptions.show(true);
        } else {
            showToast("获取城市列表失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pvCustomOptions = null;
        webView.stopLoading();
        webView.destroy();
    }
}