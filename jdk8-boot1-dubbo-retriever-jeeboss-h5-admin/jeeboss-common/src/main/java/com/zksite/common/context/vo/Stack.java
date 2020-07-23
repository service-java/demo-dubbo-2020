package com.zksite.common.context.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Stack implements Serializable {

    private static final long serialVersionUID = 8974437192236116212L;

    private String reqId;

    private List<CallStack> list = new ArrayList<CallStack>();

    public String getReqId() {
        if (reqId == null) {
            reqId = UUID.randomUUID().toString().replaceAll("-", "");
        }
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public List<CallStack> getList() {
        return list;
    }

    public void setList(List<CallStack> list) {
        this.list = list;
    }


}
