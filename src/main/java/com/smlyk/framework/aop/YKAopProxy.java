package com.smlyk.framework.aop;

/**
 * 默认用JDK动态代理
 * @author yekai
 */
public interface YKAopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);

}
