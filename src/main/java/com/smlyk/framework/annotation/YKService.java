package com.smlyk.framework.annotation;

import java.lang.annotation.*;

/**
 * 业务逻辑,注入接口
 * @author yekai
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface YKService {

    String value() default "";

}
