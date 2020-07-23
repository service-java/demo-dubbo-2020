package com.zksite.jeeboss.service.system.biz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zksite.common.cache.annotation.CacheParam;
import com.zksite.common.cache.annotation.Cacheable;
import com.zksite.jeeboss.api.system.entity.Role;
import com.zksite.jeeboss.api.system.entity.User;
import com.zksite.jeeboss.service.system.dao.RoleDao;

@Service
public class RoleBizService {

    @Autowired
    private RoleDao roleDao;

    public void deleteUserRole(User user, List<Role> roles) {
        roleDao.deleteUserRoleByRoles(user, roles);
    }

    public void deleteUserRole(User user) {
        roleDao.deleteUserRoles(user);
    }

    public void addUserRole(User user, Role role) {
        roleDao.addUserRole(user, role);
    }

    public void deleteRoleResources(Role role) {
        roleDao.deleteRoleResources(role);
    }

    public void add(Role role) {
        roleDao.insert(role);
    }

    public void deleteUserRoleByRole(Role role) {
        roleDao.deleteUserRoleByRole(role);
    }

    public int countByOrgId(Integer orgId) {
        return roleDao.countByOrgId(orgId);
    }

    @Cacheable(key = "system:role:user:", timeout = 30 * 60, deserializeClass = Role.class)
    public List<Role> findByUserId(@CacheParam Integer userId) {
        return roleDao.findByUserId(userId);
    }

    @Cacheable(key = "system:role:user:", remove = true)
    public void refreshUserRole(@CacheParam Integer userId) {

    }

    public void update(Role role) {
        roleDao.update(role);
    }

    public List<Role> find(Role role) {
        return roleDao.findList(role);
    }

    public Role get(Integer id) {
        return roleDao.get(id);
    }

    public void delete(Integer id) {
        roleDao.delete(id);
    }

    public void deleteUserRoleByUserId(Integer userId) {
        roleDao.deleteUserRoleByUserId(userId);
    }

}
