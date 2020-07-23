package com.zksite.jeeboss.api.system.entity;

import java.io.Serializable;
import java.util.Date;

import com.zksite.common.mybatis.Page;

/**
 * @Title: Entity
 * @Description: 用户
 * @date 2018-01-04 15:25:54
 * @version V1.0
 */

public class User implements Serializable {

    private static final long serialVersionUID = -6820739887542365572L;

    /**
     * createDate
     */
    private Date createDate;

    /**
     * 组织机构
     */
    private Integer orgId;

    /**
     * 头像
     */
    private String photo;

    /**
     * 电话
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * email
     */
    private String email;

    /**
     * 性别,0:女 1:男
     */
    private Integer sex;

    /**
     * age
     */
    private Integer age;

    /**
     * name
     */
    private String name;

    /**
     * id
     */
    private Integer id;

    private Org org;
    
    private String nickname;
    
    private Page<User> page;

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
     * 获取:组织机构
     */

    public Integer getOrgId() {
        return this.orgId;
    }

    /**
     * 设置:组织机构
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取:头像
     */

    public String getPhoto() {
        return this.photo;
    }

    /**
     * 设置:头像
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * 获取:电话
     */

    public String getPhone() {
        return this.phone;
    }

    /**
     * 设置:电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取:密码
     */

    public String getPassword() {
        return this.password;
    }

    /**
     * 设置:密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取:email
     */

    public String getEmail() {
        return this.email;
    }

    /**
     * 设置:email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取:性别,0:女 1:男
     */

    public Integer getSex() {
        return this.sex;
    }

    /**
     * 设置:性别,0:女 1:男
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取:age
     */

    public Integer getAge() {
        return this.age;
    }

    /**
     * 设置:age
     */
    public void setAge(Integer age) {
        this.age = age;
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

    public Page<User> getPage() {
        return page;
    }

    public void setPage(Page<User> page) {
        this.page = page;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
}
