package com.smlyk.framework.webmvc;

import java.util.Map;

/**
 * @author yekai
 */
public class YKModelAndView {

    private String viewName;

    private Map<String, ?> model;

    public YKModelAndView(String viewName){
        this(viewName, null);
    }

    public YKModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
