package com.smlyk.framework.core;

/**
 * 单例工厂的顶层设计
 * @author yekai
 */
public interface YKBeanFactory {

    /**
     * 根据 beanName 从 IOC 容器中获得一个实例 Bean
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;


    Object getBean(Class<?> beanClass) throws Exception;

}
