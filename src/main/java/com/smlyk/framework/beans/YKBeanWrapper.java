package com.smlyk.framework.beans;

/**
 * @author yekai
 */
public class YKBeanWrapper {

    private Object wrappedInstance;

    private Class<?> wrappedClass;

    public YKBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    // 返回代理以后的 Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass() {
        return wrappedInstance.getClass();
    }
}
