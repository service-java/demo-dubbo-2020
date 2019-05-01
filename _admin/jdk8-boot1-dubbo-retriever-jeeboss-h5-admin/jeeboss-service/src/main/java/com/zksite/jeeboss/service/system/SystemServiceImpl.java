package com.zksite.jeeboss.service.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.zksite.common.constant.ErrorCode;
import com.zksite.common.exception.BizException;
import com.zksite.common.mybatis.Page;
import com.zksite.common.validate.ValidateService;
import com.zksite.jeeboss.api.system.SystemApi;
import com.zksite.jeeboss.api.system.entity.Dict;
import com.zksite.jeeboss.api.system.entity.Org;
import com.zksite.jeeboss.api.system.entity.Resource;
import com.zksite.jeeboss.api.system.entity.Role;
import com.zksite.jeeboss.api.system.entity.User;
import com.zksite.jeeboss.service.system.biz.DictBizService;
import com.zksite.jeeboss.service.system.biz.OrgBizService;
import com.zksite.jeeboss.service.system.biz.ResourceBizService;
import com.zksite.jeeboss.service.system.biz.RoleBizService;
import com.zksite.jeeboss.service.system.biz.UserBizService;

@Service
public class SystemServiceImpl implements SystemApi {

    @Autowired
    private UserBizService userBizService;

    @Autowired
    private RoleBizService roleBizService;

    @Autowired
    private ResourceBizService resourceBizService;

    @Autowired
    private OrgBizService orgBizService;

    @Autowired
    private ValidateService validateService;

    @Autowired
    private DictBizService dictBizService;

    public void addResource(Resource resource) throws BizException {
        if (resource == null)
            return;
        validateService.validate(resource);
        if (resource.getParentId() != null) {
            List<Integer> roleIds =
                    resourceBizService.findRoleResourceByResourceId(resource.getParentId());
            for (Integer roleId : roleIds) {
                resourceBizService.refreshRoleResouce(roleId);
            }
        }
        resourceBizService.add(resource);
    }

    @Override
    public void updateResource(Resource resource) throws BizException {
        if (resource == null)
            return;
        validateService.validate(resource);
        List<Integer> roleIds = resourceBizService.findRoleResourceByResourceId(resource.getId());
        for (Integer roleId : roleIds) {
            resourceBizService.refreshRoleResouce(roleId);
        }
        resourceBizService.update(resource);
    }

    public void deleteResource(Integer id) {
        if (id == null) {
            return;
        }
        Resource resource = resourceBizService.getById(id);
        if (resource != null) {
            if (resource.getParentId() == 0) {// 如果是父级节点，删除下面的所有字节点
                Resource temp = new Resource();
                temp.setParentId(resource.getId());
                List<Resource> children = resourceBizService.find(temp);
                for (Resource re : children) {
                    deleteResource(re.getId());
                }
            }
            List<Integer> roleIds = resourceBizService.findRoleResourceByResourceId(id);
            for (Integer roleId : roleIds) {
                resourceBizService.refreshRoleResouce(roleId);
            }
            // 解除和角色的绑定
            resourceBizService.deleteRoleResourceByResource(id);
            // 删除资源
            resourceBizService.delete(id);
        }
    }

    @Override
    public List<Resource> findResources(Resource resource) {
        return resourceBizService.find(resource);
    }

    public List<Resource> findByRoleId(Integer roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return resourceBizService.findByRoleId(roleId);
    }

    @Override
    public Resource getResource(Integer id) {
        if (id == null)
            return null;
        return resourceBizService.getById(id);
    }

    public List<Resource> findByRoleIds(List<Integer> roleIds) {
        if (roleIds.size() == 0) {
            return Collections.emptyList();
        }
        Set<Resource> set = new HashSet<Resource>();
        for (Integer id : roleIds) {
            List<Resource> list = resourceBizService.findByRoleId(id);
            set.addAll(list);
        }
        List<Resource> list = new ArrayList<Resource>(set);
        Collections.sort(list, new Comparator<Resource>() {

            public int compare(Resource o1, Resource o2) {
                if (o1.getSort().equals(o2.getSort())) {
                    return 0;
                } else if (o1.getSort() > o2.getSort()) {
                    return 1;
                }
                return -1;
            }

        });
        return list;
    }

    @Transactional
    public void userAuthorize(User user, List<Role> roles) {
        if (user == null || roles.size() == 0) {
            return;
        }
        roleBizService.refreshUserRole(user.getId());
        roleBizService.deleteUserRole(user);
        for (Role role : roles) {
            roleBizService.addUserRole(user, role);
        }
    }

    @Transactional
    public void roleAuthorize(Role role, List<Resource> resources) {
        if (role == null || resources.size() == 0) {
            return;
        }
        // 删除角色绑定的资源
        resourceBizService.deleteRoleResourceByRoleId(role.getId());
        resourceBizService.refreshRoleResouce(role.getId());
        resourceBizService.addRoleResources(role.getId(), resources);
    }

    public void addOrg(Org org) throws BizException {
        validateService.validate(org);
        orgBizService.add(org);
    }

    public void addUser(User user) throws BizException {
        validateService.validate(user);
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        userBizService.add(user);
    }

    public void addUser(User user, List<Role> roles) throws BizException {
        addUser(user);
        userAuthorize(user, roles);
    }

    public void addRole(Role role) throws BizException {
        validateService.validate(role);
        roleBizService.add(role);
    }

    public void addRole(Role role, List<Resource> resources) throws BizException {
        addRole(role);
        roleAuthorize(role, resources);
    }


    public void deleteRole(Role role) {
        if (role == null) {
            return;
        }
        // 删除用户绑定的角色
        roleBizService.deleteUserRoleByRole(role);
        // 刷新缓存
        resourceBizService.refreshRoleResouce(role.getId());
        // 删除角色绑定的资源
        resourceBizService.deleteRoleResourceByRoleId(role.getId());
        roleBizService.delete(role.getId());
    }

    public void deleteOrg(Org org) throws BizException {
        if (org == null) {
            return;
        }
        // 判断此机构下是否有绑定的角色或用户
        int userCount = userBizService.countByOrgId(org.getId());
        int roleCount = roleBizService.countByOrgId(org.getId());
        if (userCount > 0 || roleCount > 0) {
            throw new BizException(ErrorCode.BIZ_EXCEPTION.getErrcode(), "此机构下有绑定的角色或用户,不能删除此机构");
        }
        Org temp = new Org();
        temp.setParentId(org.getId());
        List<Org> list = orgBizService.find(temp);
        for (Org co : list) {
            deleteOrg(co);
        }
        orgBizService.delete(org);
    }

    public void userUnauthorization(User user, List<Role> roles) {
        if (user == null || roles.size() == 0) {
            return;
        }
        roleBizService.deleteUserRole(user, roles);
    }

    public void userUnauthorization(User user, Role role) {
        if (user == null || role == null) {
            return;
        }
        roleBizService.refreshUserRole(user.getId());
        userUnauthorization(user, Arrays.asList(role));
    }

    public List<Role> findRoleByUserId(Integer userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return roleBizService.findByUserId(userId);
    }

    @Override
    public List<Org> findOrgs(Org org) {
        if (org == null) {
            org = new Org();
        }
        return orgBizService.find(org);
    }

    @Override
    public Org getOrg(Integer id) {
        if (id == null) {
            return null;
        }
        return orgBizService.get(id);
    }

    @Override
    public void updateOrg(Org org) throws BizException {
        if (org == null) {
            return;
        }
        orgBizService.update(org);
    }

    @Override
    public void updateRole(Role role) {
        if (role == null) {
            return;
        }
        roleBizService.update(role);
    }

    @Override
    public List<Role> findRoles(Role role) {

        return roleBizService.find(role);
    }

    @Override
    public Role getRole(Integer id) {
        if (id == null) {
            return null;
        }
        return roleBizService.get(id);
    }

    @Override
    public Page<User> find(User user) {
        if (user.getPage() == null) {
            user.setPage(new Page<>());
        }
        List<User> list = userBizService.find(user);
        user.getPage().setList(list);
        return user.getPage();
    }

    @Override
    public List<User> find2List(User user) {
        return userBizService.find(user);
    }

    @Override
    public User getUser(Integer id) {
        if (id == null) {
            return null;
        }
        return userBizService.get(id);
    }

    @Override
    public void updateUser(User user) {
        if (user == null) {
            return;
        }
        if (StringUtils.isNotBlank(user.getPassword())) {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        }
        userBizService.update(user);
    }

    @Override
    public void deleteUser(User user) {
        if (user == null) {
            return;
        }
        roleBizService.deleteUserRoleByUserId(user.getId());
        userBizService.delete(user);
    }

    @Override
    public User getUserByName(String name) throws BizException {
        if (name == null) {
            throw new BizException(ErrorCode.INVALID_PARAMETER.getErrcode(), "用户名不能为空");
        }
        return userBizService.getByName(name);
    }

    @Override
    public Page<Dict> findDictByPage(Dict dict) {
        if (dict.getPage() == null) {
            dict.setPage(new Page<Dict>());
        }
        List<Dict> list = dictBizService.find(dict);
        dict.getPage().setList(list);
        return dict.getPage();
    }

    @Override
    public List<Dict> findDictByType(String type) {
        if (StringUtils.isBlank(type)) {
            return Collections.emptyList();
        }
        Dict dict = new Dict();
        dict.setType(type);
        return dictBizService.find(dict);
    }

    @Override
    public Dict getDict(String type, String value) {
        if (StringUtils.isBlank(type) || StringUtils.isBlank(value)) {
            return null;
        }
        return dictBizService.getDict(type, value);
    }

    @Override
    public void addDict(Dict dict) throws BizException {
        validateService.validate(dict);
        if (getDict(dict.getType(), dict.getValue()) != null) {
            throw new BizException(ErrorCode.BIZ_EXCEPTION.getErrcode(), "已存在的字典项");
        }
        dictBizService.add(dict);
    }

    @Override
    public void updateDict(Dict dict) throws BizException {
        Dict dbDict = getDict(dict.getType(), dict.getValue());
        if (dbDict != null) {
            if (dbDict.getId().equals(dict.getId())) {
                dictBizService.update(dict);
            } else {
                throw new BizException(ErrorCode.BIZ_EXCEPTION.getErrcode(), "已存在的字典项");
            }
        } else {
            dictBizService.update(dict);
        }

    }

    @Override
    public Dict getDict(Integer id) {
        if (id == null) {
            return null;
        }
        return dictBizService.get(id);
    }

    @Override
    public void deleteDict(Integer id) {
        if (id == null) {
            return;
        }
        dictBizService.delete(id);
    }
}
