package com.knowledge.mnlin.frame.bean;

import org.litepal.crud.DataSupport;

/**
 * Created on 2018/4/10
 * function : 章节bean
 *
 * @author ACChain
 */

public class ChapterBean extends DataSupport {
    /**
     * 网络对应url
     * 第n章
     * 标题
     * 字数
     * 发布时间
     * 内容
     * <p>
     * 外键
     */
    private String url;
    private int order;
    private String title;
    private int charLength;
    private long publishTime;
    private String content;

    private NovelBean novelBean;

    public ChapterBean(){

    }

    public ChapterBean(String url, int order, String title) {
        this.url = url;
        this.order = order;
        this.title = title;
    }

    public NovelBean getNovelBean() {
        return novelBean;
    }

    public ChapterBean setNovelBean(NovelBean novelBean) {
        this.novelBean = novelBean;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCharLength() {
        return charLength;
    }

    public void setCharLength(int charLength) {
        this.charLength = charLength;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public String getContent() {
        return content;
    }

    public String setContent(String content) {
        this.content = content;
        return content;
    }
}
