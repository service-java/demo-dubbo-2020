package com.zksite.jeeboss.api.system.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title: Entity
 * @Description: 机构
 * @date 2018-01-04 15:24:52
 * @version V1.0
 */

public class Org implements Serializable {
    private static final long serialVersionUID = -8155470196253880681L;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * createDate
     */
    private Date createDate;

    /**
     * name
     */
    private String name;

    /**
     * parentId
     */
    private Integer parentId;

    /**
     * id
     */
    private Integer id;


    /**
     * 获取:排序
     */

    public Integer getSort() {
        return this.sort;
    }

    /**
     * 设置:排序
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取:createDate
     */

    public Date getCreateDate() {
        return this.createDate;
    }

    /**
     * 设置:createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 获取:name
     */

    public String getName() {
        return this.name;
    }

    /**
     * 设置:name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取:parentId
     */

    public Integer getParentId() {
        return this.parentId;
    }

    /**
     * 设置:parentId
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取:id
     */

    public Integer getId() {
        return this.id;
    }

    /**
     * 设置:id
     */
    public void setId(Integer id) {
        this.id = id;
    }
}
