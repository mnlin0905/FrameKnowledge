package com.knowledge.mnlin.frame.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.LinkedList;

/**
 * 功能----记事本配置表
 * <p>
 * Created by MNLIN on 2017/11/10.
 */

public class NoteConfigBean extends DataSupport {
    @Column(nullable = false,ignore = false)
    private long createTime;

    private long updateTime;

    @Column(nullable = false)
    private String title;

    /**
     * 记录内容,包括文字图片等等
     */
    private LinkedList<NoteContentBean> content;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LinkedList<NoteContentBean> getContent() {
        return content;
    }

    public void setContent(LinkedList<NoteContentBean> content) {
        this.content = content;
    }
}
