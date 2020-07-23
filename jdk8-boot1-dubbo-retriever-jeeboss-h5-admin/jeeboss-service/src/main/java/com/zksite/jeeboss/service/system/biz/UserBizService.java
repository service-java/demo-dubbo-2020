package com.zksite.jeeboss.service.system.biz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zksite.jeeboss.api.system.entity.User;
import com.zksite.jeeboss.service.system.dao.UserDao;

@Service
public class UserBizService {

    @Autowired
    private UserDao userDao;

    public void add(User user) {
        userDao.insert(user);
    }

    public int countByOrgId(Integer orgId) {
        return userDao.countByOrgId(orgId);
    }

    public List<User> find(User user) {
        return userDao.findList(user);
    }

    public User get(Integer id) {
        return userDao.get(id);
    }

    public void update(User user) {
        userDao.update(user);
    }

    public void delete(User user) {
        userDao.delete(user);
    }

    public User getByName(String name) {
        return userDao.getByName(name);
    }

}
