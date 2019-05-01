package com.zksite.jeeboss.web.modules.aop.aspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zksite.common.aop.aspect.BaseAspect;
import com.zksite.common.constant.ErrorCode;
import com.zksite.common.exception.BizException;
import com.zksite.jeeboss.api.system.SystemApi;
import com.zksite.jeeboss.api.system.entity.Resource;
import com.zksite.jeeboss.api.system.entity.Role;
import com.zksite.jeeboss.web.modules.aop.annotation.Permission;
import com.zksite.web.common.context.RequestContext;
import com.zksite.web.common.jwt.Payload;
import com.zksite.web.common.model.ResponseModel;

@Component
@Aspect
public class PermissionAspect extends BaseAspect {

    @Pointcut("@annotation(com.zksite.jeeboss.web.modules.aop.annotation.Permission) || @within(com.zksite.jeeboss.web.modules.aop.annotation.Permission)")
    public void permissionPoint() {};

    @Autowired
    private SystemApi systemApi;

    @Around(value = "permissionPoint()")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        Payload payload = RequestContext.getContext().getPayload();
        if (payload == null) {
            throw new BizException(ErrorCode.FORBIDDEN);
        }
        Permission permission = getAnnotation(pjp, Permission.class);
        if (permission != null) {
            switch (permission.type()) {
                case ROLE:
                    List<Role> roleList = systemApi.findRoleByUserId(payload.getUid());
                    for (Role role : roleList) {
                        if (role.getPermission().equals(permission.value())) {
                            return pjp.proceed();
                        }
                    }
                    break;
                case RESOURCE:
                    List<Role> list = systemApi.findRoleByUserId(payload.getUid());
                    if (list.size() == 0) {
                        return new ResponseModel(Collections.emptyList());
                    }
                    List<Integer> roleIds = new ArrayList<Integer>(list.size());
                    for (Role role : list) {
                        roleIds.add(role.getId());
                    }
                    List<Resource> resources = systemApi.findByRoleIds(roleIds);
                    for (Resource resource : resources) {
                        if (resource.getPermission().equals(permission.value())) {
                            return pjp.proceed();
                        }
                    }
                    break;
            }
        }
        throw new BizException(ErrorCode.FORBIDDEN);
    }
}
