package com.smlyk.framework.aop.aspect;

import com.smlyk.framework.aop.intercept.YKMethodInterceptor;
import com.smlyk.framework.aop.intercept.YKMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author yekai
 */
public class YKAfterReturningAdvice extends YKAbstractAspectJAdvice implements YKAdvice,YKMethodInterceptor{

    private YKJoinPoint joinPoint;

    public YKAfterReturningAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable{

        invokeAdviceMethod(joinPoint, returnValue, null);

    }

    @Override
    public Object invoke(YKMethodInvocation mi) throws Throwable {
        Object returnValue = mi.proceed();
        this.joinPoint = mi;
        afterReturning(returnValue, mi.getMethod(), mi.getArguments(), mi.getThis());
        return returnValue;
    }
}
