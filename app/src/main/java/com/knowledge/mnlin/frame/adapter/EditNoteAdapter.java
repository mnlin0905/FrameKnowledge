package com.knowledge.mnlin.frame.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.activity.EditNoteActivity;
import com.knowledge.mnlin.frame.bean.NoteContentBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 功能----编辑便签适配器
 * <p>
 * Created by MNLIN on 2017/11/13.
 */
public class EditNoteAdapter extends RecyclerView.Adapter<EditNoteAdapter.ViewHolder> {

    private EditNoteActivity context;
    private List<NoteContentBean> datas;
    private AdapterView.OnItemClickListener onItemClickListener;

    public EditNoteAdapter(EditNoteActivity context, List<NoteContentBean> datas, AdapterView.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.datas = datas;
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 负责为item创建视图
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_edit_note, parent, false));
    }

    /**
     * 负责将数据绑定到item的视图上
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        NoteContentBean bean = datas.get(position);
        holder.mEtMsg.setVisibility(bean.isString() ? View.VISIBLE : View.GONE);
        holder.mEtMsg.setTag(position);
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

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.et_msg)
        TextView mEtMsg;
        @BindView(R.id.iv_image)
        ImageView mIvImage;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                if (!datas.get(getAdapterPosition() - 1).isString())
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(null, itemView, getAdapterPosition() - 1, getAdapterPosition() - 1);
            });
            mEtMsg.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (datas.get(getAdapterPosition() - 1).isString()) {
                        datas.get(getAdapterPosition() - 1).setPathOrData(s.toString());
                        context.hasModified = true;
                    }
                }
            });
            mIvImage.setOnLongClickListener(v -> {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("是否删除该图片?")
                        .setContentText("删除操作不可逆转")
                        .setCancelText("放弃")
                        .setConfirmText("删除")
                        .showCancelButton(true)
                        .setConfirmClickListener(sDialog -> {
                            int position = getAdapterPosition() - 1;
                            datas.get(position).delete();
                            datas.get(position - 1).setPathOrData(datas.get(position - 1).getPathOrData() + "\n" + datas.get(position + 1).getPathOrData());
                            datas.get(position + 1).delete();
                            datas.remove(position + 1);
                            datas.remove(position);
                            notifyDataSetChanged();
                            sDialog.dismissWithAnimation();
                        })
                        .setCancelClickListener(SweetAlertDialog::cancel)
                        .show();
                return true;
            });
        }
    }
}