package com.zksite.common.context;

import java.util.List;

import com.zksite.common.context.vo.CallStack;
import com.zksite.common.context.vo.Stack;

public class CallStackContext {

    private Stack stack;

    private boolean mark;

    private static final ThreadLocal<CallStackContext> LOCAL = new ThreadLocal<CallStackContext>() {
        @Override
        protected CallStackContext initialValue() {
            return new CallStackContext();
        }
    };

    public static CallStackContext getContext() {
        return LOCAL.get();
    }

    public static void removeContext() {
        LOCAL.remove();
    }

    public Stack getStack() {
        return stack;
    }

    public void mark() {
        mark = true;
    }

    public boolean isMark() {
        return mark;
    }

    public Stack setStack(Stack stack) {
        this.stack = stack;
        return this.stack;
    }

    public List<CallStack> push(CallStack item) {
        List<CallStack> list = stack.getList();
        list.add(item);
        return list;
    }



}
