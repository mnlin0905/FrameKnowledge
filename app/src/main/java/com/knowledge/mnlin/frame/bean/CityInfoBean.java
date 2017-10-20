package com.knowledge.mnlin.frame.bean;

import java.util.ArrayList;

/**
 * 功能----从网络获取省市区的列表信息
 * <p>
 * Created by MNLIN on 2017/10/16.
 */

public class CityInfoBean {

    /**
     * name : 北京
     * sub : [{"name":"请选择"},{"name":"东城区"},{"name":"西城区"},{"name":"崇文区"},{"name":"宣武区"},{"name":"朝阳区"},{"name":"海淀区"},{"name":"丰台区"},{"name":"石景山区"},{"name":"房山区"},{"name":"通州区"},{"name":"顺义区"},{"name":"昌平区"},{"name":"大兴区"},{"name":"怀柔区"},{"name":"平谷区"},{"name":"门头沟区"},{"name":"密云县"},{"name":"延庆县"},{"name":"其他"}]
     * type : 0
     */

    private String name;
    private int type;
    private ArrayList<CityInfoBean> sub;

    public CityInfoBean(String name, int type, ArrayList<CityInfoBean> sub) {
        this.name = name;
        this.type = type;
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<CityInfoBean> getSub() {
        CityInfoBean temp;
        if (sub != null) {
            for (int i = 0; i < sub.size(); i++) {
                temp = sub.get(i);
                if (temp.requireDelete()) {
                    sub.remove(i--);
                }
            }
        }
        return sub;
    }

    public CityInfoBean setSub(ArrayList<CityInfoBean> sub) {
        this.sub = sub;
        return this;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * 是否有子区域存在
     */
    public boolean hasSub() {
        return sub != null && sub.size() != 0;
    }

    /**
     * @return 是否需要删除该item
     */
    public boolean requireDelete() {
        return name.equals("请选择") || name.equals("其他");
    }
}
