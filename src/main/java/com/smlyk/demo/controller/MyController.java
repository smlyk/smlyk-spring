package com.smlyk.demo.controller;

import com.smlyk.demo.service.IModifyService;
import com.smlyk.demo.service.IQueryServcie;
import com.smlyk.framework.annotation.YKAutowired;
import com.smlyk.framework.annotation.YKController;
import com.smlyk.framework.annotation.YKRequestMapping;
import com.smlyk.framework.annotation.YKRequestParam;
import com.smlyk.framework.webmvc.YKModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yekai
 */
@YKController
@YKRequestMapping("/web")
public class MyController {

    @YKAutowired
    private IQueryServcie queryServcie;

    @YKAutowired
    private IModifyService modifyService;

    @YKRequestMapping("query")
    public YKModelAndView query(HttpServletRequest request, HttpServletResponse response, @YKRequestParam("name") String name){

        String result = queryServcie.query(name);
        return out(response, result);
    }

    @YKRequestMapping("add")
    public YKModelAndView add(HttpServletRequest request,HttpServletResponse response,
                              @YKRequestParam("name") String name,@YKRequestParam("addr") String addr){

        String result = modifyService.add(name,addr);
        return out(response,result);
    }
    @YKRequestMapping("remove")
    public YKModelAndView remove(HttpServletRequest request,HttpServletResponse response,
                                 @YKRequestParam("id") Integer id){

        String result = modifyService.remove(id);
        return out(response,result);
    }

    @YKRequestMapping("edit")
    public YKModelAndView edit(HttpServletRequest request,HttpServletResponse response,
                               @YKRequestParam("id") Integer id,
                               @YKRequestParam("name") String name){

        String result = modifyService.edit(id,name);
        return out(response,result);
    }


    private YKModelAndView out(HttpServletResponse resp,String str){
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
