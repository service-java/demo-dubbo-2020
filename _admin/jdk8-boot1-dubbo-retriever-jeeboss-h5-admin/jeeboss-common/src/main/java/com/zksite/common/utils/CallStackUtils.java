package com.zksite.common.utils;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;

import com.zksite.common.context.vo.CallStack;
import com.zksite.common.context.vo.Stack;

public class CallStackUtils {


    public static void printStack(Stack stack, Logger logger, StopWatch stopWatch, String code) {
        StringBuilder builder = new StringBuilder("reqId=");
        builder.append(stack.getReqId());
        builder.append(",code=").append(code);
        builder.append(",duration=").append(stopWatch.getTime());
        builder.append(",").append("stack=[");
        int count = 0;
        for (CallStack callStack : stack.getList()) {
            if (count == stack.getList().size() - 1) {
                builder.append(callStack.getMethod());
            } else {
                builder.append(callStack.getMethod()).append("=>");
            }
            count++;
        }

        builder.append("]");
        logger.info(builder.toString());
    }

}
