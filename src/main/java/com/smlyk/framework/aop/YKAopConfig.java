package com.smlyk.framework.aop;

import lombok.Data;

/**
 * @author yekai
 */
@Data
public class YKAopConfig {

    private String pointCut;

    private String aspectBefore;

    private String aspectAfter;

    private String aspectClass;

    private String aspectAfterThrow;

    private String aspectAfterThrowingName;

    private String aspectAround;


}
