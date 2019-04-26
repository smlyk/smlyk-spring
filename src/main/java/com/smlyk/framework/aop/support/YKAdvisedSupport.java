package com.smlyk.framework.aop.support;

import com.smlyk.framework.aop.YKAopConfig;
import com.smlyk.framework.aop.aspect.YKAfterReturningAdvice;
import com.smlyk.framework.aop.aspect.YKAfterThrowingAdvice;
import com.smlyk.framework.aop.aspect.YKMethodAroundAdvice;
import com.smlyk.framework.aop.aspect.YKMethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AdvisedSupport本身不会提供创建代理的任何方法，专注于生成拦截器链
 * @author yekai
 */
public class YKAdvisedSupport {

    private Class targetClass;

    private Object target;

    private Pattern pointCutClassPattern;

    private transient Map<Method, List<Object>> methodCache;

    private YKAopConfig config;

    public YKAdvisedSupport(YKAopConfig config) {
        this.config = config;
    }

    public Class getTargetClass(){
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws  Exception{

        List<Object> cached = methodCache.get(method);

        //缓存中不存在，则进行下一步处理
        if (cached == null){
            Method tcMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(tcMethod);
            //存入缓存
            methodCache.put(tcMethod, cached);
        }
        return cached;
    }

    public boolean pointCutMatch(){
        return pointCutClassPattern.matcher(targetClass.toString()).matches();
    }


    private void parse() {
        //pointCut表达式
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");

        String pointCutForClass = pointCut.substring(0,pointCut.lastIndexOf("\\(") - 4);
        System.out.println("paser() ---> "+ pointCutForClass);
        pointCutClassPattern = Pattern.compile("class " +
                pointCutForClass.substring(pointCutForClass.lastIndexOf(" ")+1));

        methodCache = new HashMap<>();
        Pattern pattern = Pattern.compile(pointCut);

        try {
            Class aspectClass = Class.forName(config.getAspectClass());
            Map<String, Method> aspectMethodMap = new HashMap<>();
            Method[] methods = aspectClass.getMethods();
            for (Method method : methods) {
                aspectMethodMap.put(method.getName(), method);
            }

            //在这里得到的方法都是原生的方法
            Method[] targetClassMethods = targetClass.getMethods();
            for (Method tcMethod : targetClassMethods) {
                String tcMethodStr = tcMethod.toString();
                if (tcMethodStr.contains("throws")){
                    tcMethodStr = tcMethodStr.substring(0, tcMethodStr.indexOf("throws")).trim();
                }
                //匹配方法名是否符合切面表达式
                Matcher matcher = pattern.matcher(tcMethodStr);
                if (matcher.matches()){
                    //能满足切面规则的类，添加到AOP配置中
                    List<Object> advices = new LinkedList<>();
                    //前置通知
                    if (null != config.getAspectBefore() && !"".equals(config.getAspectBefore().trim())){
                        advices.add(new YKMethodBeforeAdvice(aspectMethodMap.get(config.getAspectBefore()), aspectClass.newInstance()));
                    }
                    //后置通知
                    if (null != config.getAspectAfter() && !"".equals(config.getAspectAfter().trim())){
                        advices.add(new YKAfterReturningAdvice(aspectMethodMap.get(config.getAspectAfter()), aspectClass.newInstance()));
                    }
                    //异常通知
                    if (null != config.getAspectAfterThrow() && !"".equals(config.getAspectAfterThrow().trim())){
                        YKAfterThrowingAdvice afterThrowingAdvice = new YKAfterThrowingAdvice(aspectMethodMap.get(config.getAspectAfterThrow()), aspectClass.newInstance());
                        afterThrowingAdvice.setThrowingName(config.getAspectAfterThrowingName());
                        advices.add(afterThrowingAdvice);
                    }

                    //环绕通知
                    if (null != config.getAspectAround() && !"".equals(config.getAspectAround().trim())){
                        advices.add(new YKMethodAroundAdvice(aspectMethodMap.get(config.getAspectAround()), aspectClass.newInstance()));
                    }

                    methodCache.put(tcMethod, advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
