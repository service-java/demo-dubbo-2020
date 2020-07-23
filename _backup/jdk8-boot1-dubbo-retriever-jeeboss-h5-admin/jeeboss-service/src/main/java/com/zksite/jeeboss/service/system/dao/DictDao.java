package com.zksite.jeeboss.service.system.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.zksite.jeeboss.api.system.entity.Dict;

@Repository
public interface DictDao {

    List<Dict> findList(Dict dict);

    @Select("SELECT * FROM sys_dict WHERE `type`=#{type} AND `value`=#{value}")
    Dict getDict(@Param("type") String type, @Param("value") String value);

    void insert(Dict dict);

    void update(Dict dict);

    Dict get(Integer id);

    void delete(@Param("id") Integer id);

}
