package com.knowledge.mnlin.frame.view;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knowledge.mnlin.frame.R;


/**
 * 功能---- 可折叠的布局
 * <p>
 * Created by MNLIN on 2017/11/20.
 */

public class CollapseLayout extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "CollapseLayout";

    private Context context;
    private AttributeSet attrs;
    private TextView tv_title, tv_content;

    //是否处于折叠状态
    private boolean isCollapsed = true;

    //是否是第一次初始化视图
    private boolean isFirstLayout = true;

    //父布局的id
    private ViewGroup parent;

    public CollapseLayout(Context context) {
        this(context, null);
    }

    public CollapseLayout(ViewGroup parent, Context context) {
        this(context);
        this.parent = parent;
    }

    public CollapseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CollapseLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        inflate(context, R.layout.layout_collapse, this);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        setBackground(getResources().getDrawable(R.drawable.selector_background_white_pressed_little_gray, null));
        setOrientation(VERTICAL);

        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);

        /*
        * 获取布局的框架父管理布局
        * */
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CollapseLayout);
        @IdRes final int collapseLayoutParent = array.getResourceId(R.styleable
                .CollapseLayout_CollapseLayout_parent, -1);
        if (collapseLayoutParent != -1) {
            post(() -> parent = CollapseLayout.this.getRootView().findViewById(collapseLayoutParent));
        }
        array.recycle();

        setOnClickListener(this);
    }

    @Override
    public final void onClick(View v) {
        if (isCollapsed) {
            if (parent != null) {
                collapseChild(parent);
            }
            expand(this);
        } else {
            collapse(this);
        }
    }

    /**
     * 进行折叠
     */
    private void collapse(final CollapseLayout layout) {
        layout.isCollapsed = true;
        int width = layout.tv_content.getMeasuredWidth();
        layout.tv_content.animate()
                .x(width)
                .setDuration(1000)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        layout.tv_content.animate().setListener(null);
                        layout.tv_content.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    /**
     * 打开折叠内容
     */
    private void expand(final CollapseLayout layout) {
        layout.isCollapsed = false;
        if (!isFirstLayout) {
            layout.tv_content.setVisibility(View.VISIBLE);
            layout.tv_content
                    .animate()
                    .x(0)
                    .setDuration(1000);
        } else {
            isFirstLayout = false;
            layout.tv_content.setVisibility(View.INVISIBLE);
            layout.tv_content.post(() -> layout.tv_content
                    .animate()
                    .x(layout.tv_content.getRight())
                    .setDuration(0)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            layout.tv_content.setVisibility(View.VISIBLE);
                            layout.tv_content.animate().setListener(null);
                            layout.tv_content.animate().x(0).setDuration(1000);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    }));
        }
    }

    /**
     * 将子View中CollapseLayout类型的进行折叠
     */
    private void collapseChild(ViewGroup parent) {
        int childAmount = parent.getChildCount();
        CollapseLayout item = null;
        for (int i = 0; i < childAmount; i++) {
            View view = parent.getChildAt(i);
            if (view instanceof CollapseLayout) {
                item = (CollapseLayout) view;
                if (!item.isCollapsed) {
                    collapse(item);
                }
            } else if (view instanceof ViewGroup) {
                collapseChild((ViewGroup) view);
            }
        }
    }

    /**
     * 设置标题和内容
     */
    public void setTitleAndContent(CharSequence title, CharSequence content) {
        tv_title.setText(title);
        tv_content.setText(content);
    }
}
