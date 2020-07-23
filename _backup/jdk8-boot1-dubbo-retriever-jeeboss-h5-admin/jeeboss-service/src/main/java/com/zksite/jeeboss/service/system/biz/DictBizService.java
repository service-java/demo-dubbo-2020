package com.zksite.jeeboss.service.system.biz;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zksite.jeeboss.api.system.entity.Dict;
import com.zksite.jeeboss.service.system.dao.DictDao;

@Service
public class DictBizService {


    @Autowired
    private DictDao dictDao;

    public List<Dict> find(Dict dict) {
        return dictDao.findList(dict);
    }

    public Dict getDict(String type, String value) {
        return dictDao.getDict(type, value);
    }

    public void add(Dict dict) {
        dictDao.insert(dict);
    }

    public void update(Dict dict) {
        dictDao.update(dict);
    }

    public Dict get(Integer id) {
        return dictDao.get(id);
    }

    public void delete(Integer id) {
        dictDao.delete(id);
    }

}
