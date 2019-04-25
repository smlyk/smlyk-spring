package com.smlyk.framework.webmvc;

import com.smlyk.framework.annotation.YKRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 专人干专事
 * @author yekai
 */
public class YKHandlerAdapter {

    public boolean supports(Object handler){

        return handler instanceof YKHandlerMapping;
    }

    public YKModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception{

        YKHandlerMapping handlerMapping = (YKHandlerMapping) handler;

        //每一个方法有一个参数列表，那么这里保存的是形参列表
        Map<String, Integer> paramMapping = new HashMap<>();

        //这里只是出来了命名参数
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();

        for (int i = 0; i<pa.length; i++){
            for (Annotation a: pa[i]) {
                if (a instanceof YKRequestParam){
                    String paramName = ((YKRequestParam) a).value();
                    if (!"".equals(paramName.trim())){
                        paramMapping.put(paramName, i);
                    }
                }
            }
        }

        //根据用户请求的参数信息，跟method中的参数信息进行动态匹配
        //resp传进来的目的只有一个：只是为了将其值赋值给方法参数。

        //只有当用户传过来的ModelAndView为空的时候，才会new一个默认的

        //1.要准备好这个方法的形参列表
        //方法重载：形参的决定因素:参数的个数、参数的类型、参数顺序、方法的名字
        //只处理Request和Response
        Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i< parameterTypes.length; i++){
            Class<?> paramType = parameterTypes[i];
            if (HttpServletRequest.class == paramType || HttpServletResponse.class == paramType){
                paramMapping.put(paramType.getName(), i);
            }
        }

        //2.拿到自定义命名参数所在位置
        //用户通过URl传过来的参数列表
        Map<String, String[]> reqParameterMap = req.getParameterMap();

        //3.构造实参列表
        Object[] paramValues = new Object[parameterTypes.length];
        reqParameterMap.forEach((reqParamName, reqParamValues) ->{

            String value = Arrays.toString(reqParamValues).replaceAll("\\[|\\]","").replaceAll("\\s","");
            if (!paramMapping.containsKey(reqParamName)) return;

            int index = paramMapping.get(reqParamName);

            //因为页面上传过来的值都是String类型的，而在方法中定义的类型是千变万化的
            //要针对我们传过来的参数进行类型转换
            paramValues[index] = caseStringValue(value, parameterTypes[index]);
        });

        if (paramMapping.containsKey(HttpServletRequest.class.getName())){
            int reqIndex =  paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (paramMapping.containsKey(HttpServletResponse.class.getName())){
            int respIndex = paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        //4.从handler中取出controller、method，然后利用反射机制进行调用
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (null == result) return null;

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == YKModelAndView.class;
        if (isModelAndView)  return (YKModelAndView)result;

        return null;
    }

    private Object caseStringValue(String value, Class<?> clazz) {

        if (clazz == String.class){
            return value;
        }else if (clazz == Integer.class){
            return Integer.valueOf(value);
        }else if (clazz == int.class){
            return Integer.valueOf(value).intValue();
        }else {
            return null;
        }

    }


}
