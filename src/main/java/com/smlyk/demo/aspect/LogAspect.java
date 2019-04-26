package com.smlyk.demo.aspect;

import com.smlyk.framework.aop.aspect.YKJoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author yekai
 */
@Slf4j
public class LogAspect {

    /**
     * 在调用一个方法之前，执行before方法
     * @param joinPoint
     */
    public void before(YKJoinPoint joinPoint){

        log.info("Invoker before method!!!" + "\nTargetObject: " + joinPoint.getThis() + "\nArgs: "+ Arrays.toString(joinPoint.getArguments()));

    }

    /**
     * 在调用一个方法之后，执行after方法
     * @param joinPoint
     */
    public void after(YKJoinPoint joinPoint){

        log.info("Invoker after method!!!" + "\nTargetObject: "+ joinPoint.getThis() + "\nArgs: "+ Arrays.toString(joinPoint.getArguments()));

    }

    /**
     * 调用方法发送异常时执行
     * @param joinPoint
     * @param ex
     */
    public void afterThrowing(YKJoinPoint joinPoint, Throwable ex){

        log.info("出现异常："+"\nTargetObject: "+joinPoint.getThis() + "\nArgs: " + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows: "+ ex.getMessage());

    }


}
