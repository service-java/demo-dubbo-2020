package com.zksite.web.common.jwt;

import com.zksite.common.mybatis.Page;

public interface PayloadRepository {

    void save(Payload payload);

    Payload get(String id);

    void delete(String id);

    void update(Payload payload);

    Page<Payload> find(Page<Payload> page);
}
