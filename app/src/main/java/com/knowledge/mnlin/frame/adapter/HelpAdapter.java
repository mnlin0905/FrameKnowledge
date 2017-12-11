package com.knowledge.mnlin.frame.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.knowledge.mnlin.collapselayout.CollapseLayout;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * function : 使用帮助的列表
 *
 * @author ACChain
 * @date 2017/12/5
 */

public class HelpAdapter extends BaseAdapter {
    private Context context;
    private List<String> keys;
    private List<String> values;

    public HelpAdapter(Context context, List<String> keys, List<String> values) {
        this.context = context;
        this.keys = keys == null ? new ArrayList<>() : keys;
        this.values = values == null ? new ArrayList<>() : values;
    }

    @Override
    public int getCount() {
        return Math.min(keys.size(), values.size());
    }

    @Override
    public Object getItem(int position) {
        return keys.get(position) + "\n" + values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            CollapseLayout item = new CollapseLayout(parent, context).setAnimatorTogether(true);
            item.setBackground(new ColorDrawable(((BaseActivity) context).dispatchGetColor(R.color.transparent)));
            item.findViewById(R.id.tv_content).setBackgroundColor(((BaseActivity) context).dispatchGetColor(R.color.colorPrimaryDarkHacker));
            ((TextView) item.findViewById(R.id.tv_content)).setTextColor(((BaseActivity) context).dispatchGetColor(R.color.white));
            item.getTitleView().setBackgroundResource(R.drawable.background_transparent);
            item.getTitleView().setTextColor(((BaseActivity) context).dispatchGetColor(R.color.white));
            convertView = item;
        }
        ((CollapseLayout) convertView).setTitleAndContent(keys.get(position), values.get(position));
        return convertView;
    }
}