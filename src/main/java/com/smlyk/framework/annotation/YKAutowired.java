package com.smlyk.framework.annotation;

import java.lang.annotation.*;

/**
 * 自动注入
 * @author yekai
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YKAutowired {

    String value() default "";
}
