package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Administrator on 2017/7/3 0003.
 */
public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties properties;

    //静态块优于普通块，普通块优于构造块
    //静态代码块  类被加载时，执行且执行一次。一把用其来初始化静态变量
    static {

        String fileName = "mmall.properties";
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("配置文件读取异常",e);
        }

    }


    //PropertiesUtil为工具类，所以方法getProperty为静态方法
    public static String getProperty(String key){
        String value = properties.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return null;  //若value值为空，则返回null
        }
        return value.trim();
    }

    //重载方法getProperty
    public static String getProperty(String key,String defaultValue){
        String value = properties.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
           value = defaultValue;
        }
        return value.trim();
    }


    /*
    //普通代码块
    {

    }

    //构造代码块
    public PropertiesUtil() {

    }
    */
}
