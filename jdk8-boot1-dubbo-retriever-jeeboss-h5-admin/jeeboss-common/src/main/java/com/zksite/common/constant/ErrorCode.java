package com.zksite.common.constant;

import com.alibaba.fastjson.JSONObject;

/**
 * 通用错误码
 * 
 * @author Cobe
 *
 */
public enum ErrorCode {

    NORMAL("0", "成功"), //
    BIZ_EXCEPTION("100", "发生业务异常"), //
    INVALID_PARAMETER("101", "非法参数"), //
    INVALID_API_SIGN("102", "网络超时"), //
    FREQUENCY_LIMIT("103", "您的请求过于频繁，请稍后再提交。"), //
    USER_INPUT_ERROR("201", "用户输入错误"), //
    USER_INPUT_ERROR_TOO_LONG("20101", "输入内容超过长度限制"), //
    NOT_LOGIN("401", "当前操作需登录后才能继续"), //
    LOGIN_FAIL("401.1", "登录失败，用户名或密码有误"), //
    FORBIDDEN("403", "你没有权限访问此资源"), //
    RESOURCE_NOT_FOUND("404", "您访问的资源不存在。"), //
    UNKNOWN_ERROR("500", "服务器离家出走，请稍后再试"), //
    EXTERNAL_SYSTEM_ERROR("509", "外部系统异常"), //
    SYSTEM_ERROR("900", "系统异常"), // 偏向于基础框架、服务器系统异常
    UPDATE_NOTE("999", "该APP版本过旧，请更新到最新版本");//

    private String errcode;
    private String errm;

    ErrorCode(String errcode, String errm) {
        this.errcode = errcode;
        this.errm = errm;
    }

    public String getErrcode() {
        return errcode;
    }

    public String getErrm() {
        return errm;
    }

    public String toJSONString(String errcode, String errm) {
        JSONObject json = new JSONObject();
        json.put("ret", errcode);
        json.put("msg", errm);
        return json.toJSONString();
    }

    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("ret", errcode);
        json.put("msg", errm);
        return json.toJSONString();
    }

}
