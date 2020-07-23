package com.zksite.jeeboss.service.system.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.zksite.jeeboss.api.system.entity.Role;
import com.zksite.jeeboss.api.system.entity.User;

@Repository
public interface RoleDao {

    @Delete(" DELETE FROM `sys_user_role` WHERE user_id=#{id}")
    void deleteUserRoles(User user);

    void deleteUserRoleByRoles(@Param("user") User user, @Param("roles") List<Role> roles);

    @Insert("INSERT `sys_user_role` (user_id,role_id) VALUES(#{user.id},#{role.id});")
    void addUserRole(@Param("user") User user, @Param("role") Role role);

    @Delete("DELETE FROM `sys_role_resource` WHERE role_id=#{id}")
    void deleteRoleResources(Role role);

    void insert(Role role);

    @Delete("DELETE FROM `sys_user_role` WHERE role_id=#{id}")
    void deleteUserRoleByRole(Role role);

    @Select("SELECT COUNT(1) FROM `sys_role` r WHERE r.`org_id`=#{orgId}")
    int countByOrgId(@Param("orgId") Integer orgId);

    @Select("SELECT * FROM `sys_role` r LEFT JOIN `sys_user_role` ur ON ur.`role_id`=r.`id` WHERE ur.`user_id`=#{userId}")
    List<Role> findByUserId(@Param("userId") Integer userId);

    void update(Role role);

    List<Role> findList(Role role);

    Role get(@Param("id") Integer id);

    void delete(@Param("id") Integer id);

    @Delete("DELETE FROM `sys_user_role` WHERE user_id=#{userId}")
    void deleteUserRoleByUserId(@Param("userId") Integer userId);

}
