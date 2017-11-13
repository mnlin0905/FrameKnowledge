package com.knowledge.mnlin.frame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knowledge.mnlin.frame.R;

/**
 * Created by admin on 2017/4/12.
 * <p>
 * 自定义横向的menu菜单，提供一个文本框，一个图标区，一个开关按钮
 */
public class LineMenuView extends LinearLayout {
    private static final String TAG = "LineMenuView";
    private Context context;
    private AttributeSet attrs;
    public LinearLayout rootView;
    public TextView tv_menu;
    public SwitchCompat sc_switch;

    public LineMenuView(Context context) {
        this(context, null);
    }

    public LineMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        initView();
        initData();
    }

    private void initView() {
        rootView = (LinearLayout) inflate(context, R.layout.layout_line_menu, this);
        tv_menu = (TextView) rootView.findViewById(R.id.tv_menu);
        sc_switch = (SwitchCompat) rootView.findViewById(R.id.sc_switch);
    }

    private void initData() {
        TypedArray params = context.obtainStyledAttributes(attrs, R.styleable.LineMenuView);

        //查看当前是否需要显示switch,默认显示为off状态
        //查看当前icon资源文件，如果未设定则默认不显示
        //获取menu需要显示的内容
        for (int i = params.getIndexCount() - 1; i >= 0; i--) {
            switch (params.getIndex(i)) {
                case R.styleable.LineMenuView_LineMenuView_switch: {
                    int switchValue = params.getInt(R.styleable.LineMenuView_LineMenuView_switch, 0);
                    //配置switch
                    switch (switchValue) {
                        case 2: {
                            sc_switch.setVisibility(View.GONE);
                            break;
                        }
                        case 1: {
                            sc_switch.setChecked(true);
                            break;
                        }
                    }
                    break;
                }
                case R.styleable.LineMenuView_LineMenuView_icon: {
                    Drawable d = params.getDrawable(R.styleable.LineMenuView_LineMenuView_icon);
                    d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
                    tv_menu.setCompoundDrawables(d, null, null, null);
                    break;
                }
                case R.styleable.LineMenuView_LineMenuView_menu: {
                    tv_menu.setText(params.getString(R.styleable.LineMenuView_LineMenuView_menu));
                    break;
                }
            }
        }
        params.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
