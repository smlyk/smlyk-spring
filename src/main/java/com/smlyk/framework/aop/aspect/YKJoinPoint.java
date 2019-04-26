package com.smlyk.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author yekai
 */
public interface YKJoinPoint {

    Method getMethod();

    Object[] getArguments();

    Object getThis();

}
