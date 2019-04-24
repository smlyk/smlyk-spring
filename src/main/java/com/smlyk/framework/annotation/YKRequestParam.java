package com.smlyk.framework.annotation;

import java.lang.annotation.*;

/**
 * 请求参数映射
 * @author yekai
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YKRequestParam {

    String value() default "";

}
