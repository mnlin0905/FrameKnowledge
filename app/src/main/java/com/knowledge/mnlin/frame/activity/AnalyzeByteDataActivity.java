package com.knowledge.mnlin.frame.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.adapter.AnalyzeByteAdapter;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.contract.AnalyzeByteDataContract;
import com.knowledge.mnlin.frame.presenter.AnalyzeByteDataPresenter;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/activity/AnalyzeByteDataActivity")
public class AnalyzeByteDataActivity extends BaseActivity<AnalyzeByteDataPresenter> implements AnalyzeByteDataContract.View,AdapterView.OnItemClickListener {

    @BindView(R.id.lv_group)
    ListView mLvGroup;
    @BindView(R.id.tfl_bytes)
    TagFlowLayout mTflBytes;
    @BindView(R.id.tfl_chars)
    TagFlowLayout mTflChars;

    //listview
    private ArrayList<String> data;
    private AnalyzeByteAdapter adapter;

    //接收的数据流
    @Autowired
    String stream;

    //记录字节流显示的方式:2,10,16进制
    private  int[] decimals={2,10,16,};
    private int arraysPosition=0;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_analyze_byte_data;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        if(TextUtils.isEmpty(stream)){
            showToast("数据源不能为空");
            finish();
        }
        int size= (int) Math.ceil(stream.length()/16.0);
        data=new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            data.add(String.format("%04X",i*16)+"-"+String.format("%04X",i*16+15));
        }
        adapter=new AnalyzeByteAdapter(this,data);
        mLvGroup.setAdapter(adapter);
        mLvGroup.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String[] start_end = data.get(position).split("-");
        int start=Integer.parseInt(start_end[0],16);
        int end=Integer.parseInt(start_end[1],16);
        String text=stream.substring(start,end);
        refreshBytes(text);
        refreshChars(text);
    }


    /**
     * 刷新字节流显示
     */
    private void refreshBytes(String bytes){
        byte[] bs=bytes.getBytes();
        ArrayList<Byte> list=new ArrayList<>();
        for (Byte b : bs) {
            list.add(b);
        }
        mTflBytes.setAdapter(new TagAdapter<Byte>(list) {
            @Override
            public View getView(FlowLayout parent, int position, Byte b) {
                TextView textView=new TextView(parent.getContext());
                textView.setGravity(Gravity.CENTER);
                String text="";
                switch (arraysPosition){
                    case 0:
                        text= Integer.toBinaryString(0xff00|b).substring(8);
                        break;
                    case 1:
                        text=b.intValue()+"";
                        break;
                    case 2:
                        text=String.format("%02X",b);
                        break;
                }
                textView.setText(text);
                return textView;
            }
        });
    }

    /**
     * 刷新字符流显示
     */
    private void refreshChars(String chars){
        ArrayList<Character> list=new ArrayList<>();
        for (int i=0;i<chars.length();i++){
            list.add(i,chars.charAt(i));
        }
        mTflChars.setAdapter(new TagAdapter<Character>(list) {
            @Override
            public View getView(FlowLayout parent, int position, Character c) {
                TextView textView=new TextView(parent.getContext());
                textView.setGravity(Gravity.CENTER);
                textView.setText(String.valueOf(c));
                return textView;
            }
        });
    }

    @Override
    protected void injectSelf() {
        activityComponent.inject(this);
        ARouter.getInstance().inject(this);
    }

    /**
     * 当点击时,切换字节流显示方式
     */
    @OnClick(R.id.tfl_bytes)
    public void onViewClicked() {
        arraysPosition=(arraysPosition+1)/decimals.length;
        onItemClick(null,null,mLvGroup.getSelectedItemPosition(),0);
    }
}