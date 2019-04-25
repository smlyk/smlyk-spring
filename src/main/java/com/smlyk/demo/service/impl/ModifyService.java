package com.smlyk.demo.service.impl;

import com.smlyk.demo.service.IModifyService;
import com.smlyk.framework.annotation.YKService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yekai
 */
@YKService
@Slf4j
public class ModifyService implements IModifyService{
    @Override
    public String add(String name, String addr) {
        return "modifyService add,name=" + name +", addr="+addr;
    }

    @Override
    public String edit(Integer id, String name) {
        return "modifyService edit,id=" + id +", name="+name;
    }

    @Override
    public String remove(Integer id) {
        return "modifyService remove,id=" + id;
    }
}
