package com.smlyk.framework.aop;

import com.smlyk.framework.aop.support.YKAdvisedSupport;

/**
 * @author yekai
 */
public class YKCglibAopProxy implements YKAopProxy{

    private YKAdvisedSupport config;

    public YKCglibAopProxy(YKAdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
