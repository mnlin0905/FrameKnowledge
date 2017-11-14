package com.knowledge.mnlin.frame.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.bean.NoteConfigBean;
import com.knowledge.mnlin.frame.util.SparseBooleanArray;
import com.knowledge.mnlin.frame.view.CircleTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.tencent.smtt.sdk.TbsReaderView.TAG;

/**
 * 功能---- 记事本列表
 * <p>
 * Created by MNLIN on 2017/11/8.
 */

public class ManageNoteAdapter extends RecyclerView.Adapter<ManageNoteAdapter
        .ViewHolder> {
    /**
     * 总资源文件，
     * 布局文件，
     * 单击模式和多选模式，
     * 某位置是否为选中状态，
     * 上下文对象
     */
    private List<NoteConfigBean> data;
    private boolean isMultiplyMode;
    public SparseBooleanArray selectedPosition;
    private Context context;

    /**
     * 自定义点击 类，因为RecyclerView没有onItemClick事件，因此需要自己定义
     */
    public interface OnItemClickListener {
        void doOnRecyclerViewItemClick(View v, int position);

        void doOnSelectedAmountChanged(int amount);

        void doOnMultiplyModeChanged(boolean isMultiplyMode);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ManageNoteAdapter(Context context, List<NoteConfigBean> data) {
        this.data = data;
        this.selectedPosition = new SparseBooleanArray(data.size());
        this.context = context;
    }

    /**
     * 通知数据需要进行刷新
     */
    public void notifyDataSetChanged(List<NoteConfigBean> datas) {
        if (datas == null) return;
        this.data = datas;
        notifyDataSetChanged(this.data.size());
    }

    /**
     * 通知数据需要进行刷新
     */
    public void notifyDataSetChanged(int size) {
        this.selectedPosition.resize(size);
        this.selectedPosition.initValue(false);
        notifyDataSetChanged();
    }

    /**
     * 负责为item创建视图
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_note, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 负责将数据绑定到item的视图上
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.v(TAG, "onBindViewHolder: ");
        NoteConfigBean bean = data.get(position);

        //序号
        holder.ctv_icon.setText(String.valueOf(getItemCount() - position - 1));

        //标题,创建时间,更新时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
        holder.tv_title.setText(bean.getTitle());
        holder.tv_create_time.setText(format.format(new Date(bean.getCreateTime())));
        holder.tv_update_time.setText(format.format(new Date(bean.getUpdateTime())));

        //如果是单击模式，则显示ImageView，否则显示RadioButton
        holder.rb_select.setVisibility(isMultiplyMode ? View.VISIBLE : View.GONE);
        holder.iv_select.setVisibility(isMultiplyMode ? View.GONE : View.VISIBLE);
        holder.rb_select.setChecked(selectedPosition.get(position));
        holder.rootView.setSelected(selectedPosition.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * RecyclerView.ViewHolder类，该类必须有
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton
            .OnCheckedChangeListener, View.OnLongClickListener {

        private TextView tv_create_time, tv_title, tv_update_time;
        private View rootView;
        private RadioButton rb_select;
        private ImageView iv_select;
        private CircleTextView ctv_icon;

        private ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            rb_select = itemView.findViewById(R.id.rb_select);
            iv_select = itemView.findViewById(R.id.iv_select);
            ctv_icon = itemView.findViewById(R.id.ctv_icon);
            tv_create_time = itemView.findViewById(R.id.tv_create_time);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_update_time = itemView.findViewById(R.id.tv_update_time);
            rb_select.setOnCheckedChangeListener(this);
            iv_select.setOnClickListener(this);
            rootView.setOnClickListener(this);
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition() - 1;
            switch (v.getId()) {
                case R.id.ll_rootView: {
                    if (isMultiplyMode) {
                        //多选模式下，直接默认选中或取消对应的item记录
                        rb_select.setChecked(!selectedPosition.get(position));
                    } else if (listener != null) {
                        listener.doOnRecyclerViewItemClick(v, position);
                    }
                    break;
                }
                case R.id.iv_select: {
                    rb_select.setChecked(true);
                    setMultiplyMode(true);
                    break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            //如果是长点击，表示需要由点击模式切换到多选模式
            if (!isMultiplyMode) {
                iv_select.performClick();
                return true;
            }
            return false;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.rb_select: {
                    selectedPosition.put(getAdapterPosition() - 1, isChecked);
                    rootView.setSelected(isChecked);
                    if (listener != null) {
                        listener.doOnSelectedAmountChanged(selectedPosition.countNumber(true));
                    }
                    break;
                }
            }
        }
    }

    /**
     * 切换多选或者点击模式
     */
    public void setMultiplyMode(boolean isMultiplyMode) {
        if (isMultiplyMode != this.isMultiplyMode) {
            this.isMultiplyMode = isMultiplyMode;
            if (listener != null) {
                listener.doOnMultiplyModeChanged(this.isMultiplyMode);
            }
            if (!this.isMultiplyMode) {
                selectedPosition.initValue(false);
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 是否处于多选模式
     */
    public boolean isMultiplyMode() {
        return this.isMultiplyMode;
    }

    /**
     * 装饰
     */
    public static class ItemDecoration extends RecyclerView.ItemDecoration{
        private int dividerSize;
        private int color;
        private Paint paint;

        public ItemDecoration(Context context){
            dividerSize = context.getResources().getDimensionPixelSize(R.dimen.divider_line_width);
            color = context.getResources().getColor(R.color.color_divider_line);
            paint = new Paint();
            paint.setColor(color);
            paint.setAlpha(200);
            paint.setAntiAlias(true);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            drawVertical(c,parent);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
                state) {
            outRect.set(0, 0, 0, dividerSize);
        }

        /**
         * 绘制纵向布局(底部绘制)
         */
        private void drawVertical(Canvas c,RecyclerView parent){
            int left=parent.getPaddingLeft();
            int right=parent.getRight()-parent.getPaddingRight();
            int childAmount=parent.getChildCount();
            for(int i=0;i<childAmount;i++){
                View child=parent.getChildAt(i);
                RecyclerView.LayoutParams params= (RecyclerView.LayoutParams) child.getLayoutParams();
                int top=child.getBottom()+params.bottomMargin;
                int bottom=top+1;
                if(i==0)paint.setColor(Color.RED);else paint.setColor(color);
                c.drawLine(left,bottom,right,bottom,paint);
            }
        }
    }
}
