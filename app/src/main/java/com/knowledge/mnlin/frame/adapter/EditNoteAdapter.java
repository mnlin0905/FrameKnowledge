package com.knowledge.mnlin.frame.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.bean.NoteContentBean;

import java.util.List;

/**
 * 功能----编辑便签适配器
 * <p>
 * Created by MNLIN on 2017/11/13.
 */
public class EditNoteAdapter extends RecyclerView.Adapter<EditNoteAdapter.ViewHolder> {

    private Context context;
    private List<NoteContentBean> datas;
    private AdapterView.OnItemClickListener onItemClickListener;

    public EditNoteAdapter(Context context, List<NoteContentBean> datas, AdapterView.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.datas = datas;
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 负责为item创建视图
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_edit_note, parent,false));
    }

    /**
     * 负责将数据绑定到item的视图上
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        NoteContentBean bean = datas.get(position);
        holder.mEtMsg.setVisibility(bean.isString() ? View.VISIBLE : View.GONE);
        holder.mIvImage.setVisibility(bean.isPicture() ? View.VISIBLE : View.GONE);
        if (bean.isString()) holder.mEtMsg.setText(bean.getPathOrData());
        if (bean.isPicture()) {
            Glide.with(context)
                    .load(bean.getPathOrData())
                    .apply(new RequestOptions() {
                        @Override
                        public RequestOptions placeholder(Drawable drawable) {
                            return super.placeholder(R.drawable.loading_icon);
                        }
                    })
                    .into(holder.mIvImage);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * RecyclerView.ViewHolder类，该类必须有
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mEtMsg;
        ImageView mIvImage;

        private ViewHolder(View itemView) {
            super(itemView);
            mEtMsg=itemView.findViewById(R.id.et_msg);
            mIvImage=itemView.findViewById(R.id.iv_image);
            itemView.setOnClickListener(v -> {
                if (!datas.get(getAdapterPosition() - 1).isString())
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(null, itemView, getAdapterPosition() - 1, getAdapterPosition() - 1);
            });
        }
    }
}