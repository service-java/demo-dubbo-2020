package com.zksite.jeeboss.service.system.biz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zksite.common.cache.annotation.CacheParam;
import com.zksite.common.cache.annotation.Cacheable;
import com.zksite.jeeboss.api.system.entity.Resource;
import com.zksite.jeeboss.service.system.dao.ResourceDao;

@Service
public class ResourceBizService {

    @Autowired
    private ResourceDao resourceDao;

    public void add(Resource resource) {
        resourceDao.insert(resource);
    }

    public void deleteRoleResourceByResource(Integer id) {
        resourceDao.deleteRoleResourceByResource(id);
    }

    public List<Integer> findRoleResourceByResourceId(Integer id) {
        return resourceDao.findRoleResourceByResourceId(id);
    }

    public void delete(Integer id) {
        resourceDao.delete(id);
    }

    @Cacheable(key = "system:resource:role:", timeout = 5 * 60, deserializeClass = Resource.class)
    public List<Resource> findByRoleId(@CacheParam Integer roleId) {
        return resourceDao.findByRoleId(roleId);
    }

    @Cacheable(key = "system:resource:role:", remove = true)
    public void deleteRoleResourceByRoleId(Integer roleId) {
        resourceDao.deleteRoleResourceByRoleId(roleId);
    }

    @Cacheable(key = "system:resource:role:", remove = true)
    public void addRoleResources(@CacheParam Integer roleId, List<Resource> resources) {
        resourceDao.addRoleResources(roleId, resources);
    }

    @Cacheable(key = "system:resource:role:", remove = true)
    public void refreshRoleResouce(@CacheParam Integer roleId) {

    }

    public List<Resource> find(Resource resource) {
        return resourceDao.findList(resource);
    }

    public Resource getById(Integer id) {
        return resourceDao.get(id);
    }

    public void update(Resource resource) {
        resourceDao.update(resource);
    }

}
