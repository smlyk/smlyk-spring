package com.smlyk.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author yekai
 */
public abstract class YKAbstractAspectJAdvice implements YKAdvice{

    private Method aspectMethod;

    private Object aspectTarget;

    public YKAbstractAspectJAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    public Object invokeAdviceMethod(YKJoinPoint joinPoint, Object returnValue, Throwable ex) throws Throwable{

        Class<?>[] parameterTypes = aspectMethod.getParameterTypes();
        if (null == parameterTypes || parameterTypes.length == 0){
            return aspectMethod.invoke(aspectTarget);
        }else {
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i<parameterTypes.length; i++){
                if (parameterTypes[i] == YKJoinPoint.class){
                    args[i] = joinPoint;
                }else if (parameterTypes[i] == Throwable.class){
                    args[i] = ex;
                }else if (parameterTypes[i] == Object.class){
                    args[i] = returnValue;
                }
            }
            return aspectMethod.invoke(aspectTarget,args);
        }
    }

}
