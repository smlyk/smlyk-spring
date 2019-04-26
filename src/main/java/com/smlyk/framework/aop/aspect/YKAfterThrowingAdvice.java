package com.smlyk.framework.aop.aspect;

import com.smlyk.framework.aop.intercept.YKMethodInterceptor;
import com.smlyk.framework.aop.intercept.YKMethodInvocation;

import java.lang.reflect.Method;

/**
 * @author yekai
 */
public class YKAfterThrowingAdvice extends YKAbstractAspectJAdvice implements YKAdvice, YKMethodInterceptor{

    private String throwingName;

    private YKMethodInvocation mi;

    public YKAfterThrowingAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public void setThrowingName(String throwingName) {
        this.throwingName = throwingName;
    }

    @Override
    public Object invoke(YKMethodInvocation mi) throws Throwable {

        try {
            return mi.proceed();
        } catch (Throwable throwable) {
            invokeAdviceMethod(mi, null, throwable.getCause());
            throw throwable;
        }
    }

}
