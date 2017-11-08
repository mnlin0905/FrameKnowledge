package com.knowledge.mnlin.frame.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.knowledge.mnlin.frame.R;

import java.util.ArrayList;

/**
 * 功能---- 左侧列表ListView的适配器
 * <p>
 * Created by MNLIN on 2017/11/8.
 */

public class AnalyzeByteAdapter extends BaseAdapter {
    private ArrayList<String> data;
    private Context context;

    public AnalyzeByteAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            TextView tv=new TextView(context);
            int padding=context.getResources().getDimensionPixelSize(R.dimen.prefer_view_padding_vertical);
            tv.setPadding(padding,padding,padding,padding);
            tv.setGravity(Gravity.CENTER);
            convertView=tv;
        }
        ((TextView) convertView).setText(data.get(position));
        return convertView;
    }
}
