package com.zksite.jeeboss.api.system.entity;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.zksite.common.mybatis.Page;

/**
 * @Title: Entity
 * @Description: 资源
 * @date 2018-01-04 15:01:23
 * @version V1.0
 */

public class Resource implements Serializable {


    private static final long serialVersionUID = 7348463574900996214L;

    /**
     * sort
     */
    private Integer sort;

    /**
     * createDate
     */
    private Date createDate;

    /**
     * 权限表示
     */
    @NotBlank(message = "权限表示不能为空")
    private String permission;

    /**
     * url
     */
    private String url;

    /**
     * 类型,0:菜单 1:功能
     */
    @NotNull(message = "资源类型不能为空")
    private Integer type;

    /**
     * name
     */
    @NotBlank(message = "资源名称不能为空")
    private String name;

    /**
     * parentId
     */
    private Integer parentId;

    /**
     * id
     */
    private Integer id;

    private Page<Resource> page;

    private String icon;
    
    /**
     * 获取:sort
     */

    public Integer getSort() {
        return this.sort;
    }

    /**
     * 设置:sort
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
     * 获取:权限表示
     */

    public String getPermission() {
        return this.permission;
    }

    /**
     * 设置:权限表示
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * 获取:url
     */

    public String getUrl() {
        return this.url;
    }

    /**
     * 设置:url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取:类型,0:菜单 1:功能
     */

    public Integer getType() {
        return this.type;
    }

    /**
     * 设置:类型,0:菜单 1:功能
     */
    public void setType(Integer type) {
        this.type = type;
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


    public Page<Resource> getPage() {
        return page;
    }

    public void setPage(Page<Resource> page) {
        this.page = page;
    }

    
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + name.hashCode();
        if (id != null)
            result = result * 31 + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Resource)) {
            return false;
        }
        Resource r = (Resource) obj;
        return this.getId().equals(r.getId());
    }


}
