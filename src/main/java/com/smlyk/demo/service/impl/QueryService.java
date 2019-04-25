package com.smlyk.demo.service.impl;

import com.smlyk.demo.service.IQueryServcie;
import com.smlyk.framework.annotation.YKService;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yekai
 */
@YKService
@Slf4j
public class QueryService implements IQueryServcie{

    @Override
    public String query(String name) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());

        String json = "{\"name\":\""+name +"\",\"time\":\""+ time +"\"}";
        log.info("这是在业务方法中打印的：" + json);

        return json;
    }
}
