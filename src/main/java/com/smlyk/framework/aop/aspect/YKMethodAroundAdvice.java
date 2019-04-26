package com.smlyk.framework.aop.aspect;

import com.smlyk.framework.aop.intercept.YKMethodInterceptor;
import com.smlyk.framework.aop.intercept.YKMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author yekai
 */
public class YKMethodAroundAdvice extends YKAbstractAspectJAdvice implements YKAdvice, YKMethodInterceptor{

    private YKJoinPoint joinPoint;

    public YKMethodAroundAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }


    public void around(Method method, Object[] args, Object target) throws Throwable{

        invokeAdviceMethod(joinPoint,null,null);
    }


    @Override
    public Object invoke(YKMethodInvocation mi) throws Throwable {
        this.joinPoint = mi;
        around(mi.getMethod(), mi.getArguments(), mi.getThis());
        Object result = mi.proceed();
        around(mi.getMethod(), mi.getArguments(), mi.getThis());
        return result;
    }
}
