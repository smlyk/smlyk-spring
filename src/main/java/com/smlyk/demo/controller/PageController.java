package com.smlyk.demo.controller;

import com.smlyk.demo.service.IQueryServcie;
import com.smlyk.framework.annotation.YKAutowired;
import com.smlyk.framework.annotation.YKController;
import com.smlyk.framework.annotation.YKRequestMapping;
import com.smlyk.framework.annotation.YKRequestParam;
import com.smlyk.framework.webmvc.YKModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yekai
 */
@YKController
@YKRequestMapping
public class PageController {

    @YKAutowired
    private IQueryServcie queryServcie;

    @YKRequestMapping("first.html")
    public YKModelAndView query(@YKRequestParam("username") String username){
        String result = queryServcie.query(username);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("username", username);
        model.put("data", result);
        model.put("token", "123456");
        return new YKModelAndView("first.html",model);

    }
}
