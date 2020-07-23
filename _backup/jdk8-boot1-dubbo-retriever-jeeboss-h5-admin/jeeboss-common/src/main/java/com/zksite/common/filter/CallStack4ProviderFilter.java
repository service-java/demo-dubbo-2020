package com.zksite.common.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.fastjson.JSON;
import com.zksite.common.constant.ErrorCode;
import com.zksite.common.context.CallStackContext;
import com.zksite.common.context.vo.CallStack;
import com.zksite.common.context.vo.Stack;
import com.zksite.common.utils.CallStackUtils;

public class CallStack4ProviderFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallStack4ProviderFilter.class);
    private static final String CALL_STACK = "call_stack";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        StopWatch watch = new StopWatch();
        Stack stack = acceptRequest(invoker, invocation);
        String code = ErrorCode.NORMAL.getErrcode();
        try {
            watch.start();
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
            CallStackUtils.printStack(stack, LOGGER, watch,code);
            CallStackContext.removeContext();
        }

    }

    private Stack acceptRequest(Invoker<?> invoker, Invocation invocation) {
        String attachment = invocation.getAttachment(CALL_STACK);
        Stack stack = null;
        if (StringUtils.isNotBlank(attachment)) {
            stack = JSON.parseObject(attachment, Stack.class);
        } else {
            stack = new Stack();
        }
        CallStackContext.getContext().setStack(stack);
        return stack;
    }

}
