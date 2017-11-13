package com.knowledge.mnlin.frame.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * 功能----记事本内容表
 * <p>
 * Created by MNLIN on 2017/11/10.
 */

public class NoteContentBean extends DataSupport {
    @Column(ignore = true)
    public static final int TYPE_STRING=0;

    @Column(ignore = true)
    public static final int TYPE_PICTURE=1;

    @Column(unique = true)
    private long id;

    /**
     * 内容中的顺序
     */
    private int order;

    /**
     * 内容类型
     *
     * 0 表示字符串
     * 1 表示图片
     */
    private int type;

    /**
     * 如果类型为字符串文本,则该字段表示字符串内容;
     * 如果为图片类型,则表示图片路径;
     */
    private String pathOrData;

    public NoteContentBean( int order, int type, String pathOrData) {
        this.order = order;
        this.type = type;
        this.pathOrData = pathOrData;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getType() {
        return type;
    }

    public boolean isString(){
        return type==TYPE_STRING;
    }

    public boolean isPicture(){
        return type==TYPE_PICTURE;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPathOrData() {
        return pathOrData;
    }

    public void setPathOrData(String pathOrData) {
        this.pathOrData = pathOrData;
    }
}
