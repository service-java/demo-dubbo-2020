package com.zksite.web.common.model;

import com.zksite.common.constant.ErrorCode;

/**
 * 接口统一返回数据模型
 * 
 * 
 */
public class ResponseModel {
    private String ret = ErrorCode.NORMAL.getErrcode();
    private String msg = ErrorCode.NORMAL.getErrm();
    private Object data = null;

    public static final ResponseModel SYSTEM_ERROR = new ResponseModel(
            ErrorCode.SYSTEM_ERROR.getErrcode(), ErrorCode.SYSTEM_ERROR.getErrm(), null);

    public static final ResponseModel INVALID_PARAMETER = new ResponseModel(
            ErrorCode.INVALID_PARAMETER.getErrcode(), ErrorCode.INVALID_PARAMETER.getErrm(), null);

    public String getRet() {
        return ret;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public ResponseModel() {}

    public ResponseModel(Object data) {
        this.data = data;
    }

    public ResponseModel(ErrorCode errcode, Object data) {
        this.ret = errcode.getErrcode();
        this.msg = errcode.getErrm();
        this.data = data;
    }

    public ResponseModel(String errcode, String errm, Object data) {
        this.ret = errcode;
        this.msg = errm;
        this.data = data;
    }
}
