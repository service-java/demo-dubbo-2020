package com.zksite.jeeboss.service.system.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.zksite.jeeboss.api.system.entity.Resource;

@Repository
public interface ResourceDao {

    void insert(Resource resource);

    @Delete("DELETE FROM `sys_role_resource` WHERE resource_id=#{id}")
    void deleteRoleResourceByResource(@Param("id") Integer id);

    void delete(@Param("id") Integer id);

    List<Resource> findByRoleId(@Param("roleId") Integer roleId);

    @Delete("DELETE FROM `sys_role_resource` WHERE role_id=#{roleId}")
    void deleteRoleResourceByRoleId(@Param("roleId") Integer roleId);

    void addRoleResources(@Param("roleId") Integer roleId,
            @Param("resources") List<Resource> resources);

    List<Resource> findList(Resource resource);

    Resource get(@Param("id") Integer id);

    void update(Resource resource);

    @Select("SELECT role_id FROM `sys_role_resource` rr WHERE rr.`resource_id`=#{id}")
    List<Integer> findRoleResourceByResourceId(@Param("id") Integer id);

}
