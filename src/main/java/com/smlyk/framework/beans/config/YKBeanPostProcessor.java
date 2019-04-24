package com.smlyk.framework.beans.config;

/**
 * @author yekai
 */
public class YKBeanPostProcessor {

    /**
     * Bean的初始化前提供的回调入口
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception{
        return bean;
    }

    /**
     * Bean的初始化之后提供的回调入口
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception{
        return bean;
    }



}
