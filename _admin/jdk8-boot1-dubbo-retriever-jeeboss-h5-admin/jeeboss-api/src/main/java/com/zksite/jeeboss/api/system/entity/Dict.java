package com.zksite.jeeboss.api.system.entity;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.zksite.common.mybatis.Page;

/**
 * @Title: Entity
 * @Description: 字典
 * @date 2018-03-05 10:23:39
 * @version V1.0
 */

public class Dict implements Serializable {


    private static final long serialVersionUID = 884607304959713581L;

    /**
     * updateDate
     */
    private Date updateDate;

    /**
     * createDate
     */
    private Date createDate;

    /**
     * 序号
     */
    @NotNull(message = "序号不能为空")
    private Integer sort;

    /**
     * 类型
     */
    @NotBlank(message = "类型不能为空")
    private String type;

    /**
     * 标签
     */
    @NotBlank(message = "标签不能为空")
    private String label;

    /**
     * value
     */
    @NotBlank(message = "value不能为空")
    private String value;

    /**
     * id
     */
    private Integer id;


    private Page<Dict> page;

    /**
     * 获取:updateDate
     */

    public Date getUpdateDate() {
        return this.updateDate;
    }

    /**
     * 设置:updateDate
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
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
     * 获取:序号
     */

    public Integer getSort() {
        return this.sort;
    }

    /**
     * 设置:序号
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取:类型
     */

    public String getType() {
        return this.type;
    }

    /**
     * 设置:类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取:标签
     */

    public String getLabel() {
        return this.label;
    }

    /**
     * 设置:标签
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * 获取:value
     */

    public String getValue() {
        return this.value;
    }

    /**
     * 设置:value
     */
    public void setValue(String value) {
        this.value = value;
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

    public Page<Dict> getPage() {
        return page;
    }

    public void setPage(Page<Dict> page) {
        this.page = page;
    }

}
