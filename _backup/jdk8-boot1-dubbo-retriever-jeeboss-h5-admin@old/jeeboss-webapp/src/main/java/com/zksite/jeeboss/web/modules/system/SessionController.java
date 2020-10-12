package com.zksite.jeeboss.web.modules.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.zksite.common.constant.ErrorCode;
import com.zksite.common.mybatis.Page;
import com.zksite.jeeboss.api.system.SystemApi;
import com.zksite.jeeboss.api.system.entity.Resource;
import com.zksite.jeeboss.api.system.entity.Role;
import com.zksite.jeeboss.api.system.entity.User;
import com.zksite.web.common.aop.annotation.Login;
import com.zksite.web.common.context.RequestContext;
import com.zksite.web.common.http.session.Session;
import com.zksite.web.common.http.session.SessionManager;
import com.zksite.web.common.jwt.JWTUtil;
import com.zksite.web.common.jwt.Payload;
import com.zksite.web.common.jwt.PayloadRepository;
import com.zksite.web.common.model.ResponseModel;
import com.zksite.web.common.monitor.annotation.Histogram;
import com.zksite.web.common.monitor.annotation.Meter;

@RestController
@RequestMapping
public class SessionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionController.class);

    @Autowired
    private SystemApi systemApi;

    @Autowired(required = false)
    private SessionManager sessionManager;

    @Autowired
    private PayloadRepository payloadRepository;

    private static final String USER_ROLES = "user_roles";

    private static final String USER_RESOURCES = "user_resources";

    @RequestMapping(value = "/session", method = RequestMethod.POST)
    public ResponseModel login(User user) {
        try {
            User dbuser = systemApi.getUserByName(user.getName());
            if (dbuser == null) {
                return new ResponseModel(ErrorCode.LOGIN_FAIL, null);
            } else {
                if (dbuser.getPassword()
                        .equals(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()))) {
                    Payload payload = new Payload();
                    payload.setCreateTime(new Date());
                    payload.setName(dbuser.getName());
                    payload.setNickName(dbuser.getNickname());
                    payload.setUid(dbuser.getId());
                    payload.setLastAccessedTime(new Date());
                    String token = JWT.create().withSubject(JSON.toJSONString(payload))
                            .sign(JWTUtil.getJWTAlgorithm());
                    List<Role> userRoles = systemApi.findRoleByUserId(dbuser.getId());
                    List<Integer> roleIds = new ArrayList<Integer>(userRoles.size());
                    for (Role role : userRoles) {
                        roleIds.add(role.getId());
                    }
                    List<Resource> resources = systemApi.findByRoleIds(roleIds);
                    if (sessionManager != null) {
                        Session session = sessionManager.createSession(dbuser.getId().toString());
                        session.setAttribute(USER_ROLES, userRoles);
                        session.setAttribute(USER_RESOURCES, resources);
                    } else {
                        HttpSession session = RequestContext.getContext().getRequest().getSession();
                        session.setAttribute(USER_ROLES, userRoles);
                        RequestContext.getContext().getRequest().getSession()
                                .setAttribute(USER_RESOURCES, resources);
                    }
                    payload.setToken(token);
                    payloadRepository.save(payload);
                    return new ResponseModel(payload);
                } else {
                    return new ResponseModel(ErrorCode.LOGIN_FAIL, null);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    @Meter
    @Histogram
    @Login
    @RequestMapping(value = "sessions", method = RequestMethod.GET)
    public ResponseModel findSession(Page<Payload> page) {
        Page<Payload> lpage = payloadRepository.find(page);
        return new ResponseModel(lpage);
    }


    @Login
    @RequestMapping(value = "session/{id}", method = RequestMethod.DELETE)
    public ResponseModel deleteSession(@PathVariable("id") String id) {
        if (StringUtils.isBlank(id)) {
            return ResponseModel.INVALID_PARAMETER;
        }
        payloadRepository.delete(id);
        return new ResponseModel();
    }

}
