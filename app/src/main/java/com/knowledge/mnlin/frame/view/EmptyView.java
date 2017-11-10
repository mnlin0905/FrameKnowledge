package com.knowledge.mnlin.frame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.knowledge.mnlin.frame.R;

/**
 * 功能----空布局
 * <p>
 * Created by MNLIN on 2017/9/18.
 */

public class EmptyView extends LinearLayout {
    public EmptyView(Context context) {
        super(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_empty_view, this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setOrientation(VERTICAL);
    }
}
