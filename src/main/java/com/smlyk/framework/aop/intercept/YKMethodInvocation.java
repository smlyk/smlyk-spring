package com.smlyk.framework.aop.intercept;

import com.smlyk.framework.aop.aspect.YKJoinPoint;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author yekai
 */
public class YKMethodInvocation implements YKJoinPoint{

    private Object proxy;

    private Method method;

    private Object target;

    private Class<?> targetClass;

    private Object[] arguments;

    private List<Object> interceptorsAndDynamicMethodmathces;

    private int currentInterceptorIndex = -1;

    public YKMethodInvocation(Object proxy, Method method, Object target, Class<?> targetClass, Object[] arguments, List<Object> interceptorsAndDynamicMethodmathces) {
        this.proxy = proxy;
        this.method = method;
        this.target = target;
        this.targetClass = targetClass;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodmathces = interceptorsAndDynamicMethodmathces;
    }

    public Object proceed() throws Throwable{

        if (currentInterceptorIndex == interceptorsAndDynamicMethodmathces.size() -1){
            return method.invoke(target, arguments);
        }

        Object interceptorOrInterceptionAdvice = interceptorsAndDynamicMethodmathces.get(++currentInterceptorIndex);
        if (interceptorOrInterceptionAdvice instanceof YKMethodInterceptor){
            YKMethodInterceptor mi = (YKMethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        }else {
            return proceed();
        }
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Object getThis() {
        return this.target;
    }
}
