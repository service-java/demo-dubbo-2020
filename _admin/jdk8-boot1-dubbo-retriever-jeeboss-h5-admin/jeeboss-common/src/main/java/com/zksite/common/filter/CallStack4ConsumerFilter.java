package com.zksite.common.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSON;
import com.zksite.common.constant.ErrorCode;
import com.zksite.common.context.CallStackContext;
import com.zksite.common.context.vo.CallStack;
import com.zksite.common.context.vo.Stack;
import com.zksite.common.utils.CallStackUtils;

public class CallStack4ConsumerFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallStack4ConsumerFilter.class);

    private static final String CALL_STACK = "call_stack";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Stack stack = getStack();
        StopWatch watch = new StopWatch();
        String code = ErrorCode.NORMAL.getErrcode();
        try {
            watch.start();
            RpcContext.getContext().setAttachment(CALL_STACK, JSON.toJSONString(stack));
            Result result = invoker.invoke(invocation);
            watch.stop();
            if (result != null && result.hasException()) {
                code = ErrorCode.UNKNOWN_ERROR.getErrcode();
            }
            stack.getList().add(new CallStack(watch.getTime(),
                    invoker.getInterface().getSimpleName() + "." + invocation.getMethodName(),
                    code));
            return result;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } finally {
            if (!CallStackContext.getContext().isMark()) {
                CallStackUtils.printStack(stack, LOGGER, watch, code);
                CallStackContext.removeContext();
            }
        }
    }

    private Stack getStack() {
        String attachment = RpcContext.getContext().getAttachment(CALL_STACK);
        Stack stack = null;
        if (StringUtils.isNotBlank(attachment)) {
            stack = JSON.parseObject(attachment, Stack.class);
        } else if (CallStackContext.getContext().getStack() != null) {
            stack = CallStackContext.getContext().getStack();
        } else {// 自身发起请求
            stack = new Stack();
        }
        CallStackContext.getContext().setStack(stack);
        return stack;
    }

}
