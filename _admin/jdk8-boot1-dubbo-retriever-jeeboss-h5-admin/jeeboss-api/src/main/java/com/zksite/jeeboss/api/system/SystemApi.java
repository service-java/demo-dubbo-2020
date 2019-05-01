package com.zksite.jeeboss.api.system;

import java.util.List;

import com.zksite.common.exception.BizException;
import com.zksite.common.mybatis.Page;
import com.zksite.jeeboss.api.system.entity.Dict;
import com.zksite.jeeboss.api.system.entity.Org;
import com.zksite.jeeboss.api.system.entity.Resource;
import com.zksite.jeeboss.api.system.entity.Role;
import com.zksite.jeeboss.api.system.entity.User;

public interface SystemApi {

    /**
     * 添加一个资源
     * 
     * @param resource
     * @throws BizException
     */
    void addResource(Resource resource) throws BizException;

    /**
     * 更新资源
     * 
     * @param resource
     * @throws BizException
     */
    void updateResource(Resource resource) throws BizException;

    /**
     * 删除一个资源
     * 
     * @param resource
     */
    void deleteResource(Integer id);

    /**
     * 通过角色Id获取资源
     * 
     * @param roleId
     * @return
     */
    List<Resource> findByRoleId(Integer roleId);

    /**
     * 通过角色Id获取资源
     * 
     * @param roleId
     * @return
     */
    List<Resource> findByRoleIds(List<Integer> roleIds);

    /**
     * 用户授权<br>
     * 此操作将为用户重新授权，当授权完成后，角色拥有的角色将是给定的roles
     * 
     * @param user
     * @param roles
     */
    void userAuthorize(User user, List<Role> roles);

    /**
     * 角色授权<br>
     * 此操作将为角色重新授权，当授权完成后，角色拥有的资源将是给定的resources
     * 
     * @param role
     * @param resources
     */
    void roleAuthorize(Role role, List<Resource> resources);

    /**
     * 添加一个机构
     * 
     * @param org
     * @throws BizException
     */
    void addOrg(Org org) throws BizException;

    /**
     * 添加一个用户
     * 
     * @param user
     * @throws BizException
     */
    void addUser(User user) throws BizException;

    /**
     * 添加一个用户并授权
     * 
     * @param user
     * @param roles
     * @throws BizException
     */
    void addUser(User user, List<Role> roles) throws BizException;

    /**
     * 添加一个角色
     * 
     * @param role
     * @throws BizException
     */
    void addRole(Role role) throws BizException;

    /**
     * 添加一个角色并授权
     * 
     * @param role
     * @param resources
     * @throws BizException
     */
    void addRole(Role role, List<Resource> resources) throws BizException;

    /**
     * 删除角色<br>
     * 如果有用户被授权此角色，此操作将会首先取消用户的授权
     * 
     * @param role
     */
    void deleteRole(Role role);

    /**
     * 删除机构<br>
     * 如果此机构有用户或角色关联，将抛异常
     * 
     * @param org
     * @exception BizException
     */
    void deleteOrg(Org org) throws BizException;

    /**
     * 取消用户的授权
     * 
     * @param user
     * @param roles
     */
    void userUnauthorization(User user, List<Role> roles);

    /**
     * 取消用户的授权
     * 
     * @param user
     * @param roles
     */
    void userUnauthorization(User user, Role role);

    /**
     * 获取用户的所有角色
     * 
     * @param user
     * @return
     */
    List<Role> findRoleByUserId(Integer userId);

    /**
     * 根据条件查找资源
     * 
     * @param resource
     * @return
     */
    List<Resource> findResources(Resource resource);


    /**
     * 获取一个资源
     * 
     * @param id
     * @return
     */
    Resource getResource(Integer id);

    /**
     * 查找org
     * 
     * @param org
     * @return
     */
    List<Org> findOrgs(Org org);

    /**
     * 获取一个机构
     * 
     * @param id
     * @return
     */
    Org getOrg(Integer id);

    /**
     * 更新机构
     * 
     * @param org
     * @throws BizException
     */
    void updateOrg(Org org) throws BizException;

    /**
     * 更新角色
     * 
     * @param role
     */
    void updateRole(Role role);

    /**
     * 查找角色
     * 
     * @param role
     * @return
     */
    List<Role> findRoles(Role role);

    /**
     * 获取角色
     * 
     * @param id
     * @return
     */
    Role getRole(Integer id);

    /**
     * 分页查找用户
     * 
     * @param user
     * @return
     */
    Page<User> find(User user);

    /**
     * 获取用户
     * 
     * @param id
     * @return
     */
    User getUser(Integer id);

    /**
     * 更新用户
     * 
     * @param user
     */
    void updateUser(User user);

    /**
     * 删除用户
     * 
     * @param user
     */
    void deleteUser(User user);

    /**
     * 通过用户名获取用户
     * 
     * @param name
     * @return
     * @throws BizException
     */
    User getUserByName(String name) throws BizException;

    /**
     * 查找用户
     * 
     * @param user
     * @return
     */
    List<User> find2List(User user);

    /**
     * 获取字典列表
     * 
     * @param dict
     * @return
     */
    Page<Dict> findDictByPage(Dict dict);

    /**
     * 通过字典类型获取字典列表
     * 
     * @param type
     * @return
     */
    List<Dict> findDictByType(String type);

    /**
     * 获取字典
     * 
     * @param type
     * @param value
     * @return
     */
    Dict getDict(String type, String value);

    /**
     * 添加字典
     * 
     * @param dict
     * @throws BizException
     */
    void addDict(Dict dict) throws BizException;

    /**
     * 更新字典
     * 
     * @param dict
     * @throws BizException
     */
    void updateDict(Dict dict) throws BizException;

    /**
     * 获取字典
     * 
     * @param id
     * @return
     */
    Dict getDict(Integer id);

    /**
     * 删除字典
     * 
     * @param id
     */
    void deleteDict(Integer id);

}
