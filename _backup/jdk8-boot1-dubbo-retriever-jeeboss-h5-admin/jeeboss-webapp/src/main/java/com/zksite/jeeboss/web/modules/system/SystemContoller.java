package com.zksite.jeeboss.web.modules.system;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zksite.common.constant.ErrorCode;
import com.zksite.common.exception.BizException;
import com.zksite.common.mybatis.Page;
import com.zksite.common.utils.upyun.UpYunClient;
import com.zksite.jeeboss.api.system.SystemApi;
import com.zksite.jeeboss.api.system.entity.Dict;
import com.zksite.jeeboss.api.system.entity.Org;
import com.zksite.jeeboss.api.system.entity.Resource;
import com.zksite.jeeboss.api.system.entity.Role;
import com.zksite.jeeboss.api.system.entity.User;
import com.zksite.jeeboss.web.modules.aop.annotation.Permission;
import com.zksite.web.common.aop.annotation.Login;
import com.zksite.web.common.model.ResponseModel;
import com.zksite.web.common.monitor.annotation.Histogram;
import com.zksite.web.common.monitor.annotation.Meter;

@RestController
@RequestMapping("/sys")
@Login
public class SystemContoller {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemContoller.class);

    @Autowired
    private SystemApi systemApi;

    /**
     * 获取用户的角色
     * 
     * @param userId
     * @return
     */
    @Meter
    @Histogram
    @RequestMapping(value = "/user/{userId}/roles", method = RequestMethod.GET)
    public ResponseModel findUserRoles(@PathVariable("userId") Integer userId) {
        if (userId == null) {
            return new ResponseModel(ErrorCode.INVALID_PARAMETER, null);
        }
        try {
            List<Role> list = systemApi.findRoleByUserId(userId);
            return new ResponseModel(list);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseModel(ErrorCode.SYSTEM_ERROR.getErrcode(), e.getMessage(), null);
        }
    }

    /**
     * 获取用户的资源
     * 
     * @param userId
     * @return
     */
    @Meter
    @Histogram
    @RequestMapping(value = "/user/{userId}/resources", method = RequestMethod.GET)
    public ResponseModel findUserResources(@PathVariable("userId") Integer userId) {
        if (userId == null) {
            return new ResponseModel(ErrorCode.INVALID_PARAMETER, null);
        }
        try {
            List<Role> list = systemApi.findRoleByUserId(userId);
            if (list.size() == 0) {
                return new ResponseModel(Collections.emptyList());
            }
            List<Integer> roleIds = new ArrayList<Integer>(list.size());
            for (Role role : list) {
                roleIds.add(role.getId());
            }
            List<Resource> resources = systemApi.findByRoleIds(roleIds);
            return new ResponseModel(resources);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseModel(ErrorCode.SYSTEM_ERROR.getErrcode(), e.getMessage(), null);
        }

    }

    /**
     * 获取资源列表
     * 
     * @param resource
     * @return
     */
    @Meter
    @Histogram
    @RequestMapping(value = "/resources", method = RequestMethod.GET)
    public ResponseModel findResources(Resource resource) {
        List<Resource> list = systemApi.findResources(resource);
        return new ResponseModel(list);
    }

    /**
     * 删除资源
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/resource/{id}", method = RequestMethod.DELETE)
    public ResponseModel deleteResource(@PathVariable("id") Integer id) {
        systemApi.deleteResource(id);
        return new ResponseModel();
    }

    @RequestMapping(value = "/resource/{id}", method = RequestMethod.GET)
    public ResponseModel getResource(@PathVariable("id") Integer id) {
        if (id == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        return new ResponseModel(systemApi.getResource(id));
    }

    /**
     * 创建一个资源
     * 
     * @param resource
     * @return
     */
    @RequestMapping(value = "/resource", method = RequestMethod.POST)
    public ResponseModel addResource(Resource resource) {
        if (resource == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        try {
            systemApi.addResource(resource);
        } catch (BizException e) {
            LOGGER.info(e.getMessage(), e);
            return new ResponseModel(e.getErrcode(), e.getErrm(), null);
        }
        return new ResponseModel();
    }

    /**
     * 更新一个资源
     * 
     * @param id
     * @param resource
     * @return
     */
    @RequestMapping(value = "/resource/{id}", method = RequestMethod.PUT)
    public ResponseModel updateResource(@PathVariable("id") Integer id,
            @RequestBody Resource resource) {
        if (resource == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        try {
            systemApi.updateResource(resource);
            return new ResponseModel();
        } catch (BizException e) {
            return new ResponseModel(e.getErrcode(), e.getErrm(), null);
        }
    }

    /**
     * 获取机构列表
     * 
     * @param org
     * @return
     */
    @Meter
    @Histogram
    @RequestMapping(value = "/orgs", method = RequestMethod.GET)
    public ResponseModel findOrgs(Org org) {
        List<Org> list = systemApi.findOrgs(org);
        return new ResponseModel(list);
    }

    /**
     * 添加一个机构
     * 
     * @param org
     * @return
     */
    @RequestMapping(value = "/org", method = RequestMethod.POST)
    public ResponseModel addOrg(Org org) {
        if (org == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        try {
            systemApi.addOrg(org);
            return new ResponseModel();
        } catch (BizException e) {
            LOGGER.info(e.getMessage(), e);
            return new ResponseModel(e.getErrcode(), e.getErrm(), null);
        }
    }

    /**
     * 获取一个机构
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/org/{id}", method = RequestMethod.GET)
    public ResponseModel getOrg(@PathVariable("id") Integer id) {
        if (id == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        Org org = systemApi.getOrg(id);
        return new ResponseModel(org);
    }

    /**
     * 更新机构
     * 
     * @param id
     * @param org
     * @return
     */
    @RequestMapping(value = "/org/{id}", method = RequestMethod.PUT)
    public ResponseModel updateOrg(@PathVariable("id") Integer id, @RequestBody Org org) {
        if (org == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        try {
            systemApi.updateOrg(org);
            return new ResponseModel();
        } catch (BizException e) {
            LOGGER.error(e.getErrm(), e);
            return new ResponseModel(e.getErrcode(), e.getErrm(), null);
        }
    }

    /**
     * 删除机构
     * 
     * @param id
     * @param org
     * @return
     */
    @RequestMapping(value = "/org/{id}", method = RequestMethod.DELETE)
    public ResponseModel deleteOrg(@PathVariable("id") Integer id, Org org) {
        if (id == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        try {
            systemApi.deleteOrg(org);
            return new ResponseModel();
        } catch (BizException e) {
            LOGGER.error(e.getErrm(), e);
            return new ResponseModel(e.getErrcode(), e.getErrm(), null);
        }
    }

    /**
     * 添加角色
     * 
     * @param role
     * @return
     */
    @RequestMapping(value = "/role", method = RequestMethod.POST)
    public ResponseModel addRole(Role role) {
        if (role == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        try {
            systemApi.addRole(role);
            return new ResponseModel();
        } catch (BizException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseModel(e.getErrcode(), e.getErrm(), null);
        }
    }

    /**
     * 更新角色
     * 
     * @param id
     * @param role
     * @return
     */
    @RequestMapping(value = "/role/{id}", method = RequestMethod.PUT)
    public ResponseModel updateRole(@PathVariable("id") Integer id, @RequestBody Role role) {
        if (id == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        systemApi.updateRole(role);
        return new ResponseModel();
    }

    /**
     * 查找角色列表
     * 
     * @param role
     * @return
     */
    @Meter
    @Histogram
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public ResponseModel findRoles(Role role) {
        List<Role> list = systemApi.findRoles(role);
        return new ResponseModel(list);
    }

    /**
     * 获取角色
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/role/{id}", method = RequestMethod.GET)
    public ResponseModel getRole(@PathVariable("id") Integer id) {
        if (id == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        Role role = systemApi.getRole(id);
        return new ResponseModel(role);
    }

    /**
     * 删除角色
     * 
     * @param id
     * @param role
     * @return
     */
    @RequestMapping(value = "/role/{id}", method = RequestMethod.DELETE)
    public ResponseModel deleteRole(@PathVariable("id") Integer id, Role role) {
        if (id == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        systemApi.deleteRole(role);
        return new ResponseModel();
    }

    /**
     * 获取角色资源
     * 
     * @param roleId
     * @return
     */
    @RequestMapping(value = "/role/{roleId}/resources", method = RequestMethod.GET)
    public ResponseModel findRoleResources(@PathVariable("roleId") Integer roleId) {
        if (roleId == null) {
            return new ResponseModel(Collections.emptyList());
        }
        List<Resource> list = systemApi.findByRoleId(roleId);
        return new ResponseModel(list);
    }

    /**
     * 角色授权
     * 
     * @param roleId
     * @param resources
     * @return
     */
    @RequestMapping(value = "/role/{roleId}/resources", method = RequestMethod.PUT)
    public ResponseModel authorizeRole(@PathVariable("roleId") Integer roleId,
            @RequestBody ArrayList<Resource> resources) {// ,
        if (roleId == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        Role role = new Role();
        role.setId(roleId);
        systemApi.roleAuthorize(role, resources);
        return new ResponseModel();
    }

    /**
     * 添加用户
     * 
     * @param user
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseModel addUser(User user) {
        if (user == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        try {
            systemApi.addUser(user);
            return new ResponseModel();
        } catch (BizException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseModel(e.getErrcode(), e.getErrm(), null);
        }
    }

    /**
     * 查找用户
     * 
     * @param user
     * @return
     */
    @Meter
    @Histogram
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseModel findUser(User user) {
        if (user.getPage() != null) {
            Page<User> page = systemApi.find(user);
            return new ResponseModel(page);
        } else {
            List<User> list = systemApi.find2List(user);
            return new ResponseModel(list);
        }
    }

    /**
     * 获取用户
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResponseModel getUser(@PathVariable("userId") Integer id) {
        if (id == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        User user = systemApi.getUser(id);
        return new ResponseModel(user);
    }

    /**
     * 更新用户
     * 
     * @param id
     * @param user
     * @return
     */
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.PUT)
    public ResponseModel updateUser(@PathVariable("userId") Integer id, @RequestBody User user) {
        if (id == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        systemApi.updateUser(user);
        return new ResponseModel();
    }

    /**
     * 删除用户
     * 
     * @param id
     * @param user
     * @return
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseModel deleteUser(@PathVariable("id") Integer id, User user) {
        if (id == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        systemApi.deleteUser(user);
        return new ResponseModel();
    }

    /**
     * 用户授权
     * 
     * @param userId
     * @param roles
     * @return
     */
    @Permission(value = "resource:user:authorize")
    @RequestMapping(value = "/user/{userId}/roles", method = RequestMethod.PUT)
    public ResponseModel authorizeUser(@PathVariable("userId") Integer userId,
            @RequestBody ArrayList<Role> roles) {
        if (userId == null) {
            return ResponseModel.INVALID_PARAMETER;
        }
        User user = new User();
        user.setId(userId);
        systemApi.userAuthorize(user, roles);
        return new ResponseModel();
    }

    /**
     * 单文件上传
     * 
     * @param request
     * @return
     * @throws IOException
     * @throws FileUploadException
     */
    @RequestMapping("/singleUpload")
    public ResponseModel upload(@RequestParam("file") MultipartFile file)
            throws IOException, FileUploadException {
        String path = null;
        String name = file.getOriginalFilename();
        String ext = name.substring(name.lastIndexOf("."), name.length());
        String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;
        path = UpYunClient.upload(newFileName, file.getBytes());
        return new ResponseModel(path);

    }

    /**
     * 分页获取字典列表
     * 
     * @param dict
     * @return
     */
    @RequestMapping(value = "/dicts", method = RequestMethod.GET)
    public ResponseModel findDict(Dict dict) {
        Page<Dict> page = systemApi.findDictByPage(dict);
        return new ResponseModel(page);
    }

    /**
     * 通过字典类型获取字典列表
     * 
     * @param type
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/dicts/{type}", method = RequestMethod.GET)
    public ResponseModel findDictsByType(@PathVariable("type") String type) throws BizException {
        if (StringUtils.isBlank(type)) {
            throw new BizException("字典type不能为空");
        }
        List<Dict> list = systemApi.findDictByType(type);
        return new ResponseModel(list);
    }

    /**
     * 添加字典
     * 
     * @param dict
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/dict", method = RequestMethod.POST)
    public ResponseModel addDict(Dict dict) throws BizException {
        systemApi.addDict(dict);
        return new ResponseModel();
    }

    @RequestMapping(value = "/dict", method = RequestMethod.PUT)
    public ResponseModel updateDict(Dict dict) throws BizException {
        systemApi.updateDict(dict);
        return new ResponseModel();
    }

    @RequestMapping(value = "/dict/{type}/{value}")
    public ResponseModel getDict(@PathVariable("type") String type,
            @PathVariable("value") String value) {
        Dict dict = systemApi.getDict(type, value);
        return new ResponseModel(dict);
    }

    @RequestMapping(value = "/dict/{id}")
    public ResponseModel getDictById(@PathVariable("id") Integer id) {
        Dict dict = systemApi.getDict(id);
        return new ResponseModel(dict);
    }

    @RequestMapping(value = "/dict/{id}", method = RequestMethod.DELETE)
    public ResponseModel deleteDict(@PathVariable("id") Integer id) {
        systemApi.deleteDict(id);
        return new ResponseModel();
    }

}
