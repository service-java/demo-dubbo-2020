package com.zksite.jeeboss.api.system.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title: Entity
 * @Description: 角色
 * @date 2018-01-04 15:16:35
 * @version V1.0
 */

public class Role implements Serializable {


    private static final long serialVersionUID = 6660405010558552337L;

    /**
     * createDate
     */
    private Date createDate;

    /**
     * orgId
     */
    private Integer orgId;

    /**
     * name
     */
    private String name;

    /**
     * id
     */
    private Integer id;

    
    private Org org;

    private String permission;
    
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
     * 获取:orgId
     */

    public Integer getOrgId() {
        return this.orgId;
    }

    /**
     * 设置:orgId
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
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

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
    
}
