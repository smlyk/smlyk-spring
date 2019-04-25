package com.smlyk.framework.beans.support;

import com.smlyk.framework.beans.config.YKBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 对配置文件进行查找，读取、解析
 * @author yekai
 */
public class YKBeanDefinitionReader {

    private List<String> registyBeanClasses = new ArrayList<String>();

    private Properties config = new Properties();

    /**
     * 固定配置文件中的 key，相对于 xml 的规范
     */
    private final String SCAN_PACKAGE = "scanPackage";

    public YKBeanDefinitionReader(String... locations){

        //通过URL定位找到其所对应的文件，然后转换为文件流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));

        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {

        //转换为文件路径，实际上就是把.替换为/就 OK 了
        //.（点号）也是一个正则表达式，它匹配任何一个字符。\. 匹配 "."
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));

        //test类测试临时改成这样...
//        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.","/"));

        File classPath = new File(url.getFile());

        for (File file : classPath.listFiles()) {
            if (file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else {
                if (!file.getName().endsWith(".class")) continue;

                String className = scanPackage + "." + file.getName().replace(".class", "");
                registyBeanClasses.add(className);
            }
        }

    }


    /**
     * 把配置文件中扫描到的所有配置信息转换为YKBeanDefinition对象，以便于之后IOC操作方便
     * @return
     */
    public List<YKBeanDefinition> loadBeanDefinitions(){

        List<YKBeanDefinition> result = new ArrayList<YKBeanDefinition>();

        try {
            for (String className : registyBeanClasses){

                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) continue;

                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()),beanClass.getName()));

                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i: interfaces) {

                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 首字母小写
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {

        char[] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的 ASCII 码相差 32，
        // 而且大写字母的 ASCII 码要小于小写字母的 ASCII 码
        //在 Java 中，对 char 做算学运算，实际上就是对 ASCII 码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }


    /**
     * 把每一个配置信息解析成一个Beandefinition
     * @param factoryBeanName
     * @param beanClassName
     * @return
     */
    private YKBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName){

        YKBeanDefinition beanDefinition = new YKBeanDefinition();

        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(beanClassName);

        return beanDefinition;
    }

    public Properties getConfig(){
        return this.config;
    }

}
