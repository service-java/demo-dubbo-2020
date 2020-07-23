package com.zksite.web.common.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.zksite.common.aop.aspect.BaseAspect;
import com.zksite.common.constant.ErrorCode;
import com.zksite.common.exception.BizException;
import com.zksite.web.common.context.RequestContext;
import com.zksite.web.common.jwt.Payload;

@Component
@Aspect
public class LoginAspect extends BaseAspect {

    @Pointcut("@annotation(com.zksite.web.common.aop.annotation.Login) || @within(com.zksite.web.common.aop.annotation.Login)")
    public void loginPoint() {};


    @Around(value = "loginPoint()")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        Payload payload = RequestContext.getContext().getPayload();
        if (payload != null) {
            return pjp.proceed();
        }
        throw new BizException(ErrorCode.NOT_LOGIN);
    }
}
