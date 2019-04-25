package com.smlyk.framework.webmvc;

import java.io.File;
import java.util.Locale;

/**
 * 1.将一个静态文件变为一个动态文件
 * 2.根据用户传入参数不同，产生不同结果
 * 最终输出字符串，交给Response输出
 * @author yekai
 */
public class YKViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;

    private String viewName;

    public YKViewResolver(String templateRoot){
        System.out.println("templateRoot" +templateRoot);
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getPath();
        this.templateRootDir = new File(templateRootPath);
    }

    public YKView resolverViewName(String viewName, Locale locale) throws Exception{
        this.viewName = viewName;
        if (null == viewName || "".equals(viewName.trim())) return null;

        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));

        return new YKView(templateFile);
    }

    public String getViewName() {
        return viewName;
    }
}
