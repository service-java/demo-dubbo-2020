package com.zksite.web.common.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.util.NestedServletException;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zksite.common.constant.ErrorCode;
import com.zksite.common.context.CallStackContext;
import com.zksite.common.context.vo.CallStack;
import com.zksite.common.context.vo.Stack;
import com.zksite.common.exception.BizException;
import com.zksite.common.utils.CallStackUtils;
import com.zksite.web.common.context.RequestContext;
import com.zksite.web.common.http.HttpServletRequestWrapper;
import com.zksite.web.common.http.HttpSessionWrapper;
import com.zksite.web.common.http.session.SessionManager;
import com.zksite.web.common.jwt.JWTUtil;
import com.zksite.web.common.jwt.Payload;
import com.zksite.web.common.jwt.PayloadRepository;
import com.zksite.web.common.model.ResponseModel;

/**
 * 全局http请求拦截<br>
 * 必须设置为web应用的第一个拦截器
 * 
 * @author hanjieHu
 *
 */

@WebFilter
public class GlobalServletFilter implements Filter {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private Environment environment;

    private static final String JWT_EXPIRED_TIME = "jwt_expired_time";

    @Autowired
    private PayloadRepository payloadRepository;

    private static final String TRACE = "trace";

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalServletFilter.class);

    public void destroy() {}

    public void init(FilterConfig filterConfig) throws ServletException {}

    private static final String payload_head = "payload";

    public void doFilter(ServletRequest req, ServletResponse rsp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) rsp;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String code = ErrorCode.NORMAL.getErrcode();
        CallStack callStack = new CallStack(0, request.getRequestURI(), code);
        String method = request.getMethod();
        if (method.equals("OPTIONS") || method.equals("options")) {// options方法委托给下个filter处理
            chain.doFilter(request, response);
            return;
        }
        try {
            initCallStack(request, response, callStack);// 初始化调用链
            if (sessionManager != null) {
                HttpServletRequestWrapper httpServletRequestWrapper =
                        new HttpServletRequestWrapper(request, sessionManager);
                initRequestContext(httpServletRequestWrapper, response);
                chain.doFilter(httpServletRequestWrapper, response);
            } else {
                initRequestContext(request, response);
                chain.doFilter(request, response);
            }
            stopWatch.stop();
        } catch (Throwable e) {
            code = ErrorCode.UNKNOWN_ERROR.getErrcode();
            processException(e, request, response);
        } finally {
            comminSession();
            callStack.setDuration(stopWatch.getTime());
            callStack.setCode(code);
            CallStackUtils.printStack(CallStackContext.getContext().getStack(), LOGGER, stopWatch,
                    code);
            CallStackContext.removeContext();
            RequestContext.getContext().clear();// 清除绑定在当前线程的requestContext
        }
    }

    private void comminSession() {
        Payload payload = RequestContext.getContext().getPayload();
        if (payload != null) {
            Payload dbpayload = payloadRepository.get(payload.getUid().toString());
            if (dbpayload != null) {
                HttpSession session = RequestContext.getContext().getRequest().getSession();
                if (session instanceof HttpSessionWrapper) {
                    sessionManager.comminSession();
                }
                payload.setLastAccessedTime(new Date());
                payloadRepository.update(payload);
            }
        }
    }

    /**
     * 处理异常
     * 
     * @param e
     * @param request
     * @param response
     */
    private void processException(Throwable e, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            if (e instanceof NestedServletException) {// SPRING MVC异常
                NestedServletException nestedServletException = (NestedServletException) e;
                Throwable throwable = nestedServletException.getCause();
                if (throwable instanceof UndeclaredThrowableException) {
                    UndeclaredThrowableException undeclaredThrowableException =
                            (UndeclaredThrowableException) throwable;
                    Throwable undeclaredThrowable =
                            undeclaredThrowableException.getUndeclaredThrowable();
                    if (undeclaredThrowable instanceof BizException) {// 业务异常
                        BizException be = (BizException) undeclaredThrowable;
                        ResponseModel model =
                                new ResponseModel(be.getErrcode(), be.getErrm(), null);
                        responseErrorMessage(model, response);
                    } else {
                        LOGGER.error(e.getMessage(), e);
                        ResponseModel model = new ResponseModel(ErrorCode.SYSTEM_ERROR, null);
                        responseErrorMessage(model, response);
                    }
                } else if (throwable instanceof BizException) {
                    BizException be = (BizException) throwable;
                    ResponseModel model = new ResponseModel(be.getErrcode(), be.getErrm(), null);
                    responseErrorMessage(model, response);
                } else {
                    LOGGER.error(e.getMessage(), e);
                    ResponseModel model = new ResponseModel(ErrorCode.SYSTEM_ERROR, null);
                    responseErrorMessage(model, response);
                }
            } else {
                LOGGER.error(e.getMessage(), e);
                ResponseModel model = new ResponseModel(ErrorCode.UNKNOWN_ERROR, null);
                responseErrorMessage(model, response);
            }
        } catch (Exception ex) {
            ex.fillInStackTrace();
            LOGGER.error(ex.getMessage(), e);
            ResponseModel model = new ResponseModel(ErrorCode.UNKNOWN_ERROR, null);
            responseErrorMessage(model, response);
        }

    }


    private void responseErrorMessage(ResponseModel model, HttpServletResponse response) {
        PrintWriter writer;
        try {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            writer = response.getWriter();
            writer.write(JSON.toJSONString(model));
            writer.flush();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    private void initCallStack(HttpServletRequest request, HttpServletResponse response,
            CallStack callStack) {
        String header = request.getHeader(TRACE);
        if (StringUtils.isNotBlank(header)) {
            try {
                Stack stack = JSON.parseObject(header, Stack.class);
                CallStackContext context = CallStackContext.getContext();
                context.mark();
                context.setStack(stack);
                context.push(callStack);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        } else {
            Stack stack = new Stack();
            CallStackContext.getContext().mark();
            CallStackContext.getContext().setStack(stack);
            CallStackContext.getContext().push(callStack);
        }
    }

    /**
     * 初始化请求上下文
     * 
     * @param request
     * @param response
     */
    private void initRequestContext(HttpServletRequest request, HttpServletResponse response) {
        RequestContext context = RequestContext.getContext();
        String token = request.getHeader(payload_head);
        if (StringUtils.isNotBlank(token)) {

            try {
                DecodedJWT decodedJWT = JWTUtil.getJWTVerifier().verify(token);
                Payload payload = JSON.parseObject(decodedJWT.getSubject(), Payload.class);
                if (payload != null) {
                    Payload dbPayload = payloadRepository.get(payload.getUid().toString());
                    if (dbPayload != null) {
                        long lassAccessedTime = dbPayload.getLastAccessedTime().getTime() / 1000;
                        long nowTime = new Date().getTime() / 1000;
                        String expiredTime = environment.getProperty(JWT_EXPIRED_TIME);
                        int timeLong = 3600;
                        if (StringUtils.isNotBlank(expiredTime)
                                && NumberUtils.isNumber(expiredTime)) {
                            timeLong = Integer.valueOf(expiredTime);
                        }
                        if ((nowTime - lassAccessedTime) < timeLong) {// 只有当前token不过期才放入当前请求上下文
                            context.setPayload(dbPayload);
                        }
                    }
                }

            } catch (JWTVerificationException e) {
                LOGGER.debug(e.getMessage(), e);
            }
        }
        context.setRequest(request);
        context.setResponse(response);
    }

}
