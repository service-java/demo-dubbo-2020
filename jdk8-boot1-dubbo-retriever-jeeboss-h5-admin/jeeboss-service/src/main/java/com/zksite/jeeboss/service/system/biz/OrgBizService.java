package com.zksite.jeeboss.service.system.biz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zksite.jeeboss.api.system.entity.Org;
import com.zksite.jeeboss.service.system.dao.OrgDao;

@Service
public class OrgBizService {

    @Autowired
    private OrgDao orgDao;

    public void add(Org org) {
        orgDao.insert(org);
    }

    public void delete(Org org) {
        orgDao.delete(org);
    }

    public List<Org> find(Org org) {
        return orgDao.findList(org);
    }

    public Org get(Integer id) {
        return orgDao.get(id);
    }

    public void update(Org org) {
        orgDao.update(org);
    }

}
