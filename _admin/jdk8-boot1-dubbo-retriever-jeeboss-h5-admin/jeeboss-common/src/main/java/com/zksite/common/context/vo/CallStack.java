package com.zksite.common.context.vo;

import java.io.Serializable;

/**
 * 调用站
 * 
 * @author hanjieHu
 *
 */
public class CallStack implements Serializable {


    private static final long serialVersionUID = -1849162901941139L;

    /**
     * 调用时长
     */
    private long duration;

    /**
     * 调用方法
     */
    private String method;

    /**
     * 调用结果
     */
    private String code;

    public CallStack(){}


    public CallStack(long duration, String method, String code) {
        this.duration = duration;
        this.method = method;
        this.code = code;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
