package com.smlyk.framework.aop;

import com.smlyk.framework.aop.intercept.YKMethodInvocation;
import com.smlyk.framework.aop.support.YKAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author yekai
 */
public class YKJdkDynamicAopProxy implements YKAopProxy, InvocationHandler{

    private YKAdvisedSupport config;

    public YKJdkDynamicAopProxy(YKAdvisedSupport config) {
        this.config = config;
    }

    /**
     * 把原生的对象传进来
     * @return
     */
    @Override
    public Object getProxy() {
        return getProxy(config.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, config.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        List<Object> interceptorsAndDynamicMethodMatchers = config.getInterceptorsAndDynamicInterceptionAdvice(method, config.getTargetClass());

        YKMethodInvocation mi = new YKMethodInvocation(proxy, method, config.getTarget(),config.getTargetClass(),args,interceptorsAndDynamicMethodMatchers);

        return mi.proceed();
    }
}
