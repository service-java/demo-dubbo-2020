package com.zksite.jeeboss.service.system.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.zksite.jeeboss.api.system.entity.Org;

@Repository
public interface OrgDao {

    void insert(Org org);

    void delete(Org org);

    List<Org> findList(Org org);

    Org get(@Param("id") Integer id);

    void update(Org org);

}
