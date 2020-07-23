package com.zksite.web.common.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zksite.web.common.jwt.Payload;

public class RequestContext {

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Payload payload;

    private static final ThreadLocal<RequestContext> LOCAL = new ThreadLocal<RequestContext>() {
        @Override
        protected RequestContext initialValue() {
            return new RequestContext();
        }
    };

    public void clear() {
        LOCAL.remove();
    }
    
    

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public static RequestContext getContext() {
        return LOCAL.get();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

}
