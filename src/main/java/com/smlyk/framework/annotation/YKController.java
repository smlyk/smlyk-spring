package com.smlyk.framework.annotation;

import java.lang.annotation.*;

/**
 * 页面交互
 * @author yekai
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YKController {

    String value() default "";
}
