package com.smlyk.framework.webmvc;

import com.smlyk.framework.annotation.YKController;
import com.smlyk.framework.annotation.YKRequestMapping;
import com.smlyk.framework.context.YKApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet 只是作为一个 MVC 的启动入口
 * @author yekai
 */
@Slf4j
public class YKDispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

    private List<YKHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<YKHandlerMapping, YKHandlerAdapter> handlerAdapterMap = new HashMap<>();

    private List<YKViewResolver> viewResolvers = new ArrayList<>();

    private YKApplicationContext applicationContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        //相当于把IOC容器初始化了
        System.out.println("config.getInitParameter(LOCATION) = "+ config.getInitParameter(LOCATION));
        applicationContext = new YKApplicationContext(config.getInitParameter(LOCATION));

        //初始化九大组件
        initStrategies(applicationContext);
    }

    private void initStrategies(YKApplicationContext applicationContext) {

        //有九种策略
        // 针对于每个用户请求，都会经过一些处理的策略之后，最终才能有结果输出
        // 每种策略可以自定义干预，但是最终的结果都是一致

        // ============= 这里说的就是传说中的九大组件 ================
        //文件上传解析，如果请求类型是multipart将通过MultipartResolver进行上传文件解析
        initMultipartResolver(applicationContext);

        //本地化解析
        initLocaleResolver(applicationContext);

        //主题解析
        initThemeReslover(applicationContext);

        /**
         * YKHandlerMapping用来保存Controller中配置的RequestMapping和Method的一个对应关系，通过handlerMapping将请求映射到处理器
         */
        initHandlerMappings(applicationContext);

        /**
         * HandlerAdapter用来动态匹配Method参数，包括类转换，动态赋值。通过HandleAdapter进行多类型的参数动态匹配。
         */
        initHandlerAdapters(applicationContext);

        /**
         * 通过ViewResolver实现动态模板的解析，自己解析一套模板语言，通过ViewResolver解析逻辑视图到具体视图实现
         */
        initViewResolvers(applicationContext);

        //flash映射管理器
        initFlashMapManager(applicationContext);

    }

    private void initFlashMapManager(YKApplicationContext applicationContext) {
    }

    /**
     * 通过ViewResolver实现动态模板的解析，自己解析一套模板语言，通过ViewResolver解析逻辑视图到具体视图实现
     */
    private void initViewResolvers(YKApplicationContext applicationContext) {
        //在页面输入一个http://localhost/first.html
        //解决页面名字和模板文件关联的问题
        String templateRoot = applicationContext.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File template: templateRootDir.listFiles()) {
            //?
            viewResolvers.add(new YKViewResolver(templateRoot));
        }

    }

    /**
     * HandlerAdapter用来动态匹配Method参数，包括类转换，动态赋值。通过HandleAdapter进行多类型的参数动态匹配。
     * @param applicationContext
     */
    private void initHandlerAdapters(YKApplicationContext applicationContext) {
        //在初始化阶段，我们能做的就是将这些参数的名字或者类型按一定顺序保存下来
        //因为后面用反射调用的时候，传的形参是一个数组
        //可以通过记录这些参数的位置index，挨个从数组中填值，这样的话就和参数的顺序无关了。
        handlerMappings.stream()
                //每一个方法有一个参数列表，那么这里保存的是形参列表
                .forEach(handlerMapping -> handlerAdapterMap.put(handlerMapping, new YKHandlerAdapter()));

    }


    /**
     * 将Controller中配置的RequestMapping和Method一一对应
     * @param applicationContext
     */
    private void initHandlerMappings(YKApplicationContext applicationContext) {

        //首先从容器中取到所有的实例
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        try {
            for (String beanName : beanNames) {

                Object controller = applicationContext.getBean(beanName);
                Class<?> clazz = controller.getClass();

                if (!clazz.isAnnotationPresent(YKController.class)) continue;

                String baseUrl = "";
                if (clazz.isAnnotationPresent(YKRequestMapping.class)){
                    YKRequestMapping requestMapping = clazz.getAnnotation(YKRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                //扫描所有的public方法
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(YKRequestMapping.class)) continue;

                    YKRequestMapping requestMapping = method.getAnnotation(YKRequestMapping.class);
                    //.* 就是单个字符匹配任意次
                    String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                    Pattern pattern = Pattern.compile(regex);
                    handlerMappings.add(new YKHandlerMapping(controller,method,pattern));

                    log.info("Mapped " + regex + ", " + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initThemeReslover(YKApplicationContext applicationContext) {
    }

    private void initLocaleResolver(YKApplicationContext applicationContext) {
    }

    private void initMultipartResolver(YKApplicationContext applicationContext) {


    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            doDipatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("<font size='25' color='blue'>500 Exception</font><br/>Details:<br/>"
                    + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]","").replaceAll("\\s","\r\n")
                    + "<font color='green'><i>Copyright@SMLYK</i></font>");
            e.printStackTrace();
        }

    }

    private void doDipatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        //根据用户请求的URL来获得一个Handler
        YKHandlerMapping  handler = getHandler(req);

        if (null == handler){
            processDispatchResult(req, resp, new YKModelAndView("404"));
            return;
        }

        YKHandlerAdapter ha = getHandlerAdapter(handler);
        //这一步只是调用方法，得到返回值
        YKModelAndView mv = ha.handle(req, resp, handler);

        //这一步才是真正的输出
        processDispatchResult(req, resp, mv);
    }

    private YKHandlerAdapter getHandlerAdapter(YKHandlerMapping handler) {
        if (handlerAdapterMap.isEmpty()) return null;

        YKHandlerAdapter ha = handlerAdapterMap.get(handler);
        if (ha.supports(handler)){
            return ha;
        }
        return null;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, YKModelAndView mv) throws Exception{

        //调用viewresolver的resolveView方法
        if (null == mv) return;
        if (viewResolvers.isEmpty()) return;

        for (YKViewResolver viewResolver : viewResolvers) {
            YKView view = viewResolver.resolverViewName(mv.getViewName(), null);
            if (null != view){
                view.render(mv.getModel(), req,resp);
                //?
                return;
            }
        }
    }

    private YKHandlerMapping getHandler(HttpServletRequest req) {
        if (handlerMappings.isEmpty()) return null;

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+","/");

        for (YKHandlerMapping handler: handlerMappings){
            Matcher matcher  = handler.getPattern().matcher(url);
            if (!matcher.matches()) continue;

            return handler;
        }

        return null;
    }
}
