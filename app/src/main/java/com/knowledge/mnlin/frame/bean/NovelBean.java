package com.knowledge.mnlin.frame.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created on 2018/4/10
 * function : 小说bean
 *
 * @author ACChain
 */

public class NovelBean extends DataSupport {
    /**
     * 小说标题
     * 小说网址
     * 作者
     * 简介
     * 创建时间
     * 完结时间
     * 小说总字数
     * 章节数
     */
    private String title;
    @Column(unique = true, nullable = false)
    private String url;
    private String author;
    private String introduce;
    private long createTime;
    private long endTime;
    private int charLength;
    private int chapterLength;
    private List<ChapterBean> chapters;

    public List<ChapterBean> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterBean> chapters) {
        this.chapters = chapters;
    }

    public String getTitle() {
        return title;
    }

    public String setTitle(String title) {
        this.title = title;
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getCharLength() {
        return charLength;
    }

    public void setCharLength(int charLength) {
        this.charLength = charLength;
    }

    public int getChapterLength() {
        return chapterLength;
    }

    public void setChapterLength(int chapterLength) {
        this.chapterLength = chapterLength;
    }
}
