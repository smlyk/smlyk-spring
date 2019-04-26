package com.smlyk.framework.aop.intercept;

/**
 * @author yekai
 */
public interface YKMethodInterceptor {

    Object invoke(YKMethodInvocation mi) throws Throwable;
}
