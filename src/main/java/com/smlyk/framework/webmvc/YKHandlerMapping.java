package com.smlyk.framework.webmvc;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author yekai
 */
@Data
public class YKHandlerMapping {

    private Object controller;

    private Method method;

    /**
     * url的封装
     */
    private Pattern pattern;

    public YKHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

}
