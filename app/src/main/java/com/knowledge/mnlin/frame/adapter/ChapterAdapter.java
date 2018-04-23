package com.knowledge.mnlin.frame.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.knowledge.mnlin.frame.R;
import com.knowledge.mnlin.frame.base.BaseActivity;
import com.knowledge.mnlin.frame.base.BaseRecyclerViewHolder;
import com.knowledge.mnlin.frame.bean.ChapterBean;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created on 2018/1/12
 * function : 转出记录
 *
 * @author ACChain
 */

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {
    private BaseActivity baseActivity;
    private List<ChapterBean> datas;
    private BaseRecyclerViewHolder.OnViewClickListener listener;

    public ChapterAdapter(BaseActivity baseActivity, List<ChapterBean> datas, BaseRecyclerViewHolder.OnViewClickListener listener) {
        this.baseActivity = baseActivity;
        this.datas = datas;
        this.listener = listener;
    }

    @Override
    public ChapterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChapterAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false), listener);
    }


    @Override
    public void onBindViewHolder(ChapterAdapter.ViewHolder holder, int position) {
        ChapterBean bean = datas.get(position);
        holder.mTvChapterName.setText(String.format(Locale.CHINA, "第%d章 %s", bean.getOrder(), bean.getTitle()));
        holder.mTvCharLength.setText(String.valueOf(bean.getCharLength()));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void notifyDataSetChanged(List<ChapterBean> dataBeans) {
        this.datas = dataBeans;
        notifyDataSetChanged();
    }

    class ViewHolder extends BaseRecyclerViewHolder {
        @BindView(R.id.tv_chapter_name)
        TextView mTvChapterName;
        @BindView(R.id.tv_charLength)
        TextView mTvCharLength;

        ViewHolder(View itemView, OnViewClickListener listener) {
            super(itemView, listener);
        }

        @Override
        protected boolean isXRecyclerView() {
            return true;
        }
    }

}
