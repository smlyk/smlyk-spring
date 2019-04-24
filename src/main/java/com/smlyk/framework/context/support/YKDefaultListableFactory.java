package com.smlyk.framework.context.support;

import com.smlyk.framework.beans.config.YKBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yekai
 */
public class YKDefaultListableFactory extends YKAbstractApplicationContext{

    /**
     * 存储注册信息的 BeanDefinition
     */
    protected final Map<String, YKBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, YKBeanDefinition>();


}
