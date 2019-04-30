package com.reger.test.user.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class User implements Serializable {
 
	private static final long serialVersionUID = 1L;

	/**
     * 主键id
     */
    @Id
    private String id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户状态
     */
    @Column(name="state")
    private Integer state;

    /**
     * 用户描述
     */
    private String description;

    /**
     * 获取主键id
     *
     * @return id - 主键id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键id
     *
     * @param id 主键id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取用户名
     *
     * @return name - 用户名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置用户名
     *
     * @param name 用户名
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取用户状态
     *
     * @return state - 用户状态
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置用户状态
     *
     * @param state 用户状态
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取用户描述
     *
     * @return description - 用户描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置用户描述
     *
     * @param description 用户描述
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", state=" + state + ", description=" + description + "]";
	}
    
}