package com.knowledge.mnlin.frame.bean;

import org.litepal.crud.DataSupport;

/**
 * 功能----记事本内容表
 * <p>
 * Created by MNLIN on 2017/11/10.
 */

public class NoteContentBean extends DataSupport {
    /**
     * 内容中的顺序
     */
    private int order;

    /**
     * 内容类型
     *
     * 0表示字符串
     * 1表示图片
     */
    private int type;

    /**
     * 如果类型为字符串文本,则该字段表示字符串内容;
     * 如果为图片类型,则表示图片路径;
     */
    private String pathOrData;
}
