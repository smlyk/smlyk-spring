package com.smlyk.framework.context;

import com.smlyk.framework.annotation.YKAutowired;
import com.smlyk.framework.annotation.YKController;
import com.smlyk.framework.annotation.YKService;
import com.smlyk.framework.beans.YKBeanWrapper;
import com.smlyk.framework.beans.config.YKBeanDefinition;
import com.smlyk.framework.beans.config.YKBeanPostProcessor;
import com.smlyk.framework.beans.support.YKBeanDefinitionReader;
import com.smlyk.framework.context.support.YKDefaultListableFactory;
import com.smlyk.framework.core.YKBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 按之前源码分析的套路，IOC、DI、MVC、AOP
 * @author yekai
 */
public class YKApplicationContext extends YKDefaultListableFactory implements YKBeanFactory{

    private String[] configLocations;

    private YKBeanDefinitionReader reader;

    /**
     * 用来保存注册式单例的容器
     */
    private Map<String, Object> singletonBeanCacheMap = new ConcurrentHashMap<>();

    /**
     * 用来存储所有的被代理过的对象
     */
    private Map<String, YKBeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();


    public YKApplicationContext(String... configLocations){

        this.configLocations = configLocations;

        try {
            refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        //1.定位,定位配置文件
        reader = new YKBeanDefinitionReader(this.configLocations);

        //2.加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<YKBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3.注册，把配置信息放到容器里面（伪IOC容器）
        doRegisterBeanDefinition(beanDefinitions);

        //4.把不是延时加载的类，提前初始化
        doAutowired();

    }

    /**
     * 只处理非延时加载的情况
     */
    private void doAutowired() {

        beanDefinitionMap.forEach((beanName, beanDefinition) -> {

            if (!beanDefinition.isLazyInit()){
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void doRegisterBeanDefinition(List<YKBeanDefinition> beanDefinitions) throws Exception {

        for (YKBeanDefinition beanDefinition : beanDefinitions){

            if (beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The ＇" + beanDefinition.getFactoryBeanName() + "' is existed !!!");
            }

            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);

        }

    }


    /**
     *  依赖注入，从这里开始，通过读取 BeanDefinition 中的信息
     *  然后，通过反射机制创建一个实例并返回
     *  Spring 做法是，不会把最原始的对象放出去，会用一个 BeanWrapper 来进行一次包装
     *  装饰器模式：
     *  1、保留原来的 OOP 关系
     *  2、我需要对它进行扩展，增强（为了以后 AOP 打基础）
     * @param beanName
     * @return
     * @throws Exception
     */
    @Override
    public Object getBean(String beanName) throws Exception {

        YKBeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

        try {
            //生成事件通知
            YKBeanPostProcessor beanPostProcessor = new YKBeanPostProcessor();

            Object instance = instantiateBean(beanDefinition);
            if (null == instance) return null;

            //在实例初始化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            YKBeanWrapper beanWrapper = new YKBeanWrapper(instance);
            beanWrapperMap.put(beanName, beanWrapper);

            //在实例初始化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            populateBean(beanName, instance);



        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * DI
     * @param beanName
     * @param instance
     */
    private void populateBean(String beanName, Object instance) {

        Class<?> clazz = instance.getClass();
        //如果类没有YKController也没有YKService注解直接返回
        if (!(clazz.isAnnotationPresent(YKController.class) || clazz.isAnnotationPresent(YKService.class))) return;

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            //如果成员变量没有YKAutowired注解就跳过
            if (!field.isAnnotationPresent(YKAutowired.class)) continue;

            YKAutowired autowired = field.getAnnotation(YKAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)){
                //包名+类名 com.smlyk.service.HelloServiceImpl
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);

            try {
                //将指定对象变量上此 Field 对象表示的字段设置为指定的新值。
                field.set(instance, beanWrapperMap.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 传一个BeanDefinition,就返回一个实例Bean
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(YKBeanDefinition beanDefinition) {

        Object instance = null;
        String className = beanDefinition.getBeanClassName();

        try {
            if (singletonBeanCacheMap.containsKey(className)){
                instance = singletonBeanCacheMap.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                singletonBeanCacheMap.put(className, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return null;
    }

    public String[] getBeanDefinitionNames(){

        return beanDefinitionMap.keySet().toArray(new String[beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){

        return beanDefinitionMap.size();
    }

    public Properties getConfig(){

        return reader.getConfig();

    }

}
