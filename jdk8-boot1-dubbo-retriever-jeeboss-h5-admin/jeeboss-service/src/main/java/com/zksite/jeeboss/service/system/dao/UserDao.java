package com.zksite.jeeboss.service.system.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.zksite.jeeboss.api.system.entity.User;

@Repository
public interface UserDao {

    void insert(User user);

    @Select("SELECT COUNT(1) FROM `sys_user` u WHERE u.`org_id`=#{orgId}")
    int countByOrgId(@Param("orgId") Integer orgId);

    List<User> findList(User user);

    User get(@Param("id") Integer id);

    void update(User user);

    void delete(User user);

    @Select("SELECT * FROM `sys_user` u WHERE u.`name`=#{name}")
    User getByName(@Param("name") String name);

}
