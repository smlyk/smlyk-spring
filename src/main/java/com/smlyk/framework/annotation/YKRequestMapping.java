package com.smlyk.framework.annotation;

import java.lang.annotation.*;

/**
 * 请求 url
 * @author yekai
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YKRequestMapping {

    String value() default "";

}
